package com.example.todolistapp;

public class Task {

    private String title;
    private String date;
    private String notes;
    private boolean isCompleted;

    public Task(String title, String date, String notes, boolean isCompleted) {
        this.title = title;
        this.date = date;
        this.notes = notes;
        this.isCompleted = isCompleted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
