package com.example.chatpet.data.model;

import java.util.Date;

public class Message {
    private String sender;
    private String text;
    private Date timestamp;

    public Message() {
        this.timestamp = new Date();
    }

    public Message(String sender, String text) {
        this();
        this.sender = sender;
        this.text = text;
    }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}