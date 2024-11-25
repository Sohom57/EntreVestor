package com.example.entrevestor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.entrevestor.Project;

import java.util.List;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ProjectViewHolder> {

    private Context context;
    private List<Project> projectList;

    public NewsFeedAdapter(Context context, List<Project> projectList) {
        this.context = context;
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);

        // Set data to views
        holder.projectName.setText(project.getName());
        holder.projectDescription.setText(project.getDescription());
        holder.moneyNeeded.setText("Money Needed: " + project.getMoneyNeeded());
        holder.duration.setText("Duration: " + project.getDuration());
        holder.links.setText("Link: " + project.getLink());

        // Load project image with Glide
        if (project.getImageUrl() != null && !project.getImageUrl().isEmpty()) {
            Glide.with(context).load(project.getImageUrl()).into(holder.projectImage);
        } else {
            holder.projectImage.setImageResource(R.drawable.image_bg); // A default image if no URL is provided
        }


        // Handle Apply button click
        holder.btnApply.setOnClickListener(view -> {
            Toast.makeText(context, "Apply button clicked for " + project.getName(), Toast.LENGTH_SHORT).show();
            // Additional code to handle "Apply" functionality
        });

        holder.btnReport.setOnClickListener(view -> {
            Intent intent=new Intent(context, report.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {

        TextView projectName, projectDescription, moneyNeeded, duration, links;
        ImageView projectImage;
        Button btnApply, btnReport;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);

            projectName = itemView.findViewById(R.id.feed_project_name);
            projectDescription = itemView.findViewById(R.id.project_description);
            moneyNeeded = itemView.findViewById(R.id.money_needed);
            duration = itemView.findViewById(R.id.duration);
            links = itemView.findViewById(R.id.links);
            projectImage = itemView.findViewById(R.id.project_image);
            btnApply = itemView.findViewById(R.id.btn_apply);
            btnReport=itemView.findViewById(R.id.btn_report);
        }
    }
}