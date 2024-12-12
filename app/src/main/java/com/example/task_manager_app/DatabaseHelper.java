package com.example.task_manager_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "task_manager.db";
    private static final int DATABASE_VERSION = 1;

    // Task table columns
    public static final String TASK_TABLE = "tasks";
    public static final String COLUMN_TASK_ID = "task_id";
    public static final String COLUMN_TASK_NAME = "task_name";
    public static final String COLUMN_TASK_STATUS = "task_status";
    public static final String COLUMN_TASK_DATE = "task_date";
    public static final String COLUMN_TASK_TIME = "task_time";
    public static final String COLUMN_TASK_DESCRIPTION = "task_description";
    public static final String COLUMN_TASK_PRIORITY = "task_priority";

    // Subtask table columns
    public static final String SUBTASK_TABLE = "subtasks";
    public static final String COLUMN_SUBTASK_ID = "subtask_id";
    public static final String COLUMN_SUBTASK_TASK_ID = "task_id";
    public static final String COLUMN_SUBTASK_NAME = "subtask_name";
    public static final String COLUMN_SUBTASK_STATUS = "subtask_status";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tasks table
        String createTaskTable = "CREATE TABLE " + TASK_TABLE + " ("
                + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TASK_NAME + " TEXT, "
                + COLUMN_TASK_STATUS + " TEXT, "
                + COLUMN_TASK_DATE + " TEXT, "
                + COLUMN_TASK_TIME + " TEXT, "
                + COLUMN_TASK_DESCRIPTION + " TEXT, "
                + COLUMN_TASK_PRIORITY + " TEXT);";

        // Create subtasks table
        String createSubtaskTable = "CREATE TABLE " + SUBTASK_TABLE + " ("
                + COLUMN_SUBTASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SUBTASK_TASK_ID + " INTEGER, "
                + COLUMN_SUBTASK_NAME + " TEXT, "
                + COLUMN_SUBTASK_STATUS + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_SUBTASK_TASK_ID + ") REFERENCES " + TASK_TABLE + "(" + COLUMN_TASK_ID + "));";

        db.execSQL(createTaskTable);
        db.execSQL(createSubtaskTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SUBTASK_TABLE);
        // Create new tables
        onCreate(db);
    }

    // Add a task to the database
    public long addTask(String taskName, String taskStatus, String taskDate, String taskTime, String taskDescription, String taskPriority) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, taskName);
        values.put(COLUMN_TASK_STATUS, taskStatus);
        values.put(COLUMN_TASK_DATE, taskDate);
        values.put(COLUMN_TASK_TIME, taskTime);
        values.put(COLUMN_TASK_DESCRIPTION, taskDescription);
        values.put(COLUMN_TASK_PRIORITY, taskPriority);

        return db.insert(TASK_TABLE, null, values);
    }

    // Update an existing task
    public int updateTask(int taskId, String taskName, String taskStatus, String taskDate, String taskTime, String taskDescription, String taskPriority) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, taskName);
        values.put(COLUMN_TASK_STATUS, taskStatus);
        values.put(COLUMN_TASK_DATE, taskDate);
        values.put(COLUMN_TASK_TIME, taskTime);
        values.put(COLUMN_TASK_DESCRIPTION, taskDescription);
        values.put(COLUMN_TASK_PRIORITY, taskPriority);

        return db.update(TASK_TABLE, values, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    // Delete a task by ID
    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TASK_TABLE, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    // Add a subtask to the database
    public long addSubtask(int taskId, String subtaskName, String subtaskStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBTASK_TASK_ID, taskId);
        values.put(COLUMN_SUBTASK_NAME, subtaskName);
        values.put(COLUMN_SUBTASK_STATUS, subtaskStatus);

        return db.insert(SUBTASK_TABLE, null, values);
    }

    // Update a subtask
    public int updateSubtask(int subtaskId, String subtaskName, String subtaskStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBTASK_NAME, subtaskName);
        values.put(COLUMN_SUBTASK_STATUS, subtaskStatus);

        return db.update(SUBTASK_TABLE, values, COLUMN_SUBTASK_ID + " = ?", new String[]{String.valueOf(subtaskId)});
    }

    // Delete a subtask by ID
    public void deleteSubtask(int subtaskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SUBTASK_TABLE, COLUMN_SUBTASK_ID + " = ?", new String[]{String.valueOf(subtaskId)});
    }

    // Get all tasks from the database
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Запрос для получения всех задач
        Cursor cursor = db.query(TASK_TABLE, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Получаем индексы столбцов
                int taskIdIndex = cursor.getColumnIndex(COLUMN_TASK_ID);
                int taskNameIndex = cursor.getColumnIndex(COLUMN_TASK_NAME);
                int taskStatusIndex = cursor.getColumnIndex(COLUMN_TASK_STATUS);
                int taskDateIndex = cursor.getColumnIndex(COLUMN_TASK_DATE);
                int taskTimeIndex = cursor.getColumnIndex(COLUMN_TASK_TIME);
                int taskDescriptionIndex = cursor.getColumnIndex(COLUMN_TASK_DESCRIPTION);
                int taskPriorityIndex = cursor.getColumnIndex(COLUMN_TASK_PRIORITY);

                // Проверяем, что индексы столбцов не равны -1 (столбцы существуют)
                if (taskIdIndex != -1 && taskNameIndex != -1 && taskStatusIndex != -1 &&
                        taskDateIndex != -1 && taskTimeIndex != -1 && taskDescriptionIndex != -1 &&
                        taskPriorityIndex != -1) {

                    // Создаем объект задачи из данных в курсоре
                    Task task = new Task(
                            cursor.getInt(taskIdIndex),
                            cursor.getString(taskNameIndex),
                            cursor.getString(taskStatusIndex),
                            cursor.getString(taskDateIndex),
                            cursor.getString(taskTimeIndex),
                            cursor.getString(taskDescriptionIndex),
                            cursor.getString(taskPriorityIndex)
                    );
                    tasks.add(task);
                } else {
                    // Если какой-то из столбцов не найден, можно залогировать ошибку или обработать по-другому
                    // Например, логируем это:
                    Log.e("DatabaseHelper", "Some columns are missing in the result set / В результирующем наборе отсутствуют некоторые столбцы.");
                }
            }
            cursor.close();
        }

        return tasks;
    }


    // Get all subtasks for a specific task
    public List<Subtask> getSubtasksForTask(int taskId) {
        List<Subtask> subtasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Запрос для получения подзадач для конкретной задачи
        Cursor cursor = db.query(SUBTASK_TABLE, null, COLUMN_SUBTASK_TASK_ID + " = ?", new String[]{String.valueOf(taskId)}, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Получаем индексы столбцов
                int subtaskIdIndex = cursor.getColumnIndex(COLUMN_SUBTASK_ID);
                int subtaskTaskIdIndex = cursor.getColumnIndex(COLUMN_SUBTASK_TASK_ID);
                int subtaskNameIndex = cursor.getColumnIndex(COLUMN_SUBTASK_NAME);
                int subtaskStatusIndex = cursor.getColumnIndex(COLUMN_SUBTASK_STATUS);

                // Проверяем, что индексы столбцов не равны -1 (столбцы существуют)
                if (subtaskIdIndex != -1 && subtaskTaskIdIndex != -1 && subtaskNameIndex != -1 && subtaskStatusIndex != -1) {
                    // Создаем объект подзадачи из данных в курсоре
                    Subtask subtask = new Subtask(
                            cursor.getInt(subtaskIdIndex),
                            cursor.getInt(subtaskTaskIdIndex),
                            cursor.getString(subtaskNameIndex),
                            cursor.getString(subtaskStatusIndex)
                    );
                    subtasks.add(subtask);
                } else {
                    // Если какой-то из столбцов не найден, можно залогировать ошибку или обработать по-другому
                    Log.e("DatabaseHelper", "Some columns are missing in the result set for subtasks");
                }
            }
            cursor.close();
        }

        return subtasks;
    }

}
