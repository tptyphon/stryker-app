package com.zalexdev.stryker.managers;

import static com.zalexdev.stryker.su.SuUtils.getInterfaces;

import android.app.Activity;

import com.zalexdev.stryker.objects.Interface;
import com.zalexdev.stryker.utils.ExecutorBuilder;
import com.zalexdev.stryker.utils.Preferences;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Timer;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.SneakyThrows;

public class WlanManager {
    @Getter
    private static WlanManager instance;



    public static void initInstance() {
        if (instance == null) {
            instance = new WlanManager();
        }
    }


    public boolean isMonitorModeEnabled(String iface) {
        ArrayList<Interface> interfaces = getInterfaces(3);
        for (Interface i : interfaces) {
            if (i.getName().equals(iface)) {
                return true;
            }
        }
        return false;
    }

    public boolean isApModeEnabled(String iface) {
        ArrayList<Interface> interfaces = getInterfaces(2);
        for (Interface i : interfaces) {
            if (i.getName().equals(iface)) {
                return true;
            }
        }
        return false;
    }

    public boolean isManagedModeEnabled(String iface) {
        ArrayList<Interface> interfaces = getInterfaces(1);
        for (Interface i : interfaces) {
            if (i.getName().equals(iface)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInterfaceAvailable(String iface) {
        ArrayList<Interface> interfaces = getInterfaces(0);
        for (Interface i : interfaces) {
            if (i.getName().equals(iface)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInterfaceAvailable() {
        return !getInterfaces(0).isEmpty();
    }

    @SneakyThrows
    public void setupInterfaceWatcher(String iface, Consumer<Boolean> isAvailable) {
        Activity activity = Preferences.getInstance().getActivity();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(() -> {
                    try {
                        if (isInterfaceAvailable(iface)) {
                            isAvailable.accept(true);
                        } else {
                            isAvailable.accept(false);
                            timer.cancel();
                        }
                    } catch (Exception e) {
                        ACRA.getErrorReporter().handleSilentException(e);
                    }
                });
            }
        }, 0, 5000);
    }

    public void setupInterfaceWatcher(Consumer<String> adapterAdded, Consumer<String> adapterRemoved) {
        Activity activity = Preferences.getInstance().getActivity();
        Timer timer = new Timer();
        ArrayList<Interface> interfaces = getInterfaces(0);
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {

                    try {
                        boolean changed = false;
                        ArrayList<Interface> newInterfaces = getInterfaces(0);
                        if (newInterfaces.size() != interfaces.size()) {
                            changed = true;
                        } else {
                            for (int i = 0; i < interfaces.size(); i++) {
                                if (!interfaces.get(i).getName().equals(newInterfaces.get(i).getName())) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed){
                            boolean added = false;
                            boolean removed = false;
                            for (Interface i : newInterfaces){
                                if (!interfaces.contains(i)) {
                                    added = true;
                                    break;
                                }
                            }
                            for (Interface i : interfaces){
                                if (!newInterfaces.contains(i)) {
                                    removed = true;
                                    break;
                                }
                            }
                            if (added){
                                if (adapterAdded != null){
                                    activity.runOnUiThread(() -> {
                                    adapterAdded.accept(newInterfaces.get(newInterfaces.size()-1).getName());});
                                }
                            }
                            if (removed){
                                if (adapterRemoved != null){
                                    activity.runOnUiThread(() -> {
                                    adapterRemoved.accept(interfaces.get(interfaces.size()-1).getName());});
                                }
                            }
                        }
                    } catch (Exception e) {
                        ACRA.getErrorReporter().handleSilentException(e);
                    }

            }
        }, 0, 5000);
    }

    public void checkMonitorMode(String iface, String cmd, boolean chroot, Consumer<Boolean> result) {
        ExecutorBuilder builder = new ExecutorBuilder()
                .setCommand(cmd.replace("{iface}",iface))
                .setChroot(chroot)
                .setActivity(Preferences.getInstance().getActivity())
                .setTimeout(15)
                .setOnFinished(strings -> {
                    Thread t = new Thread(() -> {
                        boolean ok = isMonitorModeEnabled(iface);
                        Preferences.getInstance().getActivity().runOnUiThread(() -> result.accept(ok));
                    });
                    t.start();
                });
        builder.execute();
    }

    public void checkInjection(String iface, Consumer<Boolean> result){
        ExecutorBuilder builder = new ExecutorBuilder()
                .setCommand("aireplay-ng --test "+iface)
                .setChroot(true)
                .setActivity(Preferences.getInstance().getActivity())
                .setTimeout(60)
                .setOnFinished(strings -> result.accept(ExecutorBuilder.contains(strings,"Injection is working!")));
        builder.execute();
    }

}
