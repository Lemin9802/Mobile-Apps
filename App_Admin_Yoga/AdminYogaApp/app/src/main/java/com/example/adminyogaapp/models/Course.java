package com.example.adminyogaapp.models;

public class Course {
    private String id;
    private String name;
    private String dayOfWeek;
    private String timeStart;
    private int capacity;
    private float duration;
    private float price;
    private String classType;
    private String description;

    public Course() {
    }

    public Course(String name, String dayOfWeek, String timeStart, int capacity, float duration, float price, String classType, String description) {
        this.name = name;
        this.dayOfWeek = dayOfWeek;
        this.timeStart = timeStart;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.classType = classType;
        this.description = description;
    }

    public Course(String id, String name, String dayOfWeek, String timeStart, int capacity, float duration, float price, String classType, String description) {
        this.id = id;
        this.name = name;
        this.dayOfWeek = dayOfWeek;
        this.timeStart = timeStart;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.classType = classType;
        this.description = description;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

