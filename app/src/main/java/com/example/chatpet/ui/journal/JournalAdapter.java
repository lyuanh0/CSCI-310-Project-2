package com.example.chatpet.ui.journal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatpet.R;
import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {
    private List<JournalEntry> entries;
    private Context context;

    public JournalAdapter(Context context) {
        this.context = context;
        this.entries = new ArrayList<>();
    }

    public void setEntries(List<JournalEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_journal_entry, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        JournalEntry entry = entries.get(position);

        holder.tvDate.setText(TimeUtils.formatDate(entry.getDate()));
        holder.tvEntry.setText(entry.getEntry());

        // Expand/collapse functionality
        holder.itemView.setOnClickListener(v -> {
            if (holder.tvEntry.getMaxLines() == 3) {
                holder.tvEntry.setMaxLines(Integer.MAX_VALUE);
            } else {
                holder.tvEntry.setMaxLines(3);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvEntry;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvEntry = itemView.findViewById(R.id.tv_entry);
        }
    }
}