package com.zalexdev.stryker.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DebugData {

    private static final int STATUS_STARTED = 0;
    private static final int STATUS_RUNNING = 1;
    private static final int STATUS_DELETED = 2;

    private Map<String, Integer> commands = new HashMap<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private static DebugData instance;


    private DebugData() {}

    public static synchronized DebugData getInstance() {
        if (instance == null) {
            instance = new DebugData();
        }
        return instance;
    }

    public synchronized void addCmd(String cmd) {
        commands.put(cmd, STATUS_STARTED);
    }

    public synchronized void runCmd(String cmd) {
        if (commands.containsKey(cmd) && commands.get(cmd) == STATUS_STARTED) {
            commands.put(cmd, STATUS_RUNNING);
        }
    }

    public synchronized void delCmd(final String cmd) {
        if (commands.containsKey(cmd) && commands.get(cmd) == STATUS_RUNNING) {
            commands.put(cmd, STATUS_DELETED);

            executor.submit(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    synchronized (DebugData.this) {
                        commands.remove(cmd);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    public synchronized ArrayList<String> getCmds() {
        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : commands.entrySet()) {
            String statusString;
            switch (entry.getValue()) {
                case STATUS_STARTED:
                    statusString = "started";
                    break;
                case STATUS_RUNNING:
                    statusString = "running";
                    break;
                case STATUS_DELETED:
                    statusString = "deleted";
                    break;
                default:
                    statusString = "unknown";
            }
            result.add(entry.getKey() + "|||" + statusString);
        }
        return result;
    }

}