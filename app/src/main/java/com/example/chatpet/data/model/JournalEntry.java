package com.example.chatpet.data.model;

import java.time.LocalDate;
import java.util.Date;

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
    }

    public String generateEntry(Date date) {
        // This will be replaced with LLM-generated content
        return "Dear Diary, Today was a wonderful day! My owner played with me...";
    }

    public void displayEntry() {
        System.out.println("Date: " + date);
        System.out.println("Entry: " + entry);
    }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { this.date = date; }

    public String getEntry() { return entry; }

    public void setEntry(String entry) { this.entry = entry; }

    public String getReport() { return report; }
    public void setReport(String report) { this.report = report; }
    public void addToReport(String interaction) {
        this.report += "\n" + interaction;
    }

}