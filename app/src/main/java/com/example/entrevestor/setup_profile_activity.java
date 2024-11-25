package com.example.entrevestor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.entrevestor.profile_activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class setup_profile_activity extends AppCompatActivity {

    private ImageView profilePicture;
    private EditText nameInput, emailInput, phoneInput, bioInput;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;
    private StorageReference profileImageStorage;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        profileImageStorage = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        profilePicture = findViewById(R.id.profile_picture);
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input); // Phone input field
        bioInput = findViewById(R.id.bio_input);
        Button uploadButton = findViewById(R.id.upload_button);
        Button saveButton = findViewById(R.id.save_button);

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        profilePicture.setImageURI(imageUri);
                    }
                }
        );

        uploadButton.setOnClickListener(v -> openGallery());
        saveButton.setOnClickListener(v -> saveUserProfile());
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(Intent.createChooser(galleryIntent, "Select Profile Picture"));
    }

    private void saveUserProfile() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String bio = bioInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and upload a profile picture.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User is not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        StorageReference filePath = profileImageStorage.child(userId + ".jpg");

        filePath.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                filePath.getDownloadUrl().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        String downloadUrl = task1.getResult().toString();

                        // Adding all profile data including phone and setting isProfileSetup to true
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("email", email);
                        userMap.put("phone", phone);
                        userMap.put("bio", bio);
                        userMap.put("profileImage", downloadUrl);
                        userMap.put("isProfileSetup", true); // This flag indicates profile setup is complete

                        userDatabase.child(userId).updateChildren(userMap).addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                Toast.makeText(setup_profile_activity.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(setup_profile_activity.this, profile_activity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(setup_profile_activity.this, "Failed to save profile. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(setup_profile_activity.this, "Failed to get profile image URL.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(setup_profile_activity.this, "Failed to upload profile image.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}