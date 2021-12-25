package com.daisy.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.daisy.utils.DeviceList;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DeviceSearch extends Service {
    public DeviceSearch() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getConnectDeviceList();
        return super.onStartCommand(intent, flags, startId);

    }

    private void getConnectDeviceList() {
        final InetAddress[] host = new InetAddress[1];
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    WifiManager wifii = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    DhcpInfo dhcp = wifii.getDhcpInfo();
                    host[0] = InetAddress.getByName(intToIp(dhcp.dns1));
                    byte[] ip = host[0].getAddress();

                    for (int i = 1; i <= 254; i++) {
                        ip[3] = (byte) i;
                       InetAddress address = InetAddress.getByAddress(ip);
                        if (address.isReachable(100)) {
                            DeviceList.getInstance().setDevices(address);
                        } else if (!address.getHostAddress().equals(address.getHostName())) {
                            System.out.println(address + " machine is known in a DNS lookup");
                        }

                    }

                    BackgroundService.IpSearched();

                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    public String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                ((i >> 24) & 0xFF);
    }
}