package com.zalexdev.stryker.objects;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class Interface {

    public String name = "";
    public String mac = "";
    public String ssid = "";
    public String type = "";

    public String getName() {
        return this.name;
    }

    public String getMac() {
        return this.mac;
    }

    public String getSsid() {
        return this.ssid;
    }

    public String getType() {
        return this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Interface() {
    }

    public Interface(String name, String mac, String ssid, String type) {
        this.name = name;
        this.mac = mac;
        this.ssid = ssid;
        this.type = type;
    }

    @NonNull
    public String toString() {
        return "Interface{name=" + this.name + ", mac=" + this.mac + ", ssid=" + this.ssid + ", type=" + this.type + "}";
    }

    public static ArrayList<String> getInterfaceNames(ArrayList<Interface> interfaces) {
        ArrayList<String> names = new ArrayList<>();
        for (Interface iface : interfaces) {
            names.add(iface.getName());
        }
        return names;
    }

    public static ArrayList<Interface> parse(ArrayList<String> lines) {
        ArrayList<Interface> interfaces = new ArrayList<>();
        Interface currentInterface = null;

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Interface")) {
                if (currentInterface != null) {
                    interfaces.add(currentInterface);
                }
                currentInterface = new Interface();
                String name = line.substring("Interface".length()).trim();
                currentInterface.setName(name);
            } else if (currentInterface != null) {
                if (line.startsWith("addr")) {
                    currentInterface.setMac(line.substring("addr".length()).trim());
                } else if (line.startsWith("ssid")) {
                    currentInterface.setSsid(line.substring("ssid".length()).trim());
                } else if (line.startsWith("type")) {
                    currentInterface.setType(line.substring("type".length()).trim());
                }
            }
        }

        if (currentInterface != null) {
            interfaces.add(currentInterface);
        }

        return interfaces;
    }
}
