package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {

    String email, name , phone ,password ;
    Button button ;
    private FirebaseAuth mAuth;

    public  void registerToMainMenu(View view)
    {
        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
        intent.putExtra("email" , email);
        intent.putExtra("name" , name);
        intent.putExtra("phone" , phone);
        Log.d("kita" , name);
        System.out.println(email + " " + name +" hello  " + phone );


        startActivity(intent);

    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
////            reload();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        EditText emailText = (EditText) findViewById(R.id.editTextTextEmailAddress);
//         email = emailText.getText().toString();
//
        EditText nameText = (EditText) findViewById(R.id.editTextTextName);
//        name = nameText.getText().toString();
//
        EditText numberText = (EditText) findViewById(R.id.editTextPhoneNumber);
//        phone = numberText.getText().toString();
//
        EditText pass = (EditText) findViewById(R.id.editTextPassword);
//        password = pass.getText().toString();

        button = findViewById(R.id.registerButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = String.valueOf(emailText.getText());
                password = String.valueOf(pass.getText());
                name = String.valueOf(nameText.getText());
                phone = String.valueOf(numberText.getText());

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("tag1", "createUserWithEmail:success");
                                    Toast.makeText(Register.this, "Authentication Success." + email + password,
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                                    // Update the user's profile.
                                    user.updateProfile(profileUpdates);
//                                    updateUI(user);
                                }
                                else {

                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG2", "createUserWithEmail:failure", task.getException());
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(Register.this, "Authentication failed: " + errorMessage,
                                            Toast.LENGTH_SHORT).show();


//                                    updateUI(null);
                                }
                            }
                        });
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                intent.putExtra("email" , email);
                intent.putExtra("name" , name);
                intent.putExtra("phone" , phone);
                startActivity(intent);

            }

//
//                Log.d("kita" , name);
//                System.out.println(email + " " + name +" hello  " + phone );


//                    startActivity(intent);
        });


    }
}