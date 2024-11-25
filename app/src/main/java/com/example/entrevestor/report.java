package com.example.entrevestor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class report extends AppCompatActivity {

    private EditText projectIdField, projectNameField, userNameField, descriptionField;

    // Database reference for the "Reports" table
    private DatabaseReference reportsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize Firebase Database reference for reports
        reportsRef = FirebaseDatabase.getInstance().getReference().child("Reports");

        // Initialize UI elements
        projectIdField = findViewById(R.id.project_id_field);
        projectNameField = findViewById(R.id.project_name_field);
        userNameField = findViewById(R.id.user_name_field);
        descriptionField = findViewById(R.id.description_field);
        Button reportButton = findViewById(R.id.report_button);

        // Set up button click listener
        reportButton.setOnClickListener(v -> submitReport());
    }

    // Method to handle report submission
    private void submitReport() {
        String projectId = projectIdField.getText().toString().trim();
        String projectName = projectNameField.getText().toString().trim();
        String userName = userNameField.getText().toString().trim();
        String description = descriptionField.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(projectId) || TextUtils.isEmpty(projectName) ||
                TextUtils.isEmpty(userName) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique report ID
        String reportId = reportsRef.push().getKey();

        // Prepare report data
        HashMap<String, Object> reportData = new HashMap<>();
        reportData.put("reportId", reportId);
        // Replace with actual user ID when implementing authentication
        String currentUserId = "USER_ID_HERE";
        reportData.put("userId", currentUserId); // The ID of the user submitting the report
        reportData.put("projectId", projectId);
        reportData.put("projectName", projectName);
        reportData.put("userName", userName);
        reportData.put("description", description);
        reportData.put("timestamp", System.currentTimeMillis());

        // Save report to the database
        assert reportId != null;
        reportsRef.child(reportId).setValue(reportData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(report.this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(report.this, NewsFeedActivity.class));
                clearFields();
            } else {
                Toast.makeText(report.this, "Failed to submit report. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to clear input fields after submission
    private void clearFields() {
        projectIdField.setText("");
        projectNameField.setText("");
        userNameField.setText("");
        descriptionField.setText("");
    }
}
