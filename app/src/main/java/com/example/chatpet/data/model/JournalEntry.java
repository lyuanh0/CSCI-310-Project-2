package com.example.chatpet.data.model;

import java.time.LocalDate;

public class JournalEntry {
    private LocalDate date;
    private String entry;
    private String report;

    public JournalEntry() {
        this.date = LocalDate.now();
        this.report = "";
    }

    public JournalEntry(LocalDate date, String entry) {
        this.date = date;
        this.entry = entry;
    }

    public LocalDate getDate() { return date; }


    public String getEntry() { return entry; }

    public String getReport() { return report; }

    public void setDate(LocalDate date) { this.date = date; }
    public void setEntry(String entry) { this.entry = entry; }
    public void setReport(String report) { this.report = report; }
    public void addToReport(String interaction) {
        this.report += "\n" + interaction;
    }

}