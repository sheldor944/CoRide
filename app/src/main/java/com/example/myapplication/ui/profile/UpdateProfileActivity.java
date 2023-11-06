package com.example.myapplication.ui.profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.helper.Callback;
import com.example.myapplication.service.LocationDB;
import com.example.myapplication.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    String userID;
    EditText nameEditText;
    EditText phoneEditText;
    CircleImageView imageView ;
    String TAG = "UpdateProfile";
    private static final int REQUEST_STORAGE_PERMISSION = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called.");

        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0) {
                    Log.d(TAG, "onRequestPermissionsResult: " + grantResults.length);
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    openGallery();
                }
            }
        }
    }


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            Log.d(TAG, "requestStoragePermission: granted ");
//            openGallery();
        }
    }
    private   int REQUEST_PICK_IMAGE = 1002;

    private void openGallery() {
        Log.d(TAG, "openGallery: start  ");

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
        Log.d(TAG, "openGallery: ");
    }
    private Uri selectedImageUri;
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
        selectedImageUri = null;
        locationDB.getImageURL(new Callback<Uri>() {
            @Override
            public void onComplete(Uri response) {
                Log.d(TAG, "onComplete: loading previous image ");
                Glide.with(UpdateProfileActivity.this).load(response).into(imageView); // Using Glide library to load the image

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: o duke ");
                // Check for storage permission
                openGallery();
            }

        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    firebaseFirestore.collection("users").document(userID)
                            .update("firstName" , nameEditText.getText().toString() ,"phone" , phoneEditText.getText().toString() )
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Document successfully updated!"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                }
                catch (Exception e )
                {
                    Log.d(TAG, "onClick: exception " + e);
                }

                ProgressDialog progressDialog = new ProgressDialog(UpdateProfileActivity.this);
                progressDialog.setMessage("Updating...");
                progressDialog.show();
                LocationDB locationDB = new LocationDB() ;
                if(selectedImageUri != null) {
                    locationDB.uploadImage(selectedImageUri, response -> {
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                        startActivity(intent);
                    });
                }
                else {
                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                    startActivity(intent);
                }
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
//                fragmentTransaction.replace(R.id.navigation_notifications,  viewProfileFragment);
//                fragmentTransaction.addToBackStack(null); // if you want to add the transaction to the back stack
//                fragmentTransaction.commit();
            }
        });

    }
}
