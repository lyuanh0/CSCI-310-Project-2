package com.example.chatpet;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.logic.AuthManager;
import com.example.chatpet.logic.JournalGenerator;

import java.time.LocalDate;
import java.util.List;

public class ChatPetApplication extends Application implements DefaultLifecycleObserver {
    private static final String TAG = "JournalChatPetApplication";
    private static final String PREFS_NAME = "chatpet_prefs";
    private static final String KEY_LAST_RUN_DATE = "last_run_date";

    @Override
    public void onCreate() {
        super.onCreate();

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        Log.i(TAG, "ChatPetApplication started");
    }

    @Override
    public void onStart(LifecycleOwner owner) {
        Log.i(TAG, "App moved to foreground");

        JournalRepository journalRepo = JournalRepository.getInstance();

        if(AuthManager.currentUser()!=null) {
            journalRepo.loadJournalSnapshot(entries -> {
                Log.i(TAG, "Firebase load complete: " + entries.size() + " entries loaded");
                handleJournalLogic(entries);
            });
        }
    }

    private void handleJournalLogic(List<JournalEntry> allEntries) {
        LocalDate today = LocalDate.now();

        JournalRepository journalRepo = JournalRepository.getInstance();

//        SharedPreferences prefs = getSharedPreferences("journal_prefs", MODE_PRIVATE);
//        String lastScheduledDate = prefs.getString("last_scheduled_date", "");

        Log.i(TAG, "Entries size: " + allEntries.size());

        for (JournalEntry entry : allEntries) {
            LocalDate entryDate = LocalDate.parse(entry.getDate());
            if (entryDate.isBefore(today) && entry.getEntry().isEmpty() &&
                    (entry.getReport() != null && !entry.getReport().isEmpty()) ) {

                Log.i(TAG, "Generating report for " + entry.getDate());
                JournalGenerator.getInstance().generateDailyEntry(
                        getApplicationContext(),
                        entryDate,
                        new JournalGenerator.LlmCallback() {
                            @Override
                            public void onLoading() {}

                            @Override
                            public void onSuccess(String result) {
                                Log.i(TAG, "Generated entry for " + entry.getDate());
                                entry.setEntry(result);
                                journalRepo.updateJournalEntry(entryDate, entry);

                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.e(TAG, "Failed to generate entry for " + entry.getDate() + ": " + errorMessage);
                            }
                        }
                );
            }

            else if (entryDate.isBefore(today) && entry.getEntry().isEmpty() &&
                    (entry.getReport() == null || entry.getReport().isEmpty()) ) {
                Log.i(TAG, "Deleting empty entry/report " + entry.getDate());

                journalRepo.deleteJournalEntry(entryDate);

            }
        }

        Log.i(TAG, "Entries size after: " + journalRepo.getAllJournalEntries().size());
        // Add new entry for today
        JournalEntry todayEntry = journalRepo.getJournalEntryByDate(today);
        if (todayEntry == null ) {
            todayEntry = new JournalEntry(today.toString(), "");
//            todayEntry.setReport("went to the beach");
            journalRepo.saveJournalEntry(todayEntry);
            Log.i(TAG, "Created placeholder journal entry for " + today);
        }

        // Generate entries for past days with existing reports
        allEntries = journalRepo.getAllJournalEntries();
        Log.i(TAG, "Entries size adding today: " + allEntries.size());


        // Schedule tonight’s generation
        JournalGenerator.getInstance().scheduleJournalWork(this);

    }
}
