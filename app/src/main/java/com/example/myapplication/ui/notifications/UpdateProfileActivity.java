package com.example.myapplication.ui.notifications;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateProfileActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    String userID;
    EditText nameEditText;
    EditText phoneEditText;
    String TAG = "UpdateProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("users").document(userID)
                        .update("firstName" , nameEditText.getText().toString() ,"phone" , phoneEditText.getText().toString() )
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Document successfully updated!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));

                Intent intent = new Intent(getApplicationContext() , ViewProfileFragment.class);
                startActivity(intent);
            }
        });





        System.out.println(userID);

    }
}