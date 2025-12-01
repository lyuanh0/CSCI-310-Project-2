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
import com.example.chatpet.data.model.Message;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {
    private List<JournalEntry> allEntries;  // OG entries
    private List<JournalEntry> displayedEntries;
    private final Context context;

    public JournalAdapter(Context context) {
        this.context = context;
        this.allEntries = new ArrayList<>();
        this.displayedEntries = new ArrayList<>();
    }

    public void setEntries(List<JournalEntry> entries) {
        this.allEntries = new ArrayList<>(entries);

        // Display entries that are not empty
        this.displayedEntries = new ArrayList<>();
        for (JournalEntry e : entries) {
            if (e.getEntry() != null && !e.getEntry().trim().isEmpty()) {
                displayedEntries.add(e);
            }
        }

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
        JournalEntry entry = displayedEntries.get(position);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        LocalDate entryDate = LocalDate.parse(entry.getDate());

        holder.tvDate.setText(entryDate.format(formatter));
        holder.tvEntry.setText(entry.getEntry());
        holder.btnFavorite.setText(entry.isFav() ? "♥" : "♡");

        // Toggle fav button
        holder.btnFavorite.setOnClickListener(v -> {
            boolean newState = !entry.isFav();
            entry.setFav(newState);

            holder.btnFavorite.setText(newState ? "♥" : "♡");
        });

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
        return displayedEntries.size();
    }

    public static class JournalViewHolder extends RecyclerView.ViewHolder {
        TextView btnFavorite;
        TextView tvDate;
        TextView tvEntry;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvEntry = itemView.findViewById(R.id.tv_entry);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }

    // For searching with search bar
    public void filter(String query) {
        query = query.toLowerCase();
        displayedEntries.clear();

        for (JournalEntry entry : allEntries) {
            boolean matchesText = entry.getEntry().toLowerCase().contains(query);

            LocalDate entryDate = LocalDate.parse(entry.getDate());

            String dateStr = entryDate.toString().replace("-", ""); // yyyyMMdd
            boolean matchesDate = dateStr.contains(query);

            String formattedDate = entryDate.format(DateTimeFormatter.ofPattern("MMMM d yyyy")).toLowerCase();
            boolean matchesFormattedDate = formattedDate.contains(query);

            formattedDate = entryDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")).toLowerCase();
            boolean matchesFormattedDate2 = formattedDate.contains(query.replace(",", "")) && !(query.replace(",", "").trim().isEmpty());

            if (matchesText || matchesDate || matchesFormattedDate || matchesFormattedDate2) {
                if (!entry.getEntry().isEmpty()) {
                    displayedEntries.add(entry);
                }
            }
        }
        notifyDataSetChanged();
    }

}