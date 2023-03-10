package com.example.Modul6;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private TelephonyManager telephonyManager;
    private WifiManager wifiManager;

    private String imei, imeiSIM, phoneNumber, networkOperator, macAddress, ipAddress, androidVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if the app has permission to access device information
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_PHONE_STATE },
                    PERMISSION_REQUEST_CODE);
        } else {
            // Permission is granted, proceed to get device information
            getDeviceInfo();
        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private void getDeviceInfo() {
        // Get device information
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager != null) {
            imei = telephonyManager.getDeviceId();
            imeiSIM = telephonyManager.getSimSerialNumber();
            phoneNumber = telephonyManager.getLine1Number();
            networkOperator = telephonyManager.getNetworkOperatorName();
        }

        // Get MAC address
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        macAddress = wifiInfo.getMacAddress();

        // Get IP address
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface iface : interfaces) {
                List<InetAddress> addresses = Collections.list(iface.getInetAddresses());
                for (InetAddress addr : addresses) {
                    if (addr instanceof Inet4Address) {
                        if (!addr.isLoopbackAddress()) {
                            ipAddress = addr.getHostAddress();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("IP Address", "Unable to get IP address: " + e.getMessage());
        }

        // Get Android version
        androidVersion = Build.VERSION.RELEASE;

        // Print device information
        Log.d("DeviceInfo", "IMEI: " + imei);
        Log.d("DeviceInfo", "IMEI SIM: " + imeiSIM);
        Log.d("DeviceInfo", "Phone number: " + phoneNumber);
        Log.d("DeviceInfo", "Network operator: " + networkOperator);
        Log.d("DeviceInfo", "MAC address: " + macAddress);
        Log.d("DeviceInfo", "IP address: " + ipAddress);
        Log.d("DeviceInfo", "Android version: " + androidVersion);

    }}