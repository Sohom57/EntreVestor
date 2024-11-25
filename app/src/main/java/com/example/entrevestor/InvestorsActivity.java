package com.example.entrevestor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvestorsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApplicantAdapter adapter;
    private List<HashMap<String, String>> applicantList; // Replace User with HashMap for simplicity

    private DatabaseReference databaseReference;
    private String projectId; // Pass this from the previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investor);

        // Get project ID from intent
        projectId = getIntent().getStringExtra("projectId");

        if (projectId == null) {
            Toast.makeText(this, "Project ID is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view_investors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list and adapter
        applicantList = new ArrayList<>();
        adapter = new ApplicantAdapter(this, applicantList);
        recyclerView.setAdapter(adapter);

        // Fetch applicants from Firebase
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Projects")
                .child(projectId)
                .child("applicants");

        loadApplicants();
    }

    private void loadApplicants() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                applicantList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, String> user = (HashMap<String, String>) snapshot.getValue();
                    if (user != null) {
                        applicantList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InvestorsActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
