package com.example.chatpet.ui.journal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.chatpet.R;
import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.logic.JournalGenerator;

import java.time.LocalDate;
import java.util.List;

public class JournalFragment extends Fragment {
    private static final String TAG = "JournalActivity";
    private RecyclerView rvJournal;
    private JournalAdapter journalAdapter;
    private JournalGenerator journalGenerator;
    private JournalRepository journalRepository;
    private ImageButton sendButton;
    private TextView outputText;
    private ProgressBar progressBar;
    private SearchView searchView;
    private boolean favState = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_journal, container, false);


        // Schedule daily journal generation work
        JournalGenerator.getInstance().scheduleJournalWork(requireContext());

        // Setup
        journalGenerator = JournalGenerator.getInstance();
        journalRepository = JournalRepository.getInstance();
        initializeViews(view);
        setupRecyclerView();

        // Search bar set up
        searchBar();

        // Entries logic setup
        loadJournalEntries();
        //setUpEntries();
        //generateToday();

        // Set up bookmark button for fav entries
        favButton();

        return view;
    }

    private void initializeViews(View view) {
        sendButton = view.findViewById(R.id.sendButton);
        searchView = view.findViewById(R.id.searchView);

        rvJournal = view.findViewById(R.id.rv_journal);
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
        journalAdapter = new JournalAdapter(requireContext());
        rvJournal.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvJournal.setAdapter(journalAdapter);
    }

    private void loadJournalEntries() {
        List<JournalEntry> entries;// = journalRepository.getAllJournalEntries();

        if(!favState) {
            entries = journalRepository.getAllJournalEntries();
        }
        else {
            entries = journalRepository.getFavEntries();
        }

        // For testing: samples
//        if (entries.isEmpty()) {
//            // Generate a sample entry for today
////            JournalEntry entry = new JournalEntry(LocalDate.now(), "Before clicking, todays' entry.");
////            journalRepository.saveJournalEntry(entry);
//
//            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 2), "second sample set up"));
//            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 1), "third sample set up"));
//            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 23), "fouth sample set up"));
//            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 17), "fifth sample set up"));
//            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 18), "sixth sample set up"));
//            journalRepository.saveJournalEntry(new JournalEntry(LocalDate.of(2025, 10, 6), "seventh sample set up"));
//        }

        journalAdapter.setEntries(entries);
    }

    private void favButton() {
        sendButton.setOnClickListener(v -> {
            Log.e(TAG, "bookmark button clicked\nPrev state: fav is " + favState + ", now: " + !favState);
            favState = !favState;

            loadJournalEntries();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh RecyclerView data each time pull up journal
        loadJournalEntries();
    }

    // For testing setup
    private void setUpEntries() {
        String prompt = "fed me fish and we chatted 3 times.";
        Log.e(TAG, "Prompt: " + prompt);

        // Button click = run LLM
        sendButton.setOnClickListener(v -> {
            LocalDate testDate = LocalDate.of(2025, 10, 20);
            JournalEntry entry = new JournalEntry(testDate.toString(), "first");

            Log.i(TAG, "Before Size of journalEntries: " + journalRepository.getAllJournalEntries().size());
            journalRepository.saveJournalEntry(entry);
            Log.i(TAG, "After Size of journalEntries: " + journalRepository.getAllJournalEntries().size());

            entry = journalRepository.getJournalEntryByDate(testDate);
            Log.i(TAG, "entry: " + entry.getReport());

            if (entry == null) {
                entry = new JournalEntry(testDate.toString(), "null");
                entry.setReport(prompt);
                journalRepository.saveJournalEntry(entry);
                Log.i(TAG, "new Size of journalEntries: " + journalRepository.getAllJournalEntries().size());
            }
            if (entry.getReport() == null) {
                entry.setReport(prompt);
                journalRepository.updateJournalEntry(testDate, entry);
                Log.i(TAG, "updated Size of journalEntries: " + journalRepository.getAllJournalEntries().size());
            }

            Log.e(TAG, "Generating Journal entry...");

            journalGenerator.generateDailyEntry(requireContext(), testDate, new JournalGenerator.LlmCallback() {
                @Override
                public void onLoading() {
                    requireActivity().runOnUiThread(() -> {
                        Log.e(TAG, "thinking...");
                    });
                }

                @Override
                public void onSuccess(String result) {
                    requireActivity().runOnUiThread(() -> {
                        Log.e(TAG, "Generated Entry:\n\n" + result);

                        // Update repository with new entry text
                        JournalEntry updatedEntry = journalRepository.getJournalEntryByDate(testDate);
                        if (updatedEntry == null) {
                            updatedEntry = new JournalEntry(testDate.toString(), "");
                        }
                        updatedEntry.setEntry(result);
                        journalRepository.updateJournalEntry(testDate, updatedEntry);

                        // Refresh RecyclerView
                        List<JournalEntry> updatedList = journalRepository.getAllJournalEntries();
                        journalAdapter.setEntries(updatedList);


                        Toast.makeText(requireContext(), "Journal entry created!", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    requireActivity().runOnUiThread(() -> {
                        Log.e(TAG, "Error: " + errorMessage);

                    });
                }
            });
        });
    }

    private void generateToday() {
        String prompt = "fed me fish and we chatted 3 times.";
        Log.e(TAG, "ChatPet Prompt: " + prompt);

        // Button click = run LLM
        sendButton.setOnClickListener(v -> {
            Log.e(TAG, "bookmark button clicked");
            JournalEntry today = journalRepository.getJournalEntryByDate(LocalDate.now());
            if (today != null) {
                Log.e(TAG, "today journalEntry is exist");
                if(today.getReport() == null) {
                    Log.e(TAG, "today journalEntry exist but report is null");
                    today.setReport(prompt);
                }
            } else {
                Log.e(TAG, "today journalEntry is null!");
                // optionally create a new one or show an error
                return;
            }

            //today.setReport(prompt); // testing

            Log.i(TAG, today.getDate() + ": " + today.getReport());

            Log.e(TAG, "Generating journal entry...");
            LocalDate todayDate = LocalDate.parse(today.getDate());
            journalGenerator.generateDailyEntry(requireContext(), todayDate, new JournalGenerator.LlmCallback() {
                @Override
                public void onLoading() {
                    requireActivity().runOnUiThread(() -> {
                        Log.e(TAG, "writing...");

                    });
                }

                @Override
                public void onSuccess(String result) {
                    requireActivity().runOnUiThread(() -> {
                        Log.e(TAG, "Generated Entry:\n\n" + result);

                        // Refresh RecyclerView
                        List<JournalEntry> updatedList = journalRepository.getAllJournalEntries();
                        journalAdapter.setEntries(updatedList);

                        Toast.makeText(requireContext(), "Journal entry created!", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    requireActivity().runOnUiThread(() -> {
                        Log.e(TAG, "Error: " + errorMessage);

                    });
                }
            });
        });
    }

}