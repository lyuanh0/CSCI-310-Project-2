package com.example.chatpet.ui.journal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.chatpet.R;
import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.logic.JournalGenerator;

import java.time.LocalDate;
import java.util.List;

public class JournalActivity extends AppCompatActivity {
    private static final String TAG = "JournalActivity";
    private RecyclerView rvJournal;
    private JournalAdapter journalAdapter;
    private JournalGenerator journalGenerator;
    private JournalRepository journalRepository;
    private ImageButton sendButton;
    private TextView outputText;
    private ProgressBar progressBar;
    private TextView promptHeader;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        // Schedule daily journal generation work
        JournalGenerator.getInstance().scheduleJournalWork(this);


        // Setup
        journalGenerator = JournalGenerator.getInstance();
        journalRepository = JournalRepository.getInstance();
        initializeViews();
        setupRecyclerView();

        // Search bar set up
        searchBar();

        // Entries logic setup
        loadJournalEntries();
        //setUpEntries();

        generateToday();

    }

    private void initializeViews() {
        sendButton = findViewById(R.id.sendButton);
        outputText = findViewById(R.id.outputText);
        progressBar = findViewById(R.id.progressBar);
        promptHeader = findViewById(R.id.headerText);
        searchView = findViewById(R.id.searchView);

        rvJournal = findViewById(R.id.rv_journal);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pet Journal");
        }
    }

    private void searchBar() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                journalAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                journalAdapter.filter(newText);
                return true;
            }
        });
    }

    private void setupRecyclerView() {
        journalAdapter = new JournalAdapter(this);
        rvJournal.setLayoutManager(new LinearLayoutManager(this));
        rvJournal.setAdapter(journalAdapter);
    }

    private void loadJournalEntries() {
        List<JournalEntry> entries = journalRepository.getAllJournalEntries();

        // For testing: samples
        if (entries.isEmpty()) {
            // Generate a sample entry for today
//            JournalEntry entry = new JournalEntry(LocalDate.now(), "Before clicking, todays' entry.");
//            journalRepository.saveJournalEntry(entry);

            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 2), "second sample set up"));
            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 1), "third sample set up"));
            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 23), "fouth sample set up"));
            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 17), "fifth sample set up"));
            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 18), "sixth sample set up"));
            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 6), "seventh sample set up"));
        }

        journalAdapter.setEntries(entries);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh RecyclerView data each time pull up journal
        loadJournalEntries();
    }

    // For testing setup
    private void setUpEntries() {
        String prompt = "fed me fish and we chatted 3 times.";
        promptHeader.setText("ChatPet Prompt: " + prompt);

        // Button click = run LLM
        sendButton.setOnClickListener(v -> {
            LocalDate testDate = LocalDate.of(2025, 10, 20);
            JournalEntry entry = new JournalEntry(testDate, "first");

            Log.i(TAG, "Before Size of journalEntries: " + journalRepository.getAllJournalEntries().size());
            journalRepository.saveJournalEntry(entry);
            Log.i(TAG, "After Size of journalEntries: " + journalRepository.getAllJournalEntries().size());

            entry = journalRepository.getJournalEntryByDate(testDate);
            Log.i(TAG, "entry: " + entry.getReport());

            if (entry == null) {
                entry = new JournalEntry(testDate, "null");
                entry.setReport(prompt);
                journalRepository.saveJournalEntry(entry);
                Log.i(TAG, "new Size of journalEntries: " + journalRepository.getAllJournalEntries().size());
            }
            if (entry.getReport() == null) {
                entry.setReport(prompt);
                journalRepository.updateJournalEntry(testDate, entry);
                Log.i(TAG, "updated Size of journalEntries: " + journalRepository.getAllJournalEntries().size());
            }


            progressBar.setVisibility(View.VISIBLE);
            outputText.setText("Generating journal entry...");

            journalGenerator.generateDailyEntry(this, testDate, new JournalGenerator.LlmCallback() {
                @Override
                public void onLoading() {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.VISIBLE);
                        outputText.setText("Thinking...");
                    });
                }

                @Override
                public void onSuccess(String result) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        outputText.setText("Generated Entry:\n\n" + result);

                        // Update repository with new entry text
                        JournalEntry updatedEntry = journalRepository.getJournalEntryByDate(testDate);
                        if (updatedEntry == null) {
                            updatedEntry = new JournalEntry(testDate, "");
                        }
                        updatedEntry.setEntry(result);
                        journalRepository.updateJournalEntry(testDate, updatedEntry);

                        // Refresh RecyclerView
                        List<JournalEntry> updatedList = journalRepository.getAllJournalEntries();
                        journalAdapter.setEntries(updatedList);


                        Toast.makeText(JournalActivity.this, "Journal entry created!", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        outputText.setText("Error: " + errorMessage);
                    });
                }
            });
        });
    }

    private void generateToday() {

        String prompt = "fed me fish and we chatted 3 times.";
        promptHeader.setText("ChatPet Prompt: " + prompt);

        // Button click = run LLM
        sendButton.setOnClickListener(v -> {
            Log.e(TAG, "button clicked");
            JournalEntry today = journalRepository.getJournalEntryByDate(LocalDate.now());
            if (today != null) {
                Log.e(TAG, "today journalEntry is exist");
                today.setReport("went to sleep");
            } else {
                Log.e(TAG, "today journalEntry is null!");
                // optionally create a new one or show an error
            }

            today.setReport(prompt); // testing

            Log.i(TAG, today.getDate() + ": " + today.getReport());

            progressBar.setVisibility(View.VISIBLE);
            outputText.setText("Generating journal entry...");

            journalGenerator.generateDailyEntry(this, today.getDate(), new JournalGenerator.LlmCallback() {
                @Override
                public void onLoading() {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.VISIBLE);
                        outputText.setText("Writting...");
                    });
                }

                @Override
                public void onSuccess(String result) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        outputText.setText("Generated Entry:\n\n" + result);

                        // Refresh RecyclerView
                        List<JournalEntry> updatedList = journalRepository.getAllJournalEntries();
                        journalAdapter.setEntries(updatedList);

                        Toast.makeText(JournalActivity.this, "Journal entry created!", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        outputText.setText("Error: " + errorMessage);
                    });
                }
            });
        });
    }

}