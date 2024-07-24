package com.zalexdev.stryker.objects;

import java.util.ArrayList;

public class Host {
    private String ipAddress;
    private String hostname;
    private String macAddress;
    private String vendor;
    private ArrayList<String> openPorts;

    public Host(String ipAddress, String hostname, String macAddress, String vendor, ArrayList<String> openPorts) {
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.macAddress = macAddress;
        this.vendor = vendor;
        this.openPorts = openPorts;
    }


    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public ArrayList<String> getOpenPorts() {
        return openPorts;
    }

    public void setOpenPorts(ArrayList<String> openPorts) {
        this.openPorts = openPorts;
    }
}