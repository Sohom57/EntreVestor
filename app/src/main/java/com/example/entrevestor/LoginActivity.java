package com.example.entrevestor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "loginPrefs";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REMEMBER_ME = "rememberMe";

    private EditText editTextUsername, editTextPassword;
    private CheckBox checkBoxRememberMe;
    private Button buttonLogin;
    private TextView textViewForgotPassword, textViewNewUser;
    private boolean passwordVisible = false;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize views
        initializeViews();

        // Check if "Remember Me" was previously selected
        loadLoginDetails();

        // Set up button click listener for login
        buttonLogin.setOnClickListener(v -> {
            String email = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            if (validateInputs(email, password)) {
                loginUser(email, password);
            } else {
                Toast.makeText(getApplicationContext(), "Please enter valid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Forgot Password click
        textViewForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPassword_Activity.class)));

        // Handle New User? Sign Up Now click
        textViewNewUser.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, sign_up_activity.class)));

        // Set up password field eye icon to toggle visibility
        editTextPassword.setOnTouchListener((v, event) -> togglePasswordVisibility(event, editTextPassword, true));
    }

    // Method to initialize views
    private void initializeViews() {
        editTextUsername = findViewById(R.id.editTextLoginUsername);
        editTextPassword = findViewById(R.id.editTextLoginPassword);
        checkBoxRememberMe = findViewById(R.id.checkBoxLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewNewUser = findViewById(R.id.textViewNewUser);
    }

    // Method to load saved login details for Remember Me
    private void loadLoginDetails() {
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
        if (rememberMe) {
            editTextUsername.setText(sharedPreferences.getString(KEY_EMAIL, ""));
            editTextPassword.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
            checkBoxRememberMe.setChecked(true);
        }
    }

    // Method to validate email and password inputs
    private boolean validateInputs(String email, String password) {
        return isValidEmail(email) && !password.isEmpty();
    }

    // Method to check if the email format is valid
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }

    // Method to log in the user with Firebase Authentication
    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            user.reload().addOnCompleteListener(reloadTask -> { // Reload the user profile
                                if (reloadTask.isSuccessful()) {
                                    if (user.isEmailVerified()) {
                                        if (checkBoxRememberMe.isChecked()) {
                                            saveLoginDetails(email, password); // Save credentials
                                        } else {
                                            clearLoginDetails();
                                        }
                                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, profile_activity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Please verify your email before logging in", Toast.LENGTH_SHORT).show();
                                        firebaseAuth.signOut();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Failed to reload user. Try again later.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        handleLoginFailure(task.getException());
                    }
                });
    }


    // Method to handle login failure messages
    private void handleLoginFailure(Exception exception) {
        String errorMessage;
        if (exception instanceof FirebaseAuthInvalidUserException) {
            errorMessage = "No account found with this email.";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = "Incorrect password. Please try again.";
        } else {
            errorMessage = "Login failed. Please check your credentials.";
        }
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Method to save login details if Remember Me is checked
    private void saveLoginDetails(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_REMEMBER_ME, true);
        editor.apply();
    }

    // Method to clear login details on logout
    private void clearLoginDetails() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_REMEMBER_ME);
        editor.apply();
    }

    // Method to toggle password visibility on eye icon click
    private boolean togglePasswordVisibility(MotionEvent event, EditText editText, boolean isPwdField) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                int cursorPosition = editText.getSelectionEnd();
                if (isPwdField) {
                    passwordVisible = !passwordVisible;
                    editText.setInputType(passwordVisible ?
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                editText.setSelection(cursorPosition); // Preserve cursor position
                return true;
            }
        }
        return false;
    }
}
