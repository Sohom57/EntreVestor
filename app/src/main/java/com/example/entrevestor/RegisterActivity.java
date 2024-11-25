package com.example.entrevestor;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextCreatePassword, editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textViewExistingUser;
    private boolean createPasswordVisible = false;
    private boolean confirmPasswordVisible = false;

    // Firebase references
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Database Reference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        editTextUsername = findViewById(R.id.editTextLoginUsername1);
        editTextEmail = findViewById(R.id.editTextLoginUsername2);
        editTextCreatePassword = findViewById(R.id.editTextCreatePassword1);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword2);
        buttonRegister = findViewById(R.id.register);
        textViewExistingUser = findViewById(R.id.textViewExistingUser);

        // Set up the eye icon click listener for "Create Password"
        editTextCreatePassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextCreatePassword.getRight() - editTextCreatePassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (createPasswordVisible) {
                            editTextCreatePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            createPasswordVisible = false;
                        } else {
                            editTextCreatePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            createPasswordVisible = true;
                        }
                        editTextCreatePassword.setSelection(editTextCreatePassword.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        // Set up the eye icon click listener for "Confirm Password"
        editTextConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextConfirmPassword.getRight() - editTextConfirmPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (confirmPasswordVisible) {
                            editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            confirmPasswordVisible = false;
                        } else {
                            editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            confirmPasswordVisible = true;
                        }
                        editTextConfirmPassword.setSelection(editTextConfirmPassword.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        // Set up button click listener for registration
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextCreatePassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                if (validateInputs(username, email, password, confirmPassword)) {
                    registerUser(username, email, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Handle existing user click
        textViewExistingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    // Method to validate user input fields
    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check password complexity
        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 8 characters, contain a digit, and a special character.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    // Method to validate password using regex
    private boolean isValidPassword(String password) {
        // Regular expression to ensure at least 8 characters, a digit, and a special character
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#\\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        return pattern.matcher(password).matches();
    }

    // Method to register user using Firebase Authentication
    private void registerUser(String username, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        saveUserToDatabase(user, username, email);
                    } else {
                        // Registration failed
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Method to save user details in Firebase Realtime Database
    private void saveUserToDatabase(FirebaseUser user, String username, String email) {
        String userId = user.getUid();
        User newUser = new User(username, email);

        databaseReference.child(userId).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // User model class
    public static class User {
        public String username;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}
