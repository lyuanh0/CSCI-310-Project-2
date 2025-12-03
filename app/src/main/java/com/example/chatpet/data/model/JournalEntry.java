package com.example.chatpet.data.model;

import android.util.Log;

import com.example.chatpet.data.repository.JournalRepository;

import java.time.LocalDate;

public class JournalEntry {
    private String date;
    private String entry;
    private String report;
    private boolean fav;

    public JournalEntry() {
        this.date = LocalDate.now().toString();
        this.report = "";
        this.fav = false;
    }

    public JournalEntry(String date, String entry) {
        this.date = date;
        this.entry = entry;
        this.report = "";
        this.fav = false;
    }

    public String getDate() { return date; }

    public String getEntry() { return entry; }

    public String getReport() { return report; }

    public boolean isFav() { return fav; }

    public void setDate(String date) { this.date = date; }

    public void setEntry(String entry) { this.entry = entry; }

    public void setReport(String report) { this.report = report; }

    public void setFav(boolean fav) { this.fav = fav; }

    public void addToReport(String interaction) {
        this.report += interaction + "\n";
        Log.e("JournalEntryReport", "Added: " + interaction);
        JournalRepository.getInstance().updateJournalEntry(LocalDate.parse(this.date), this);

    }

}