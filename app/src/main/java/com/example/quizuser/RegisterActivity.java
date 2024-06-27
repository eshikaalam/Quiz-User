package com.example.quizuser;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    FirebaseDatabase db;
    private String selectDivisions,  selectDistricts;
    private TextView tvDivisionSpinner, tvDistrictSpinner;
    private Spinner divisionSpinner, districtSpinner;
    private ArrayAdapter<CharSequence> divisionAdapter, districtAdapter;

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB,
            editTextRegisterMobile, editTextRegisterPwd, editTextRegisterConfirmPwd, editTextRegisterUsername;

    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;

    private Handler handler = new Handler();
    private Runnable usernameCheckRunnable;
    private static final long USERNAME_CHECK_DELAY = 1000;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Register");
        }

        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_SHORT).show();

        db = FirebaseDatabase.getInstance("https://quizzapp-2390a-default-rtdb.firebaseio.com/");

        progressBar = findViewById(R.id.progessBar);
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);
        editTextRegisterUsername = findViewById(R.id.editText_register_username); //editTextUserName
        editTextRegisterUsername.addTextChangedListener(usernameTextWatcher);

        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //initilazation of the spinner
        divisionSpinner = findViewById(R.id.spinner_bd_divisions);

        //populate ArrayAdapter using string array and a spinner layout that we will define

        divisionAdapter = ArrayAdapter.createFromResource(this,R.array.array_bangladesh_divisions,R.layout.spinner_layout);

        //specify the layout to use when the list of choices appear
        divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set the adapter to the spinner to populate the state spinner

        divisionSpinner.setAdapter(divisionAdapter);

        // when any item of the division is selected

        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                districtSpinner = findViewById(R.id.spinner_bd_districts);

                //obtain the name of the division that was selected
                selectDivisions = divisionSpinner.getSelectedItem().toString();

                int parentId = parent.getId();
                if(parentId == R.id.spinner_bd_divisions){
                    switch(selectDivisions){
                        case "Select Your Division": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_default_districts,R.layout.spinner_layout);
                            break;

                        case "Barisal":districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_districts_barisal,R.layout.spinner_layout);
                            break;

                        case "Chittagong":districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_districts_chittagong,R.layout.spinner_layout);
                            break;

                        case "Dhaka":districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_districts_dhaka,R.layout.spinner_layout);
                            break;

                        default: break;
                    }
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //populate the district according to the selected division
                    districtSpinner.setAdapter(districtAdapter);

                    //to obtain the selected district from the district spinner
                    districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectDistricts = districtSpinner.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    tvDivisionSpinner = findViewById(R.id.textView_bd_divisions);
                    tvDistrictSpinner = findViewById(R.id.textView_bd_districts);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.btn_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectGenderId);

                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
                String textUsername = editTextRegisterUsername.getText().toString(); //userName is used as textUserName
                String textGender;

                String mobileRegex = "^(?:\\+?88)?01[3-9]\\d{8}$";

                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "please enter your full name", Toast.LENGTH_SHORT).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "please enter your email", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "please re-enter your email", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this, "Please enter Date of birth", Toast.LENGTH_SHORT).show();
                    editTextRegisterDoB.setError("Date of birth is required");
                    editTextRegisterDoB.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select gender", Toast.LENGTH_SHORT).show();
                    radioButtonRegisterGenderSelected.setError("Gender is not selected");
                    radioButtonRegisterGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this, "Please enter mobile no.", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile No. is required");
                    editTextRegisterMobile.requestFocus();
                } else if (textMobile.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your Mobile No.", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile No. must be 11 digits");
                    editTextRegisterMobile.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your Mobile No.", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile No. is not valid");
                    editTextRegisterMobile.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(RegisterActivity.this, "please enter your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();
                } else if (textPwd.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Password too weak");
                    editTextRegisterPwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "re-enter your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                } else if (TextUtils.isEmpty(textUsername)) {
                    Toast.makeText(RegisterActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    editTextRegisterUsername.setError("Username is required");
                    editTextRegisterUsername.requestFocus();
                } else if (selectDivisions.equals("Select Your Division")){
                    Toast.makeText(RegisterActivity.this, "Please select division", Toast.LENGTH_SHORT).show();
                    tvDivisionSpinner.setError("Division is required");
                    tvDivisionSpinner.requestFocus();
                } else if (selectDivisions.equals("Select Your Districts")){
                    Toast.makeText(RegisterActivity.this, "Please select district", Toast.LENGTH_SHORT).show();
                    tvDistrictSpinner.setError("District is required");
                    tvDistrictSpinner.requestFocus();
                    tvDivisionSpinner.setError(null);
                }

                else {
                    tvDivisionSpinner.setError(null);
                    tvDistrictSpinner.setError(null);
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    checkUsernameUniqueness(textUsername, textFullName, textEmail, textDoB, textGender, textMobile, textPwd);
                }
            }
        });
    }

    private TextWatcher usernameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not needed for this implementation
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not needed for this implementation
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (usernameCheckRunnable != null) {
                handler.removeCallbacks(usernameCheckRunnable);
            }

            // Schedule a new check after USERNAME_CHECK_DELAY milliseconds
            usernameCheckRunnable = () -> {
                String textUserName = s.toString().trim();
                if (!TextUtils.isEmpty(textUserName)) {
                    checkUsernameAvailability(textUserName);
                }
            };
        }
    };

    private void checkUsernameAvailability(final String userName) {
        DatabaseReference referenceUsername = FirebaseDatabase.getInstance().getReference("Usernames");
        Query usernameQuery = referenceUsername.equalTo(userName);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(RegisterActivity.this, "Username is not available, choose another one", Toast.LENGTH_SHORT).show();
                    editTextRegisterUsername.setError("Username not available");
                    editTextRegisterUsername.requestFocus();
                    progressBar.setVisibility(View.GONE);
                } else {
                    String email = String.valueOf(editTextRegisterEmail.getText());
                    String pwd = String.valueOf(editTextRegisterPwd.getText());
                    String doB = String.valueOf(editTextRegisterDoB.getText());
                    String fullName = String.valueOf(editTextRegisterFullName);
                    String mobile = String.valueOf(editTextRegisterMobile);
                    String gender = String.valueOf(radioGroupRegisterGender);
                    registerUser(fullName, email, doB, gender, mobile, pwd, userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Error checking username availability", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkUsernameUniqueness(final String userName,final  String fullName, final String email, final String doB, final String gender, final String  mobile, final String pwd) {
        DatabaseReference usersRef = db.getReference("users");
        Query usernameQuery = usersRef.orderByChild("userName").equalTo(userName);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(RegisterActivity.this, "Username is already taken", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    // Username is unique, proceed with registration
                    registerUser(fullName, email, doB, gender, mobile, pwd, userName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseDatabase", "Error checking username uniqueness", databaseError.toException());
                Toast.makeText(RegisterActivity.this, "Error checking username uniqueness", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void registerUser(String fullName, String email, String doB, String gender,
                              String mobile, String pwd, String username) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(fullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(doB, gender, mobile);

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        DatabaseReference referenceUsername = FirebaseDatabase.getInstance().getReference("Usernames");
                                        referenceUsername.child(username).setValue(firebaseUser.getUid());

                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "Registered successfully. Please verify your email", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
                                    }

                                    progressBar.setVisibility(View.GONE);

                                }
                            });
                        } else {
                            // Handle the case where task.getException() is null
                            Log.e(TAG, "Exception is null");
                            Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
