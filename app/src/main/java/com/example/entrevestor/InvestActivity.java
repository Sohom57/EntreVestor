package com.example.entrevestor;

import android.os.Bundle;
import android.widget.TextView;
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
import java.util.List;

public class InvestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView investorCountText;
    private DatabaseReference databaseReference;
    private InvestAdapter investAdapter;
    private List<String> investorList;
    private String projectId; // Unique project ID passed via Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);

        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_view_investors);
        investorCountText = findViewById(R.id.investor_count);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        investorList = new ArrayList<>();
        investAdapter = new InvestAdapter(this, investorList);
        recyclerView.setAdapter(investAdapter);

        // Get projectId from Intent
        projectId = getIntent().getStringExtra("PROJECT_ID");
        if (projectId == null) {
            Toast.makeText(this, "Project not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Firebase reference to the project investors
        databaseReference = FirebaseDatabase.getInstance().getReference("Projects")
                .child(projectId).child("Investors");

        loadInvestors();
    }

    private void loadInvestors() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                investorList.clear();
                for (DataSnapshot investorSnapshot : snapshot.getChildren()) {
                    String investorName = investorSnapshot.getValue(String.class);
                    if (investorName != null) {
                        investorList.add(investorName);
                    }
                }
                // Update UI
                investAdapter.notifyDataSetChanged();
                investorCountText.setText("Total Investors: " + investorList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InvestActivity.this, "Failed to load investors", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
