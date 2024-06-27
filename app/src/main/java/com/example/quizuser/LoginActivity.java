package com.example.quizuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
        }

        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_pwd);
        progressBar = findViewById(R.id.progessBar);

        authProfile = FirebaseAuth.getInstance();

        //Login button

        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is invalid");
                    editTextLoginEmail.requestFocus();
                } else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("password is required");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });


    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //Get instance of the current user

                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    // Check if email is verified before user access their profile

                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        // open user profile
                        //start UserProfileActivity
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finish();
                    } else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }

                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        try {
                            throw exception;
                        } catch (FirebaseAuthInvalidUserException e) {
                            editTextLoginEmail.setError("User does not exist. Please register again");
                            editTextLoginEmail.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            editTextLoginEmail.setError("Invalid credentials. Kindly check and re-enter");
                            editTextLoginEmail.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        // setup alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email is not verified");
        builder.setMessage("can not login. verify your email first");

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

    //check if user is already logged in. In such case, take the user to User's profile

    @Override
    protected void onStart() {
        super.onStart();

        if(authProfile.getCurrentUser() != null) {
            Toast.makeText(LoginActivity.this, "Already logged in", Toast.LENGTH_SHORT).show();

            //start UserProfileActivity
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }
        else {
            Toast.makeText(LoginActivity.this, "You can login now", Toast.LENGTH_SHORT).show();
        }
    }
}