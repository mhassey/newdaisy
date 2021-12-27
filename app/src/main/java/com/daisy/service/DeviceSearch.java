package com.daisy.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.daisy.common.session.SessionManager;
import com.daisy.pojo.response.IpSearched;
import com.daisy.utils.DeviceList;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
        ArrayList<String> hostAddress = new ArrayList<>();
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
                        if (address.isReachable(200)) {
                            hostAddress.add(address.getHostAddress());
                        } else if (!address.getHostAddress().equals(address.getHostName())) {
                            System.out.println(address + " machine is known in a DNS lookup");
                        }

                    }
                    SessionManager.get().addFilterDevice(hostAddress);
                    EventBus.getDefault().post(new IpSearched());

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