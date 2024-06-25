package com.example.quizuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.quizuser.Adapters.GrideAdapter;
import com.example.quizuser.databinding.ActivitySetQuestionBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SetQuestionActivity extends AppCompatActivity {

    ActivitySetQuestionBinding binding;
    FirebaseDatabase database;

    GrideAdapter adapter;
    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        database = FirebaseDatabase.getInstance();
        key = getIntent().getStringExtra("key");
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new GrideAdapter(getIntent().getIntExtra("sets",0),
                getIntent().getStringExtra("category"));

        binding.gridView.setAdapter(adapter);


    }
}