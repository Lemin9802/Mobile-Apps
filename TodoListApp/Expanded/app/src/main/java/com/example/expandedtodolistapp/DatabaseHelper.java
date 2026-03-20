package com.example.expandedtodolistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo_list.db";
    private static final int DATABASE_VERSION = 2; // Incremented for schema changes

    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id"; // SQLite ID
    private static final String COLUMN_FIREBASE_ID = "firebase_id"; // Firebase ID
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_COMPLETED = "completed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FIREBASE_ID + " TEXT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_NOTES + " TEXT, " +
                COLUMN_COMPLETED + " INTEGER)";
        db.execSQL(CREATE_TASKS_TABLE);
        Log.d("DatabaseHelper", "Database created: " + CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle upgrades such as adding the firebase_id column
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_TASKS + " ADD COLUMN " + COLUMN_FIREBASE_ID + " TEXT");
            Log.d("DatabaseHelper", "Database upgraded to version " + newVersion + " by adding column: " + COLUMN_FIREBASE_ID);
        }
    }

    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DATE, task.getDate());
        values.put(COLUMN_NOTES, task.getNotes());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_FIREBASE_ID, task.getFirebaseId()); // Save Firebase ID

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        Log.d("DatabaseHelper", "Task added with SQLite ID: " + id + ", Firebase ID: " + task.getFirebaseId());
        return id;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1
                );
                task.setFirebaseId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIREBASE_ID)));
                tasks.add(task);
                Log.d("DatabaseHelper", "Task retrieved: " + task.getTitle());
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DATE, task.getDate());
        values.put(COLUMN_NOTES, task.getNotes());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_FIREBASE_ID, task.getFirebaseId()); // Update Firebase ID

        int rowsUpdated = db.update(TABLE_TASKS, values, COLUMN_ID + "=?", new String[]{String.valueOf(task.getId())});
        db.close();
        Log.d("DatabaseHelper", "Task updated: " + task.getTitle() + ", Rows affected: " + rowsUpdated);
        return rowsUpdated;
    }

    public void deleteTask(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_TASKS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        Log.d("DatabaseHelper", "Task deleted with SQLite ID: " + id + ", Rows affected: " + rowsDeleted);
    }

    public void deleteTaskByFirebaseId(String firebaseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_TASKS, COLUMN_FIREBASE_ID + "=?", new String[]{firebaseId});
        db.close();
        Log.d("DatabaseHelper", "Task deleted with Firebase ID: " + firebaseId + ", Rows affected: " + rowsDeleted);
    }
}
