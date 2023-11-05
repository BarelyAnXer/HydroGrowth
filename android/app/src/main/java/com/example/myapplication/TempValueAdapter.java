package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TempValueAdapter extends RecyclerView.Adapter<TempValueAdapter.TempValueViewHolder> {
    private List<TempValue> tempValueList;

    public TempValueAdapter(List<TempValue> tempValueList) {
        this.tempValueList = tempValueList;
    }

    @NonNull
    @Override
    public TempValueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_temp_value, parent, false);
        return new TempValueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TempValueViewHolder holder, int position) {
        TempValue tempValue = tempValueList.get(position);
        holder.bind(tempValue);
    }

    @Override
    public int getItemCount() {
        return tempValueList.size();
    }

    public static class TempValueViewHolder extends RecyclerView.ViewHolder {
        private TextView tempValueTextView;
        private TextView dateTextView;

        public TempValueViewHolder(@NonNull View itemView) {
            super(itemView);
            tempValueTextView = itemView.findViewById(R.id.tempValueTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(TempValue tempValue) {
            tempValueTextView.setText(tempValue.getTempValue());
            dateTextView.setText(tempValue.getDate().toString()); // You might want to format the date
        }
    }
}
