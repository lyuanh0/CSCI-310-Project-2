package com.example.chatpet.ui.journal;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatpet.R;
import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.logic.JournalGenerator;

import java.util.Date;
import java.util.List;

public class JournalActivity extends AppCompatActivity {
    private RecyclerView rvJournal;
    private JournalAdapter journalAdapter;
    private JournalGenerator journalGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        journalGenerator = JournalGenerator.getInstance();

        initializeViews();
        setupRecyclerView();
        loadJournalEntries();
    }

    private void initializeViews() {
        rvJournal = findViewById(R.id.rv_journal);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pet Journal");
        }
    }

    private void setupRecyclerView() {
        journalAdapter = new JournalAdapter(this);
        rvJournal.setLayoutManager(new LinearLayoutManager(this));
        rvJournal.setAdapter(journalAdapter);
    }

    private void loadJournalEntries() {
        List<JournalEntry> entries = journalGenerator.getAllEntries();

        if (entries.isEmpty()) {
            // Generate a sample entry for today
            JournalEntry todayEntry = journalGenerator.generateDailyEntry(new Date());
            entries.add(todayEntry);
            Toast.makeText(this, "Generated today's journal entry!", Toast.LENGTH_SHORT).show();
        }

        journalAdapter.setEntries(entries);
    }
}