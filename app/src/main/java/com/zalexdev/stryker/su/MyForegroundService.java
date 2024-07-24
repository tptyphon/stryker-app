package com.zalexdev.stryker.su;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.zalexdev.stryker.MainActivity;
import com.zalexdev.stryker.R;
import com.zalexdev.stryker.objects.NucleiHost;
import com.zalexdev.stryker.objects.NucleiVuln;
import com.zalexdev.stryker.utils.ExecutorBuilder;
import com.zalexdev.stryker.utils.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;

public class MyForegroundService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "MyForegroundService";

    @Getter
    private static MyForegroundService instance;
    private final IBinder binder = new LocalBinder();
    private ExecutorService executorService;
    private final AtomicInteger taskCounter = new AtomicInteger(0);

    public static final String ACTION_RUN_FUNCTION = "com.example.RUN_FUNCTION";
    public static final String EXTRA_PARAMETER = "parameter";
    public NucleiHost nucleiHost;
    public Notification notification;
    public ExecutorBuilder executorBuilder;
    public List<ExecutorBuilder> executorBuilders = new ArrayList<>();

    private final Map<String, NotificationCompat.Builder> activeNotifications = new HashMap<>();
    private NotificationManager notificationManager;

    @Setter
    private static OnServiceStartedListener onServiceStartedListener;

    public interface OnServiceStartedListener {
        void onServiceStarted(MyForegroundService service);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        executorService = Executors.newCachedThreadPool();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (onServiceStartedListener != null) {
            onServiceStartedListener.onServiceStarted(this);
        }
    }

    public class LocalBinder extends Binder {
        MyForegroundService getService() {
            return MyForegroundService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP_SCAN".equals(intent.getAction())) {
            String scanId = intent.getStringExtra("scanId");
            if (scanId != null) {
                stopScan(scanId);
            }
        } else {
            createMainServiceNotification();
        }
        return START_STICKY;
    }

    private void createMainServiceNotification() {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Nuclei Scan Service")
                .setContentText("Service is running")
                .setSmallIcon(R.drawable.web)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        executorService.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(serviceChannel);
    }

    private String createNotificationChannel(String channelId) {
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Nuclei Scan " + channelId,
                NotificationManager.IMPORTANCE_LOW
        );
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    public static void startService(Context context) {
        Intent serviceIntent = new Intent(context, MyForegroundService.class);
        context.startForegroundService(serviceIntent);
    }

    public static void stopService(Context context) {
        Intent serviceIntent = new Intent(context, MyForegroundService.class);
        context.stopService(serviceIntent);
    }

    public void startScan(Context context, String parameter) {
        nucleiHost = new NucleiHost(parameter);
        Preferences preferences = Preferences.getInstance();
        preferences.saveNucleiHost(nucleiHost);

        String notificationChannelId = createNotificationChannel(nucleiHost.getRandom_id());

        Intent stopIntent = new Intent(this, MyForegroundService.class);
        stopIntent.setAction("STOP_SCAN");
        stopIntent.putExtra("scanId", nucleiHost.getRandom_id());
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, notificationChannelId)
                .setContentTitle("Nuclei Scan: " + parameter)
                .setContentText("Scan in progress...")
                .setSmallIcon(R.drawable.web)
                .addAction(R.drawable.close, "Stop", stopPendingIntent)
                .setProgress(100, 0, true);

        activeNotifications.put(nucleiHost.getRandom_id(), notificationBuilder);

        int notificationId = nucleiHost.getRandom_id().hashCode();
        notificationManager.notify(notificationId, notificationBuilder.build());

        executorBuilder = new ExecutorBuilder();
        executorBuilder.setChroot(true);
        executorBuilder.setNoLog(true);

        executorBuilder.setNotificationId(nucleiHost.getRandom_id());
        executorBuilder.setCommand("./nuclei -u " + parameter + " -j -stats");
        executorBuilder.setOutput(s -> {

            if (s.contains("{") && s.contains("}")) {
                NucleiVuln v = NucleiVuln.fromJson(s);
                preferences.addNucleiVuln(nucleiHost.getRandom_id(), v);
                //updateNotificationProgress(nucleiHost.getRandom_id(), "Vulnerabilities found: " + preferences.getNucleiHostById(nucleiHost.getRandom_id()).getVulnerabilities().size());
            }
        });
        executorBuilder.setOnFinished(strings -> {
            Log.e("Scan finished!!!", "...");
            updateNotificationProgress(nucleiHost.getRandom_id(), "Scan completed", true);
        });
        executorBuilder.easyExecute();
        executorBuilders.add(executorBuilder);
    }

    private void updateNotificationProgress(String scanId, String statusText) {
        updateNotificationProgress(scanId, statusText, false);
    }

    private void updateNotificationProgress(String scanId, String statusText, boolean finished) {
        NotificationCompat.Builder builder = activeNotifications.get(scanId);
        if (builder != null) {
            builder.setContentText(statusText);
            if (finished) {
                builder.setProgress(0, 0, false);
            }
            notificationManager.notify(scanId.hashCode(), builder.build());
        }
    }

    public void stopScan(String scanId) {
        for (ExecutorBuilder executorBuilder : executorBuilders) {
            if (executorBuilder.getNotificationId().equals(scanId)) {
                executorBuilder.kill();
            }
        }
        updateNotificationProgress(scanId, "Scan stopped", true);
        activeNotifications.remove(scanId);

    }
}