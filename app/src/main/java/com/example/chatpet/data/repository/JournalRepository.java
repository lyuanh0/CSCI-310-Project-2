package com.example.chatpet.data.repository;

import com.example.chatpet.data.model.JournalEntry;
import java.util.ArrayList;
import java.util.Date;
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

    public boolean saveJournalEntry(JournalEntry entry) {
        journalEntries.add(entry);
        return true;
    }

    public List<JournalEntry> getAllJournalEntries() {
        return new ArrayList<>(journalEntries);
    }

    public JournalEntry getJournalEntryByDate(Date date) {
        for (JournalEntry entry : journalEntries) {
            if (isSameDay(entry.getDate(), date)) {
                return entry;
            }
        }
        return null;
    }

    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;

        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);
    }

    public boolean deleteJournalEntry(Date date) {
        JournalEntry entryToRemove = getJournalEntryByDate(date);
        if (entryToRemove != null) {
            journalEntries.remove(entryToRemove);
            return true;
        }
        return false;
    }
}