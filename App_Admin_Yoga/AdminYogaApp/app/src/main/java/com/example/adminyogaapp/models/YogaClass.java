package com.example.adminyogaapp.models;

public class YogaClass {
    private String id;
    private String name;
    private String courseId;
    private String date;
    private String teacher;
    private String comment;

    public YogaClass() {
    }

    public YogaClass(String name, String courseId, String date, String teacher, String comment) {
        this.name = name;
        this.courseId = courseId;
        this.date = date;
        this.teacher = teacher;
        this.comment = comment;
    }

    public YogaClass(String id, String name, String courseId, String date, String teacher, String comment) {
        this.id = id;
        this.name = name;
        this.courseId = courseId;
        this.date = date;
        this.teacher = teacher;
        this.comment = comment;
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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
