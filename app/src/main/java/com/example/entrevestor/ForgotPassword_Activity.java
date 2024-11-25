package com.example.entrevestor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword_Activity extends AppCompatActivity {

    private EditText emailOrPhoneEditText;
    private Button resetPasswordButton;
    private TextView backToLoginTextView;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        emailOrPhoneEditText = findViewById(R.id.emailOrPhoneEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backToLoginTextView = findViewById(R.id.backToLoginTextView);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePasswordReset();
            }
        });

        backToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginActivity
                Intent intent = new Intent(ForgotPassword_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handlePasswordReset() {
        String emailOrPhone = emailOrPhoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(emailOrPhone)) {
            emailOrPhoneEditText.setError("Please enter your registered email");
            emailOrPhoneEditText.requestFocus();
            return;
        }

        if (!isValidEmail(emailOrPhone)) {
            emailOrPhoneEditText.setError("Enter a valid email");
            emailOrPhoneEditText.requestFocus();
            return;
        }

        // Send password reset email if email is valid
        auth.sendPasswordResetEmail(emailOrPhone)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPassword_Activity.this, "Password reset link sent to " + emailOrPhone, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPassword_Activity.this, "Failed to send reset email. Please check your email.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
}
