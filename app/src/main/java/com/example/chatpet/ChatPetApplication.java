package com.example.chatpet;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.logic.JournalGenerator;

import java.time.LocalDate;
import java.util.List;

public class ChatPetApplication extends Application implements DefaultLifecycleObserver {
    private static final String TAG = "JournalChatPetApplication";

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
        LocalDate today = LocalDate.now();

        // Create a blank entry for today
        JournalEntry todayEntry = journalRepo.getJournalEntryByDate(today);
        if (todayEntry == null) {
            todayEntry = new JournalEntry(today.toString(), "");
            //todayEntry.setReport("went to the beach");
            journalRepo.saveJournalEntry(todayEntry);
            Log.i(TAG, "Created placeholder journal entry for " + today);
        }

        // Generate entries for past days with existing reports
        List<JournalEntry> allEntries = journalRepo.getAllJournalEntries();
        Log.i(TAG, "Entries size: " + allEntries.size());

        for (JournalEntry entry : allEntries) {
            LocalDate entryDate = LocalDate.parse(entry.getDate());
            if (entryDate.isBefore(today) && entry.getEntry().isEmpty() &&
                    (entry.getReport() != null) ) {

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
                    (entry.getReport() == null) ) {
                Log.i(TAG, "Deleting empty entry/report " + entry.getDate());

                journalRepo.deleteJournalEntry(entryDate);

            }
        }

        Log.i(TAG, "Entries size after: " + journalRepo.getAllJournalEntries().size());

        // Schedule tonight’s generation
        JournalGenerator.getInstance().scheduleJournalWork(this);
    }
}
