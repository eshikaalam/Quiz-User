package com.example.quizuser;

// RatingActivity.java

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);


        RatingFragment ratingFragment = new RatingFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rating_fragment_container, ratingFragment)
                .addToBackStack(null)
                .commit();
    }
}
