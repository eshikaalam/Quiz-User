package com.example.quizuser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.quizuser.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
    }
}