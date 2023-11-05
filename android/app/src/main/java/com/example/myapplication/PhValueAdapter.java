package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhValueAdapter extends RecyclerView.Adapter<PhValueAdapter.PhValueViewHolder> {
    private List<PhValue> phValueList;

    public PhValueAdapter(List<PhValue> phValueList) {
        this.phValueList = phValueList;
    }

    @NonNull
    @Override
    public PhValueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ph_value, parent, false);
        return new PhValueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhValueViewHolder holder, int position) {
        PhValue phValue = phValueList.get(position);
        holder.bind(phValue);
    }

    @Override
    public int getItemCount() {
        return phValueList.size();
    }

    public static class PhValueViewHolder extends RecyclerView.ViewHolder {
        private TextView phValueTextView;
        private TextView dateTextView;

        public PhValueViewHolder(@NonNull View itemView) {
            super(itemView);
            phValueTextView = itemView.findViewById(R.id.phValueTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(PhValue phValue) {
            phValueTextView.setText(phValue.getPhValue());
            dateTextView.setText(phValue.getDate().toString()); // You might want to format the date
        }
    }
}
