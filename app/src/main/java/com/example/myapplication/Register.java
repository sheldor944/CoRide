package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import android.Manifest;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.google.firebase.firestore.DocumentData;

public class Register extends AppCompatActivity {

    String email, name , phone ,password ;
    Button button ;
    private FirebaseAuth mAuth;
    CircleImageView imageView ;
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

    public interface TokenCallback {
        void onTokenReceived(String token);
    }

    public void getFCMtoken(TokenCallback callback) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    Log.d("token", "onComplete: " + token);
                    callback.onTokenReceived(token);
                } else {
                    Log.d("token", "onComplete: token generation failed");
                    callback.onTokenReceived(null);
                }
            }
        });
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
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        EditText emailText = (EditText) findViewById(R.id.emailRegistrationEditText);
//         email = emailText.getText().toString();
//
        EditText nameText = (EditText) findViewById(R.id.nameEditText);
//        EditText lastNameText = (EditText) findViewById(R.id.lastNameEditText);
//        name = nameText.getText().toString();
//
        EditText numberText = (EditText) findViewById(R.id.phoneEditText);
//        phone = numberText.getText().toString();
//
        EditText pass = (EditText) findViewById(R.id.editTextPassword);
//        password = pass.getText().toString();

//        ImageView imageButton = findViewById(R.id.imageButton);
        imageView = findViewById(R.id.imageButton);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating...");


        button = findViewById(R.id.registerButton);
        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                progressDialog.show();
                email = String.valueOf(emailText.getText());
                password = String.valueOf(pass.getText());
                name = String.valueOf(nameText.getText());
                phone = String.valueOf(numberText.getText());
                if(email.length() == 0 || password.length() == 0 || name.length() == 0 || password.length() == 0 )
                {
                    progressDialog.dismiss();

                    Toast.makeText(Register.this , "Fill up all the fields." , Toast.LENGTH_LONG).show();
                    return;
                }
                String lastName = "";
                Log.d(TAG, "onClick: o dukse ");


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                Log.d(TAG, "onComplete: mAuth create user o dukse");

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    Log.d("tag1", "createUserWithEmail:success");

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "onComplete: sendEmailVerification");
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(Register.this, "Authentication Success. Login after verify" ,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                progressDialog.dismiss();

                                                Toast.makeText(Register.this, "Authentication Failed." ,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    Log.d(TAG, "onComplete:1 " + user.getUid());
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    // Create a new user with a first and last name
                                    DocumentReference userRef = db.collection("users").document(user.getUid());
                                    UserModel data = new UserModel(name , lastName , email , phone , "123123" , password);
//
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assuming the user is already authenticated
                                    getFCMtoken(new TokenCallback() {
                                        @Override
                                        public void onTokenReceived(String token) {
                                            Log.d(TAG, "onTokenReceived: " + userId + "     kita   " + token);
                                            // Use the token here
                                            databaseReference.child(userId).child("fcmToken").setValue(token);
                                        }
                                    });

//                                    String token = getFCMtoken();
//                                    Log.d(TAG, "onComplete: " + userId+"     kita   " + token);
//                                    databaseReference.child(userId).child("fcmToken").setValue(token);

                                    // Use the set() method on the DocumentReference instance to write the data to Firestore
                                    userRef.set(data);
                                    if(selectedImageUri!=null)
                                    {
                                        LocationDB locationDB = new LocationDB();
                                        locationDB.uploadImage(selectedImageUri);

                                    }
                                    progressDialog.dismiss();
                                    FirebaseAuth.getInstance().signOut();
                                    Log.d(TAG, "onComplete: "+FirebaseAuth.getInstance().getCurrentUser());
                                    Intent intent = new Intent(getApplicationContext() , LoginActivity.class);
                                    startActivity(intent);
//
                                }
                                else {

                                    progressDialog.dismiss();

                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG2", "createUserWithEmail:failure", task.getException());
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(Register.this, "Authentication failed: " + errorMessage,
                                            Toast.LENGTH_SHORT).show();


//                                    updateUI(null);
                                }
                            }
                        });

            }

//
//                Log.d("kita" , name);
//                System.out.println(email + " " + name +" hello  " + phone );


//                    startActivity(intent);
        });


    }
}