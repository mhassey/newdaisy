package com.iris.utils;

import java.net.InetAddress;
import java.util.ArrayList;

public class DeviceList {
    public static DeviceList deviceList = new DeviceList();
    public static ArrayList<InetAddress> devices = new ArrayList<>();

    public static DeviceList getInstance() {
        return deviceList;
    }

    public static ArrayList<InetAddress> getDevices() {
        return devices;
    }

    public static void setDevices(InetAddress devic) {
        devices.add(devic);
    }
}
