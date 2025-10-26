package com.example.chatpet.ui.journal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatpet.R;
import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.data.remote.ChatViewModel;
import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.logic.JournalGenerator;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class JournalActivity extends AppCompatActivity {
    private static final String TAG = "JournalActivity";
    private RecyclerView rvJournal;
    private JournalAdapter journalAdapter;
    private JournalGenerator journalGenerator;
    private JournalRepository journalRepository;
    //given example
    private Button sendButton;
    private TextView outputText;
    private ProgressBar progressBar;
    private TextView promptHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        // Setup
        journalGenerator = JournalGenerator.getInstance();
        journalRepository = JournalRepository.getInstance();
        initializeViews();
        setupRecyclerView();

        // Entries logic setup
        setUpEntries();

        loadJournalEntries();

    }

    private void initializeViews() {
        sendButton = findViewById(R.id.sendButton);
        outputText = findViewById(R.id.outputText);
        progressBar = findViewById(R.id.progressBar);
        promptHeader = findViewById(R.id.headerText);

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
            JournalEntry entry = new JournalEntry(LocalDate.now(), "sample set up");
            journalRepository.saveJournalEntry(entry);
        }

        journalAdapter.setEntries(entries);
    }

    private void setUpEntries() {
        String prompt = "Played fetch at the park, ate lunch, and took a nap.";
        promptHeader.setText("ChatPet Prompt: " + prompt);

        LocalDate testDate = LocalDate.of(2025, 10, 20);
        JournalEntry entry = new JournalEntry(testDate, "");

        Log.i(TAG, "Before Size of journalEntries: " + journalRepository.getAllJournalEntries().size());
        journalRepository.saveJournalEntry(entry);
        Log.i(TAG, "After Size of journalEntries: " + journalRepository.getAllJournalEntries().size());

        entry = journalRepository.getJournalEntryByDate(testDate);
        Log.i(TAG, "entry: " + entry.getReport());

        if (entry == null) {
            entry = new JournalEntry(testDate, "");
            entry.setReport(prompt);
            journalRepository.saveJournalEntry(entry);
            Log.i(TAG, "new Size of journalEntries: " + journalRepository.getAllJournalEntries().size());
        }
        if (entry.getReport() == null) {
            entry = new JournalEntry(testDate, "");
            entry.setReport(prompt);
            journalRepository.updateJournalEntry(testDate, entry);
            Log.i(TAG, "updated Size of journalEntries: " + journalRepository.getAllJournalEntries().size());
        }

        // Button click = run LLM
        sendButton.setOnClickListener(v -> {
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

//        // given example
//        journalGenerator = new ViewModelProvider(this).get(JournalGenerator.class);
//        String report = "Went to school. Fed 3 times, went to sleep.";
//        sendButton = findViewById(R.id.sendButton);
//        outputText = findViewById(R.id.outputText);
//        progressBar = findViewById(R.id.progressBar);
//        sendButton.setOnClickListener(v -> {
//            //String prompt = inputText.getText().toString().trim();
//            String prompt = report;
//            if (!prompt.isEmpty()) {
//                String modelPath = getString(R.string.model_path);
//                journalGenerator.generateJournalEntry(this, modelPath, prompt, new JournalGenerator.LlmCallback() {
//                    @Override
//                    public void onLoading() {
//                        progressBar.setVisibility(View.VISIBLE);
//                        outputText.setText("Thinking...");
//                    }
//
//                    @Override
//                    public void onSuccess(String result) {
//                        progressBar.setVisibility(View.GONE);
//                        outputText.setText(result);
//                    }
//
//                    @Override
//                    public void onError(String errorMessage) {
//                        progressBar.setVisibility(View.GONE);
//                        outputText.setText("Error: " + errorMessage);
//                    }
//                });
//            }
//        });

    }

}