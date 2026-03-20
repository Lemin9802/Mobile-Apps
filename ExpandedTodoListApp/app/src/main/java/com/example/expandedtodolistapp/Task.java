package com.example.expandedtodolistapp;

public class Task {

    private long id; // Unique identifier for SQLite
    private String firebaseId; // Unique identifier for Firebase
    private String title; // Task title
    private String date; // Task date (format: yyyy-MM-dd HH:mm)
    private String notes; // Additional notes
    private boolean isCompleted; // Task completion status

    // Constructor for tasks with both SQLite and Firebase IDs
    public Task(long id, String firebaseId, String title, String date, String notes, boolean isCompleted) {
        this.id = id;
        this.firebaseId = firebaseId;
        this.title = title;
        this.date = date;
        this.notes = notes;
        this.isCompleted = isCompleted;
    }

    // Overloaded constructor for tasks without Firebase ID (e.g., new tasks)
    public Task(long id, String title, String date, String notes, boolean isCompleted) {
        this(id, null, title, date, notes, isCompleted);
    }

    // Overloaded constructor for tasks without SQLite ID (e.g., new tasks for Firebase only)
    public Task(String title, String date, String notes, boolean isCompleted) {
        this(-1, null, title, date, notes, isCompleted);
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
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

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", firebaseId='" + firebaseId + '\'' +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", notes='" + notes + '\'' +
                ", isCompleted=" + isCompleted +
                '}';
    }
}
