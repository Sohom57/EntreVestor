package com.example.entrevestor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sign_up_activity extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail,
            editTextRegisterDob, editTextRegisterMobile,
            editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender, radioGroupRegisterRole;
    private RadioButton radioGroupRegisterGenderSelected, radioGroupRegisterRoleSelected;
    private boolean passwordVisiblePwd = false;
    private boolean passwordVisibleConfirmPwd = false;

    // Declare FirebaseAuth and DatabaseReference
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toast.makeText(sign_up_activity.this, "SignUp Now", Toast.LENGTH_SHORT).show();

        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDob = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();
        radioGroupRegisterRole = findViewById(R.id.radio_group_register_role);
        radioGroupRegisterRole.clearCheck();
        Button buttonRegister = findViewById(R.id.reg_btn);

        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase Auth and Database Reference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioGroupRegisterGenderSelected = findViewById(selectedGenderId);

                int selectedRoleId = radioGroupRegisterRole.getCheckedRadioButtonId();
                radioGroupRegisterRoleSelected = findViewById(selectedRoleId);

                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextRegisterDob.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
                String textGender, textRole;

                // Validate input fields
                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(sign_up_activity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    editTextRegisterFullName.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(sign_up_activity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(sign_up_activity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(sign_up_activity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    editTextRegisterDob.requestFocus();
                    return;
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(sign_up_activity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    return;
                } else if (radioGroupRegisterRole.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(sign_up_activity.this, "Please select your Role", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(sign_up_activity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.requestFocus();
                    return;
                } else if (textMobile.length() != 11) {
                    Toast.makeText(sign_up_activity.this, "Mobile number should be 11 digits", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(sign_up_activity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.requestFocus();
                    return;
                } else if (textPwd.length() < 8) {
                    Toast.makeText(sign_up_activity.this, "Password should be at least 8 characters", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.requestFocus();
                    return;
                } else if (!textPwd.matches(".*[0-9].*")) {
                    Toast.makeText(sign_up_activity.this, "Password should contain at least one digit", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.requestFocus();
                    return;
                } else if (!textPwd.matches(".*[@#$%^&+=!].*")) {
                    Toast.makeText(sign_up_activity.this, "Password should contain at least one special character", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(sign_up_activity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPwd.requestFocus();
                    return;
                } else if (!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(sign_up_activity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.requestFocus();
                    return;
                }

                textGender = radioGroupRegisterGenderSelected.getText().toString();
                textRole = radioGroupRegisterRoleSelected.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                registerUser(textFullName, textEmail, textDoB, textGender, textRole, textMobile, textPwd);
            }
        });

        // Add OnTouchListeners to toggle password visibility
        editTextRegisterPwd.setOnTouchListener((v, event) -> togglePasswordVisibility(event, editTextRegisterPwd, true));
        editTextRegisterConfirmPwd.setOnTouchListener((v, event) -> togglePasswordVisibility(event, editTextRegisterConfirmPwd, false));
    }

    private boolean togglePasswordVisibility(MotionEvent event, EditText editText, boolean isPwdField) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                int cursorPosition = editText.getSelectionEnd();
                if (isPwdField) {
                    passwordVisiblePwd = !passwordVisiblePwd;
                    editText.setInputType(passwordVisiblePwd ?
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    passwordVisibleConfirmPwd = !passwordVisibleConfirmPwd;
                    editText.setInputType(passwordVisibleConfirmPwd ?
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                editText.setSelection(cursorPosition); // Preserve cursor position
                return true;
            }
        }
        return false;
    }

    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textRole, String textMobile, String textPwd) {
        firebaseAuth.createUserWithEmailAndPassword(textEmail, textPwd)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToDatabase(user, textFullName, textEmail, textDoB, textGender, textRole, textMobile);
                        } else {
                            Toast.makeText(sign_up_activity.this, "User is null after registration", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(sign_up_activity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveUserToDatabase(FirebaseUser user, String textFullName, String textEmail, String textDoB, String textGender, String textRole, String textMobile) {
        String userId = user.getUid();
        User newUser = new User(textFullName, textEmail, textDoB, textGender, textRole, textMobile);

        databaseReference.child(userId).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(sign_up_activity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(sign_up_activity.this, setup_profile_activity.class));
                        finish();
                    } else {
                        Toast.makeText(sign_up_activity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public class User {
        public String fullName, email, dob, gender, role, mobile;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String Username, String Email, String DoB, String Gender, String Role, String Mobile) {
            this.fullName = Username;
            this.email = Email;
            this.dob = DoB;
            this.gender = Gender;
            this.role = Role;
            this.mobile = Mobile;
        }
    }
}
