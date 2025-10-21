package com.example.chatpet.data.model;

import java.util.Date;

public class JournalEntry {
    private Date date;
    private String entry;
    private String summary;

    public JournalEntry() {
        this.date = new Date();
    }

    public JournalEntry(Date date, String entry) {
        this.date = date;
        this.entry = entry;
    }

    public String generateEntry(Date date) {
        // This will be replaced with LLM-generated content
        return "Dear Diary, Today was a wonderful day! My owner played with me...";
    }

    public void displayEntry() {
        System.out.println("Date: " + date);
        System.out.println("Entry: " + entry);
    }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getEntry() { return entry; }
    public void setEntry(String entry) { this.entry = entry; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}