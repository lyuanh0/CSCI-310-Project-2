package com.example.chatpet.data.repository;

import com.example.chatpet.data.model.JournalEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JournalRepository {
    private static JournalRepository instance;
    private List<JournalEntry> journalEntries;

    private JournalRepository() {
        journalEntries = new ArrayList<>();
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
    }

    public void updateJournalEntry(LocalDate date, JournalEntry newEntry) {
        for (int i = 0; i < journalEntries.size(); i++) {
            JournalEntry entry = journalEntries.get(i);
            if (entry.getDate().isEqual(date)) {
                // Replace the old entry with the new one
                journalEntries.set(i, newEntry);
            }
        }

    }

    public JournalEntry getJournalEntryByDate(LocalDate date) {
        for (JournalEntry entry : journalEntries) {
            if (entry.getDate().isEqual(date)) {
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
    }
}