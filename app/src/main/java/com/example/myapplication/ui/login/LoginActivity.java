package com.example.myapplication.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Register;
import com.example.myapplication.ui.login.LoginViewModel;
import com.example.myapplication.ui.login.LoginViewModelFactory;
import com.example.myapplication.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public void goToRegister(View view) {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
    }

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    public ProgressDialog progressDialog ;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        EditText emailText = (EditText) findViewById(R.id.username);
        EditText pass = (EditText) findViewById(R.id.password);

        Button button = findViewById(R.id.login);
//        progressDialog.findViewById(R.id.loading);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressDialog.show();

                String email = String.valueOf(emailText.getText());
                String password= String.valueOf(pass.getText());
                if(email.length()>0 && password.length()>0)
                {
//                    button.setEnabled(true);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Login success
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(LoginActivity.this , "Success " , Toast.LENGTH_SHORT);

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        // Update your UI with the user's information
                                    } else {
                                        Toast.makeText(LoginActivity.this , "Error " , Toast.LENGTH_SHORT);
                                        // Login failure
                                        // Handle the failure here
                                    }
                                }
                            });
                }
            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();

        // Dismiss the progressbar when the new intent has loaded
//        progressDialog.dismiss();
    }
}