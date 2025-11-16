package com.example.chatpet.data.repository;

import android.util.Log;
import android.widget.Toast;

import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.logic.AuthManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JournalRepository {
    private static final String TAG = "JournalRepository";
    private static JournalRepository instance;
    private List<JournalEntry> journalEntries;
    private JournalRepository() {
        journalEntries = new ArrayList<>();
    }

    public interface OnJournalLoadedListener {
        void onLoaded(List<JournalEntry> entries);
    }

    public static JournalRepository getInstance() {
        if (instance == null) {
            instance = new JournalRepository();
        }
        return instance;
    }

    public List<JournalEntry> getAllJournalEntries() {
        journalEntries.sort(Comparator.comparing(JournalEntry::getDate).reversed());
        return new ArrayList<>(journalEntries);
    }

    public JournalEntry getLatestEntry() {
        if (journalEntries == null || journalEntries.isEmpty()) {
            return null; // no entries available
        }
        journalEntries.sort(Comparator.comparing(JournalEntry::getDate).reversed());
        return journalEntries.get(0);
    }


    public void saveJournalEntry(JournalEntry entry) {
        journalEntries.add(entry);
        Log.e(TAG, "Added journalEntry to entry list: " + entry.getDate());

        updateJournalSnapshot();
    }

    public void updateJournalEntry(LocalDate date, JournalEntry newEntry) {
        for (int i = 0; i < journalEntries.size(); i++) {
            JournalEntry entry = journalEntries.get(i);
            LocalDate entryDate = LocalDate.parse(entry.getDate());

            if (entryDate.isEqual(date)) {
                // Replace the old entry with the new one
                journalEntries.set(i, newEntry);
                Log.e(TAG, "Updated journalEntry in entry list: " + newEntry.getDate());

            }
        }

        updateJournalSnapshot();
    }

    public JournalEntry getJournalEntryByDate(LocalDate date) {
        for (JournalEntry entry : journalEntries) {
            LocalDate entryDate = LocalDate.parse(entry.getDate());

            if (entryDate.isEqual(date)) {
                return entry;
            }
        }
        return null;
    }

    public void deleteJournalEntry(LocalDate date) {
        JournalEntry entryToRemove = getJournalEntryByDate(date);
        if (entryToRemove != null) {
            journalEntries.remove(entryToRemove);
        }
        Log.e(TAG, "Deleted journalEntry in entry list: " + date);

        updateJournalSnapshot();
    }

    public void clearAllEntries() {
        journalEntries.clear();
        updateJournalSnapshot();
    }

    private void updateJournalSnapshot(){
        String uid = AuthManager.currentUser().getUid();
        if (journalEntries == null || journalEntries.isEmpty()) {
            System.out.println("No journal entries to upload.");
            return;
        }

       DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("journalEntries");

        userRef.setValue(journalEntries).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                System.out.println("Journal snapshot updated successfully");
            }else{
                System.err.println("Failed to update journal snapshot:" );
            }
        });
    }

    public void loadJournalSnapshot(OnJournalLoadedListener listener) {
        String uid = AuthManager.currentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("journalEntries");

        userRef.get().addOnCompleteListener(task -> {
            journalEntries.clear();

            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();

                //loop through each journal entry and rebuild the list
                for (DataSnapshot entrySnapshot : snapshot.getChildren()) {
                    JournalEntry entry = entrySnapshot.getValue(JournalEntry.class);
                    if (entry != null) {
                        journalEntries.add(entry);
                    }
                }

                Log.i(TAG, "Loaded " + journalEntries.size() + " journal entries from Firebase");
            } else {
                Log.e(TAG, "Failed to load journal snapshot", task.getException());
            }

            // Notify loading is done
            if (listener != null) {
                listener.onLoaded(new ArrayList<>(journalEntries));
            }
        });
    }

}