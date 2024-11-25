package com.example.entrevestor;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userName, userEmail, userBio;

    private DatabaseReference userRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
        profileImage = findViewById(R.id.user_profile_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userBio = findViewById(R.id.user_bio);

        // Get user ID from intent
        userId = getIntent().getStringExtra("userId");

        // Initialize Firebase reference
        userRef = FirebaseDatabase.getInstance().getReference("user_profiles").child(userId);

        // Load user profile
        loadUserProfile();
    }

    private void loadUserProfile() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String bio = snapshot.child("bio").getValue(String.class);
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                    // Set data to views
                    userName.setText(name != null ? name : "N/A");
                    userEmail.setText(email != null ? "Email: " + email : "Email: N/A");
                    userBio.setText(bio != null ? bio : "No bio available");

                    // Load profile image
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(UserProfileActivity.this).load(profileImageUrl).into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.no_profile_pic); // Default image
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }
}
