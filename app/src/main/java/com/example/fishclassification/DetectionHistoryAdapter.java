package com.example.fishclassification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DetectionHistoryAdapter extends RecyclerView.Adapter<DetectionHistoryAdapter.ViewHolder> {

    private List<DetectionHistoryItem> itemList;
    private LayoutInflater inflater;

    // Constructor for the adapter
    public DetectionHistoryAdapter(Context context, List<DetectionHistoryItem> itemList) {
        this.itemList = itemList;
        this.inflater = LayoutInflater.from(context);
    }

    // Method to create new ViewHolder instances
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_detection_history, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to a ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetectionHistoryItem item = itemList.get(position);
        holder.textViewDate.setText(item.getCurrentDate());
        holder.textViewLabel.setText(item.getLabel());
    }

    // Returns the size of the data list
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Inner class for ViewHolder pattern
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewLabel;

        // Constructor for the ViewHolder
        ViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewLabel = itemView.findViewById(R.id.textViewLabel);
        }
    }
}
