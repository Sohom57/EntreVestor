package com.example.entrevestor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder> {

    private Context context;
    private List<HashMap<String, String>> applicantList;

    public ApplicantAdapter(Context context, List<HashMap<String, String>> applicantList) {
        this.context = context;
        this.applicantList = applicantList;
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_applicant, parent, false);
        return new ApplicantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantViewHolder holder, int position) {
        HashMap<String, String> user = applicantList.get(position);

        String name = user.get("name");
        String profileImageUrl = user.get("profileImageUrl");
        String userId = user.get("id");

        holder.nameTextView.setText(name);
        Glide.with(context).load(profileImageUrl).into(holder.profileImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("userId", userId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }

    public static class ApplicantViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView profileImageView;

        public ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.applicant_name);
            profileImageView = itemView.findViewById(R.id.applicant_profile_image);
        }
    }
}
