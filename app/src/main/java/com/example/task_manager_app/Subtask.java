package com.example.task_manager_app;

public class Subtask {
    private int id;
    private int taskId;
    private String name;
    private String status;

    public Subtask(int id, int taskId, String name, String status) {
        this.id = id;
        this.taskId = taskId;
        this.name = name;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
