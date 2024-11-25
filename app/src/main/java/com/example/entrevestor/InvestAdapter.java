package com.example.entrevestor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InvestAdapter extends RecyclerView.Adapter<InvestAdapter.InvestorViewHolder> {

    private Context context;
    private List<String> investorList;

    public InvestAdapter(Context context, List<String> investorList) {
        this.context = context;
        this.investorList = investorList;
    }

    @NonNull
    @Override
    public InvestorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_investor, parent, false);
        return new InvestorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestorViewHolder holder, int position) {
        holder.investorName.setText(investorList.get(position));
    }

    @Override
    public int getItemCount() {
        return investorList.size();
    }

    public static class InvestorViewHolder extends RecyclerView.ViewHolder {
        TextView investorName;

        public InvestorViewHolder(@NonNull View itemView) {
            super(itemView);
            investorName = itemView.findViewById(R.id.investor_name);
        }
    }
}
