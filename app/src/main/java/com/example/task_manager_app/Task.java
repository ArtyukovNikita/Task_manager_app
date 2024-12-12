package com.example.task_manager_app;

public class Task {
    private int id;
    private String name;
    private String status;
    private String date;
    private String time;
    private String description;
    private String priority;

    public Task(int id, String name, String status, String date, String time, String description, String priority) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.date = date;
        this.time = time;
        this.description = description;
        this.priority = priority;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }
}
