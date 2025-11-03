package com.example.chatpet.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.logic.JournalGenerator;

import java.time.LocalDate;

public class JournalWorker extends Worker {
    private static final String TAG = "JournalWorker";
    private JournalRepository journalRepository;


    public JournalWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "Running end-of-day journal generation...");

        journalRepository = journalRepository.getInstance();
        //journalRepository.getJournalEntryByDate(LocalDate.now()).setReport("went to take a walk");
        // Skip generating new entry if nothing actually happened
        if(journalRepository.getJournalEntryByDate(LocalDate.now()).getReport() != null) {
            JournalGenerator.getInstance().generateDailyEntry(
                    getApplicationContext(),
                    LocalDate.now(),
                    new JournalGenerator.LlmCallback() {
                        @Override
                        public void onLoading() {
                        }

                        @Override
                        public void onSuccess(String result) {
                            Log.i(TAG, "Successfully generated journal entry: " + result);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Failed to generate journal entry: " + errorMessage);
                        }
                    }
            );
        }

        return Result.success();
    }
}
