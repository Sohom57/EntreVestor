package com.example.entrevestor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profile_activity extends AppCompatActivity {
    private ImageView profileImage;
    private TextView tvRole, tvName, tvBio, tvEmailValue, tvPhoneValue;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth and Database Reference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // UI Elements
        profileImage = findViewById(R.id.profile_image);
        tvRole = findViewById(R.id.tv_role);
        tvName = findViewById(R.id.tv_name);
        tvBio = findViewById(R.id.tv_bio);
        tvEmailValue = findViewById(R.id.tv_email_value);
        tvPhoneValue = findViewById(R.id.tv_phone_value);
        Button btnEditProfile = findViewById(R.id.btn_edit_profile);
        Button btnLogout = findViewById(R.id.btn_signout);
        Button btnMyProjects = findViewById(R.id.btn_my_projects_or_investments);
        Button aNP = findViewById(R.id.add_new_project);
        Button posts= findViewById(R.id.posts);

        // Load User Data
        loadUserData();

        // Set Up Edit Profile Button
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(profile_activity.this, setup_profile_activity.class);
            startActivity(intent);
        });

        //To View Posts
        posts.setOnClickListener(v -> {
            Intent intent = new Intent(profile_activity.this, NewsFeedActivity.class);
            startActivity(intent);
        });

        // To Add New Project
        aNP.setOnClickListener(v->{
            Intent intent = new Intent(profile_activity.this, AddNewProject_Activity.class);
            startActivity(intent);
        });

        // Set Up Logout Button
        btnLogout.setOnClickListener(v -> {
            clearLoginDetails();
            firebaseAuth.signOut();
            Toast.makeText(profile_activity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(profile_activity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadUserData() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("fullName").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String bio = dataSnapshot.child("bio").getValue(String.class);
                        String phone = dataSnapshot.child("mobile").getValue(String.class);
                        String role = dataSnapshot.child("role").getValue(String.class);
                        String profileImageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                        tvName.setText(name);
                        tvRole.setText(role);
                        tvBio.setText(bio);
                        tvEmailValue.setText(email != null ? email : "Not specified");
                        tvPhoneValue.setText(phone != null ? phone : "Not specified");

                        if (profileImageUrl != null) {
                            Glide.with(profile_activity.this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_profile)
                                    .into(profileImage);
                        }
                    } else {
                        Toast.makeText(profile_activity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(profile_activity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(profile_activity.this, "No user is currently logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(profile_activity.this, LoginActivity.class));
            finish();
        }
    }

    private void clearLoginDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(LoginActivity.KEY_EMAIL);
        editor.remove(LoginActivity.KEY_PASSWORD);
        editor.remove(LoginActivity.KEY_REMEMBER_ME);
        editor.apply();
    }
}
