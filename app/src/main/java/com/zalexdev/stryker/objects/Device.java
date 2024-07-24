package com.zalexdev.stryker.objects;

import com.zalexdev.stryker.R;
import com.zalexdev.stryker.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

/**
 * A Device object is a single device that is scanned. It contains the IP address, MAC address, vendor,
 * OS, and subname. It also contains a list of ports and services
 */


public class Device {
    @Setter
    @Getter
    public String ip = "";
    @Getter
    public String mac = "Scanning...";
    public String vendor = "";
    @Setter
    public int image = 0;
    @Setter
    @Getter
    public String os = "Unknown";
    @Setter
    @Getter
    public String hostname = "";
    @Setter
    @Getter
    public boolean isMe = false;
    @Setter
    @Getter
    public boolean isCutted = false;
    @Getter
    @Setter
    public ArrayList<Port> ports = new ArrayList<>();
    @Getter
    @Setter
    private ArrayList<String> nmapoutput = new ArrayList<>();
    @Setter
    @Getter
    public boolean shim = true;
    @Setter
    @Getter
    boolean iscutted = false;
    public ArrayList<String> portsFromString = new ArrayList<>();
    private final Utils utils = new Utils();
    public Device() {
    }




    public void addPort(Port port) {
        ports.add(port);
    }
    public void guessos(){
        ArrayList<Port> ports = getPorts();
        for (Port port : ports) {
            if (port.getPortNumber().contains("21") || port.getPortNumber().contains("22") || port.getPortNumber().contains("23")) {
                setOs("Linux");
                setImage(R.drawable.linux);
            }
            if (port.getPortNumber().contains("554") || port.getPortNumber().contains("37777")) {
                setOs("Secure Camera");
                setImage(R.drawable.nest_cam_indoor);
            }
            if (port.getPortNumber().contains("9100")) {
                setOs("Printer");
                setImage(R.drawable.printer);
            }
            if (port.getPortNumber().contains("2336") || port.getPortNumber().contains("3004") || port.getPortNumber().contains("3031")) {
                setOs("IOS/MACOS");
                setImage(R.drawable.apple);
            }
            if (port.getPortNumber().contains("3389") || port.getPortNumber().contains("135") || port.getPortNumber().contains("136") || port.getPortNumber().contains("137") || port.getPortNumber().contains("138") || port.getPortNumber().contains("139") || port.getPortNumber().contains("5357") || port.getPortNumber().contains("445") || port.getPortNumber().contains("903")) {
                setOs("Windows");
                setImage(R.drawable.windows);
            }
            if (port.getPortNumber().contains("1900")) {
                setOs("Linux");
                setImage(R.drawable.router);
            }
        }
        
    }

    public String getVendor() {
        if (vendor == null || vendor.isEmpty()) {
            vendor = utils.getVendorByMacFromDB(getMac());
            if (vendor == null || vendor.isEmpty()) {
                if (getOs().contains("Linux") && getPorts().isEmpty()){
                    vendor = "Probably Android With Random MAC";
                }else {
                    vendor = "Unknown vendor";
                }
            }
        }
        return vendor;
    }

    public int getImage() {

        if (image == 0) {
            image = R.drawable.devices;
        }
        if (os.contains("Windows")){
            image = R.drawable.windows;
        }
        if (os.contains("Linux")){
            image = R.drawable.linux;
        }
        if (os.contains("Android")){
            image = R.drawable.phone;
        }
        if (os.contains("IOS")||os.contains("MacOS")||os.contains("Apple")){
            image = R.drawable.apple;
        }
        return image;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
        String ven = vendor.toLowerCase(Locale.ROOT);
        if (ven.contains("apple")) {
            setOs("MacOS/IOS");
            setImage(R.drawable.apple);
        } else if (ven.contains("microsoft")) {
            setOs("Windows");
            setImage(R.drawable.windows);
        } else if (ven.contains("hikvision") || ven.contains("dahua")) {
            setOs("Secure Camera");
            setImage(R.drawable.nest_cam_indoor);
        } else if (ven.contains("linux")) {
            setOs("Linux");
            setImage(R.drawable.linux);
        }
        if (ven.contains("unknown") || ven.contains("none") || ven.contains("null") || ven.contains("nil")) {
            setOs("Unknown");
            setImage(R.drawable.devices);
            this.vendor = utils.getVendorByMacFromDB(getMac());
        }

    }

    public void setMac(String mac) {
        vendor = utils.getVendorByMacFromDB(mac);
        this.mac = mac;
    }

    public ArrayList<String> portsToString(){
        ArrayList<String> ports = new ArrayList<>();
        for (Port port : getPorts()){
            ports.add(port.getPortNumber());
        }
        return ports;
    }


    public String portsArrayToString(){
        StringBuilder ports = new StringBuilder();
        for (Port port : getPorts()){
            ports.append(port.getPortNumber()).append(",");
        }
        return ports.toString();
    }

    public String toJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("ip", getIp());
            json.put("mac", getMac());
            json.put("vendor", getVendor());
            json.put("os", getOs());
            json.put("subname", getHostname());
            json.put("iscutted", isIscutted());
            json.put("shim", isShim());
            json.put("ports", portsArrayToString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }
    public void restoreFromJSON(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            setIp(jsonObject.getString("ip"));
            setMac(jsonObject.getString("mac"));
            setVendor(jsonObject.getString("vendor"));
            setOs(jsonObject.getString("os"));
            setHostname(jsonObject.getString("subname"));
            setIscutted(jsonObject.getBoolean("iscutted"));
            setShim(jsonObject.getBoolean("shim"));
            Collections.addAll(portsFromString, jsonObject.getString("ports").split(","));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Device{" +
                "ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", vendor='" + vendor + '\'' +
                ", image=" + image +
                ", os='" + os + '\'' +
                ", hostname='" + hostname + '\'' +
                ", ports=" + ports +
                ", nmapoutput=" + nmapoutput +
                ", shim=" + shim +
                ", iscutted=" + iscutted +
                ", portsFromString=" + portsFromString +
                ", utils=" + utils +
                '}';
    }
}
