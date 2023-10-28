package com.example.myapplication.ui.notifications;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myapplication.LocationDB;
import com.example.myapplication.R;
import com.example.myapplication.helper.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateProfileActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    String userID;
    EditText nameEditText;
    EditText phoneEditText;
    ImageView imageView ;
    String TAG = "UpdateProfile";
    private static final int REQUEST_STORAGE_PERMISSION = 1001;


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            Log.d(TAG, "requestStoragePermission: granted ");
        }
    }
    private   int REQUEST_PICK_IMAGE = 1002;

    private void openGallery() {
        Log.d(TAG, "openGallery: start  ");

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
        Log.d(TAG, "openGallery: ");
    }
    Uri selectedImageUri = null;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        Intent intent = getIntent();
        nameEditText.setText(intent.getStringExtra("name"));
        phoneEditText.setText(intent.getStringExtra("phone"));

        Button saveButton = findViewById(R.id.save_button);
        imageView = findViewById(R.id.updateImageView);
        LocationDB locationDB = new LocationDB();
        locationDB.getImageURL(new Callback<Uri>() {
            @Override
            public void onComplete(Uri response) {
                Glide.with(UpdateProfileActivity.this).load(response).into(imageView); // Using Glide library to load the image

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: o duke ");
                // Check for storage permission
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onClick: Permission ase ");
                    openGallery();

                } else {
                    Log.d(TAG, "onClick: permission nai asking for it ");
                    requestStoragePermission();
                    openGallery();
                }
            }

        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("users").document(userID)
                        .update("firstName" , nameEditText.getText().toString() ,"phone" , phoneEditText.getText().toString() )
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Document successfully updated!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));

                LocationDB locationDB = new LocationDB() ;
                locationDB.uploadImage(selectedImageUri);

                Intent intent = new Intent(getApplicationContext() , ViewProfileFragment.class);
                startActivity(intent);
            }
        });





        System.out.println(userID);

    }
}