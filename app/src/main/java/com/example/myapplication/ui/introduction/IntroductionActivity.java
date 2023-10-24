package com.example.myapplication.ui.introduction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class IntroductionActivity extends AppCompatActivity {

    private AppCompatButton mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        mReturnButton = findViewById(R.id.return_button);
        mReturnButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}