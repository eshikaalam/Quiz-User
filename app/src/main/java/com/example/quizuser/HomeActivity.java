package com.example.quizuser;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.quizuser.MainActivity;
import com.example.quizuser.R;
import com.example.quizuser.RatingActivity;
import com.example.quizuser.RecognitionActivity;
import com.example.quizuser.RestActivity;
import com.example.quizuser.TestActivity;
import com.example.quizuser.UserProfileActivity;
import com.example.quizuser.YouTubeVideo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private LottieAnimationView laView;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        laView = findViewById(R.id.animation);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        laView.setAnimation(R.raw.animationlottie);
        laView.playAnimation();
        laView.loop(true);

        // Check if email is verified
        if (firebaseUser != null) {
            checkIfEmailVerified(firebaseUser);
        }

        Button buttonStart = findViewById(R.id.btn_start);
        Button buttonRecognition = findViewById(R.id.btn_recognition);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,TestActivity.class);
                startActivity(intent);
            }
        });

        buttonRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RecognitionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            // Redirect to login screen or show alert dialog
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Email is not verified");
        builder.setMessage("You can not login next time. verify your email first");

        //open email app if user clicks continue

        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //to email app in new window, not within our app
                startActivity(intent);
            }
        });

        //create alert dialog box
        AlertDialog alertDialog = builder.create();

        //show the alert dialog box
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_review) {
            // Open RatingActivity instead of replacing the fragment
            Intent intent = new Intent(HomeActivity.this, RatingActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_refresh) {
            // Refresh the page or activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_quiz) {
            Intent intent = new Intent(HomeActivity.this, TestActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_category) {
            Intent intent = new Intent(HomeActivity.this, RestActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_learn_more) {
            Intent intent = new Intent(HomeActivity.this, YouTubeVideo.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(HomeActivity.this, "Logged out of the app", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            // Clear stack to prevent user coming back once logged out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close HomeActivity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
