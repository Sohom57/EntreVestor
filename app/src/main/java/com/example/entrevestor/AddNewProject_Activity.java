package com.example.entrevestor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddNewProject_Activity extends AppCompatActivity {

    private EditText projectName, projectDescription, moneyNeeded, links, duration;
    private ImageView profilePicture;

    private DatabaseReference projectsRef;
    private StorageReference storageRef;
    private Uri profileImageUri; // For the project image to be uploaded
    private String userProfileImageUrl; // To store the user profile image URL
    private String userName; // To store the user name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_project);

        // Initialize Firebase instances
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        assert userId != null;
        projectsRef = FirebaseDatabase.getInstance().getReference("Projects").child(userId); // Store under user's ID
        storageRef = FirebaseStorage.getInstance().getReference("ProjectImages");

        // Initialize views
        projectName = findViewById(R.id.project_name);
        projectDescription = findViewById(R.id.project_description);
        moneyNeeded = findViewById(R.id.money_needed);
        links = findViewById(R.id.links);
        duration = findViewById(R.id.duration);
        profilePicture = findViewById(R.id.profile_picture);
        Button addPhotoButton = findViewById(R.id.add_photo_button);
        Button submitButton = findViewById(R.id.submit_button);

        // Get the user's profile image URL and username
        getUserProfileData(userId);

        // Set button click listeners
        addPhotoButton.setOnClickListener(v -> chooseImage());
        submitButton.setOnClickListener(v -> submitProject());
    }

    private void getUserProfileData(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Fetching user profile data (profile image and username)
        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                userProfileImageUrl = snapshot.child("profileImage").getValue(String.class);
                userName = snapshot.child("name").getValue(String.class); // Fetch the username
            } else {
                userProfileImageUrl = null; // No profile image found
                userName = null; // No username found
            }
        }).addOnFailureListener(e -> {
            userProfileImageUrl = null; // Handle failure gracefully
            userName = null;
        });
    }

    private void chooseImage() {
        // Start an intent to choose an image from the gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImageUri = data.getData();
            profilePicture.setImageURI(profileImageUri); // Display the selected image
        }
    }

    private void submitProject() {
        String name = projectName.getText().toString().trim();
        String description = projectDescription.getText().toString().trim();
        String money = moneyNeeded.getText().toString().trim();
        String link = links.getText().toString().trim();
        String projectDuration = duration.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || money.isEmpty() || link.isEmpty() || projectDuration.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload project data to Firebase
        uploadProjectData(name, description, money, link, projectDuration);
    }

    private void uploadProjectData(String name, String description, String money, String link, String duration) {
        String projectId = projectsRef.push().getKey(); // Generate a unique key for the project
        if (projectId != null && profileImageUri != null) {
            // Upload the image to Firebase Storage
            StorageReference filePath = storageRef.child(projectId + ".jpg");
            filePath.putFile(profileImageUri).addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                // Store the project data in the Firebase Database
                Map<String, Object> projectData = new HashMap<>();
                projectData.put("Name", name);
                projectData.put("Description", description);
                projectData.put("MoneyNeeded", money);
                projectData.put("Link", link);
                projectData.put("Duration", duration);
                projectData.put("ProjectImage", uri.toString()); // Project's uploaded image URL
                projectData.put("ProfileImage", userProfileImageUrl); // User's profile image URL
                projectData.put("userName", userName); // Username of the user creating the project

                projectsRef.child(projectId).setValue(projectData)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddNewProject_Activity.this, "Project added successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddNewProject_Activity.this, NewsFeedActivity.class));
                                finish(); // Close this activity
                            } else {
                                Toast.makeText(AddNewProject_Activity.this, "Failed to add project", Toast.LENGTH_SHORT).show();
                            }
                        });
            })).addOnFailureListener(e -> Toast.makeText(AddNewProject_Activity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
