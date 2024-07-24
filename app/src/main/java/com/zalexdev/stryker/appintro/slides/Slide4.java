package com.zalexdev.stryker.appintro.slides;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import lombok.SneakyThrows;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.zalexdev.stryker.R;

import com.zalexdev.stryker.objects.Interface;
import com.zalexdev.stryker.su.SuUtils;
import com.zalexdev.stryker.utils.Preferences;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Slide4 extends Fragment {

    private Activity activity;
    private Context context;
    private MaterialButton next;
    private int bypass = 0;
    private boolean stop = false;



    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide5, container, false);
        activity = getActivity();
        context = getContext();
        Preferences preferences = Preferences.getInstance();

        TextView title = view.findViewById(R.id.slide_title);

        ImageView wifi = view.findViewById(R.id.wifi_item);
        ImageView ble = view.findViewById(R.id.bluetooth_item);
        ImageView ap = view.findViewById(R.id.wifi_tethering_item);
        next = view.findViewById(R.id.next);
        startCheck(s -> {
            switch (s) {
                case "wifi_true":
                    activity.runOnUiThread(() -> toggleViewBackground(wifi, true));
                    break;
                case "wifi_false":
                    activity.runOnUiThread(() -> toggleViewBackground(wifi, false));
                    break;
                case "ble_true":
                    activity.runOnUiThread(() -> toggleViewBackground(ble, true));
                    break;
                case "ble_false":
                    activity.runOnUiThread(() -> toggleViewBackground(ble, false));
                    break;
                case "ap_true":
                    activity.runOnUiThread(() -> toggleViewBackground(ap, true));
                    break;
                case "ap_false":
                    activity.runOnUiThread(() -> toggleViewBackground(ap, false));
                    break;
            }
        });
        wifi.setOnClickListener(v -> {
            bypass++;
            if (bypass == 5) {
                next.setEnabled(true);
                next.setText(getString(R.string.next));
                next.setIcon(ContextCompat.getDrawable(context, R.drawable.next));
                stop = true;
            }
        });
         next.setOnClickListener(v -> SuUtils.getInterfaces(activity, 3, interfaces -> {
             Log.d("Slide4", "accept: " + interfaces);
             preferences.setInterfaces(Interface.getInterfaceNames(interfaces));
             preferences.setBoolean("first_run", true);
             Preferences.getInstance().replaceFragment(new Slide6Final(), "Slide6");
         }));
        return view;
    }

    public void toggleViewBackground(View view, boolean isOn) {
        if (isOn) {
            view.setBackgroundResource(R.drawable.circle);
        } else {
            view.setBackgroundResource(R.drawable.circle_off);
        }
    }


    private void startCheck(Consumer<String> callback){

        new Thread(() -> {
            while (!stop){
            boolean wifi = isWifiEnabled();
            boolean ble = isBluetoothEnabled();
            boolean ap = isHotspotEnabled((WifiManager) context.getSystemService(Context.WIFI_SERVICE));

            callback.accept("wifi_"+wifi);
            callback.accept("ble_"+ble);
            callback.accept("ap_"+ap);
            activity.runOnUiThread(() -> {
                if (wifi && ble && ap) {
                    next.setEnabled(true);
                    next.setText(getString(R.string.next));
                    next.setIcon(ContextCompat.getDrawable(context, R.drawable.next));
                }else{
                    next.setEnabled(false);
                }
            });

            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}}

        }).start();
    }

    private boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    private boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    public  boolean isHotspotEnabled(final WifiManager manager) {
        try {
            final Method method =
                    manager.getClass().getDeclaredMethod("isWifiApEnabled");
            return (Boolean) method.invoke(manager);
        } catch (Exception ignored) {

        }
        return false;
    }

}