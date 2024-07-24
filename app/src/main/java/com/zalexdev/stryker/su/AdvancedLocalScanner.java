package com.zalexdev.stryker.su;

import android.app.Activity;
import android.util.Log;

import com.zalexdev.stryker.objects.Device;
import com.zalexdev.stryker.objects.Port;
import com.zalexdev.stryker.utils.ExecutorBuilder;
import com.zalexdev.stryker.utils.Preferences;
import com.zalexdev.stryker.utils.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AdvancedLocalScanner {
    private static final String TAG = "AdvancedLocalScanner";
    private static final int REACHABLE_TIMEOUT = 1000;
    private static final int MAX_THREADS = 5;

    private String gateway;
    private Thread mainThread;
    private String iface;
    private Preferences preferences;
    private Activity activity;
    private Map<String, Device> devicesOld = new HashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public AdvancedLocalScanner(String iface) {
        this.iface = iface;
        preferences = Preferences.getInstance();
        activity = preferences.getActivity();
        mainThread = new Thread(this::startScan);
        mainThread.start();
    }

    public void startScan() {
        gateway = getGateway(iface);
        String cmd = "nmap " + gateway + " -sn -PE -n -PP -T4 --stats-every 1s";
        Log.i(TAG, "Starting scan with command: " + cmd);

        ExecutorBuilder builder = new ExecutorBuilder();
        builder.setCommand(cmd);
        builder.setActivity(preferences.getActivity());
        builder.setContext(preferences.getContext());
        builder.setChroot(true);
        builder.setOutput(s -> {
            Matcher per = Pattern.compile("[0-9]*\\.[0-9]+%").matcher(s);
            if (per.find()) {
                preferences.getActivity().runOnUiThread(() -> onProgressUpdate((int) Double.parseDouble(per.group().replace("%", ""))));
            }
        });

        builder.setOnFinished(nmapOutput -> {
            executorService.submit(() -> {
                List<String> arpScanOutput = runArpScan(iface);
                List<Device> devices = parseNmapOutput(nmapOutput, arpScanOutput);
                scanDevices(devices);
            });
        });

        builder.execute();
    }

    private List<String> runArpScan(String iface) {
        return ExecutorBuilder.runCommandChroot("arp-scan -I " + iface + " -l");
    }

    private List<Device> parseNmapOutput(List<String> nmapOutput, List<String> arpScanOutput) {
        List<Device> devices = new ArrayList<>();
        Device device = null;
        Utils utils = new Utils();

        for (String line : nmapOutput) {
            String temp = line.replaceAll("\\s+", " ").trim();
            if (temp.contains("Nmap scan report for ")) {
                if (device != null) {
                    devices.add(device);
                }
                device = new Device();
                device.setIp(temp.replace("Nmap scan report for ", ""));
            } else if (temp.contains("MAC Address")) {
                Matcher mac = Pattern.compile("((\\w{2}:){5}\\w{2})").matcher(temp);
                if (mac.find()) {
                    String macAddress = mac.group(0).toUpperCase(Locale.ROOT);
                    device.setMac(macAddress);
                    device.setVendor(utils.getVendorByMacFromDB(macAddress));
                }
            }
        }

        if (device != null) {
            devices.add(device);
        }

        for (String line : arpScanOutput) {
            String temp = line.replaceAll("\\s+", " ").trim();
            Matcher mac = Pattern.compile("((\\w{2}:){5}\\w{2})").matcher(temp);
            if (mac.find()) {
                String[] split = temp.split(" ");
                if (split.length >= 2) {
                    String ip = split[0];
                    String macAddress = split[1].toUpperCase(Locale.ROOT);
                    Device existingDevice = devices.stream()
                            .filter(d -> d.getIp().equals(ip))
                            .findFirst()
                            .orElse(null);
                    if (existingDevice == null) {
                        Device newDevice = new Device();
                        newDevice.setIp(ip);
                        newDevice.setMac(macAddress);
                        newDevice.setVendor(utils.getVendorByMacFromDB(macAddress));
                        devices.add(newDevice);
                    }
                }
            }
        }

        devices.sort((a, b) -> {
            String[] aOct = a.getIp().split("\\.");
            String[] bOct = b.getIp().split("\\.");
            for (int i = 0; i < 4; i++) {
                int aVal = Integer.parseInt(aOct[i]);
                int bVal = Integer.parseInt(bOct[i]);
                if (aVal != bVal) {
                    return Integer.compare(aVal, bVal);
                }
            }
            return 0;
        });

        return devices;
    }

    private void scanDevices(List<Device> devices) {
        Map<String, Device> devicesMap = new HashMap<>();
        for (Device device : devices) {
            devicesMap.put(device.getIp(), device);
        }

        activity.runOnUiThread(() -> onProgressUpdate(110));

        CountDownLatch latch = new CountDownLatch(devices.size());

        for (Device device : devices) {
            executorService.submit(() -> {
                try {
                    if (isNewDevice(device)) {
                        Device scannedDevice = scanDevice(device);
                        devicesMap.put(scannedDevice.getIp(), scannedDevice);
                        activity.runOnUiThread(() -> onDeviceAdded(scannedDevice));
                    } else {
                        Device existingDevice = devicesOld.get(device.getIp());
                        devicesMap.put(existingDevice.getIp(), existingDevice);
                        activity.runOnUiThread(() -> onDeviceChanged(existingDevice, devices.indexOf(device)));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        executorService.submit(() -> {
            try {
                latch.await();
                devicesOld = devicesMap;
                activity.runOnUiThread(this::onFinishedScan);
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted while waiting for scans to finish", e);
            }
        });
    }

    private Device scanDevice(Device device) {
        ArrayList<String> output = ExecutorBuilder.runCommandChroot("nmap " + device.getIp() + " -n -Pn -O -F --max-os-tries=3 --script=banner");
        Log.i(TAG, "Finished scanning device: " + device.getIp());

        Device scannedDevice = parseNmapDeviceOutput(output, device.getIp());
        if (scannedDevice.getMac() == null) {
            scannedDevice.setMac(device.getMac());
            scannedDevice.setVendor(device.getVendor());
        }

        try {
            InetAddress address = InetAddress.getByName(device.getIp());
            if (address.isReachable(REACHABLE_TIMEOUT)) {
                String hostname = address.getCanonicalHostName();
                if (!hostname.equals(device.getIp())) {
                    scannedDevice.setHostname(hostname.split("\\.")[0]);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error checking device reachability", e);
        }

        return scannedDevice;
    }

    private Device parseNmapDeviceOutput(ArrayList<String> output, String ip) {
        Device device = new Device();
        ArrayList<Port> ports = new ArrayList<>();
        device.setIp(ip);
        device.setShim(false);

        for (String line : output) {
            String temp = line.replaceAll("\\s+", " ").replace("*", "");

            if (temp.contains("/tcp")) {
                Port port = new Port();
                String r = temp.replace("/tcp", "").replace("open", "").replace("filtered", "").replaceAll("\\\\x([A-Z]|[0-9])([A-Z]|[0-9])", "");
                Matcher m = Pattern.compile("[0-9]+").matcher(r);
                if (m.find()) {
                    String portNum = m.group();
                    String service = r.replaceAll("\\s+", "").replace(portNum, "");
                    port.setPortNumber(portNum);
                    port.setPortName(service);
                    ports.add(port);
                }
            } else if (temp.contains("MAC Address")) {
                Matcher mac = Pattern.compile("((\\w{2}:){5}\\w{2})").matcher(temp);
                if (mac.find()) {
                    device.setMac(mac.group(0).toUpperCase(Locale.ROOT));
                }
                String vendor = temp.replace("MAC Address: ", "").replace(mac + " ", "").replace("(", "").replace(")", "");
                if (mac.find()) {
                    vendor = vendor.replace(mac.group() + " ", "");
                }
                device.setVendor(vendor);
            } else if (temp.contains("Running:")) {
                device.setOs(temp.replace("Running: ", "").replace("Microsoft", ""));
            } else if (temp.contains("No exact matches")) {
                device.setOs("Unknown");
            }
        }

        device.setPorts(ports);
        device.setNmapoutput(output);
        return device;
    }

    private boolean isNewDevice(Device device) {
        return !devicesOld.containsKey(device.getIp());
    }

    private String getGateway(String iface) {
        List<String> output = ExecutorBuilder.runCommandChroot("ip -o -f inet addr show | awk '/scope global/ {print $2, $4}' | grep " + iface);
        if (!output.isEmpty()) {
            return output.get(0).replace(iface + " ", "");
        }
        return "192.168.1.1/24";
    }

    public abstract void onProgressUpdate(int progress);
    public abstract void onDeviceAdded(Device device);
    public abstract void onDeviceChanged(Device device, int pos);
    public abstract void onStarted();
    public abstract void onFinishedScan();
}