package com.example.expandedtodolistapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskListener {

    private List<Task> taskList; // List of tasks
    private TaskAdapter taskAdapter; // Adapter for RecyclerView
    private DatabaseHelper databaseHelper; // SQLite helper
    private FirebaseHelper firebaseHelper; // Firebase helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SQLite and Firebase helpers
        databaseHelper = new DatabaseHelper(this);
        firebaseHelper = new FirebaseHelper();

        // Load tasks from SQLite
        taskList = databaseHelper.getAllTasks();

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(taskAdapter);

        // Button to add a new task
        findViewById(R.id.btnAddTask).setOnClickListener(v -> showTaskDialog(-1));
    }

    // Show dialog for adding or editing a task
    private void showTaskDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(position >= 0 ? "Edit Task" : "Add Task");
        builder.setView(R.layout.dialog_add_task);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Find views in dialog
        EditText etTitle = dialog.findViewById(R.id.etTaskTitle);
        EditText etDate = dialog.findViewById(R.id.etTaskDate);
        EditText etNotes = dialog.findViewById(R.id.etTaskNotes);
        Button btnSaveTask = dialog.findViewById(R.id.btnSaveTask);
        Button btnCancelTask = dialog.findViewById(R.id.btnCancelTask);

        // If editing, prefill the fields
        if (position >= 0) {
            Task task = taskList.get(position);
            etTitle.setText(task.getTitle());
            etDate.setText(task.getDate());
            etNotes.setText(task.getNotes());
        }

        // Save button action
        btnSaveTask.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String date = etDate.getText().toString(); // Format: yyyy-MM-dd HH:mm
            String notes = etNotes.getText().toString();

            if (title.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Title and Date are required", Toast.LENGTH_SHORT).show();
                return;
            }

            Task task;
            if (position >= 0) {
                // Update existing task
                task = taskList.get(position);
                task.setTitle(title);
                task.setDate(date);
                task.setNotes(notes);

                // Update in SQLite
                databaseHelper.updateTask(task);
                // Update in Firebase
                firebaseHelper.updateTaskInFirebase(task);
            } else {
                // Add new task
                task = new Task(title, date, notes, false);
                long id = databaseHelper.addTask(task); // Save to SQLite
                task.setId(id);

                // Add task to Firebase and sync with Firebase ID
                firebaseHelper.addTaskToFirebase(task, firebaseId -> {
                    task.setFirebaseId(firebaseId); // Set Firebase ID
                    // Update Firebase ID in SQLite
                    databaseHelper.updateTask(task);
                });
                taskList.add(task); // Add to task list in RecyclerView
            }

            // Set reminder
            long reminderTimeInMillis = convertDateToMillis(date);
            if (reminderTimeInMillis > 0) {
                setReminder(task, reminderTimeInMillis);
            }

            taskAdapter.notifyDataSetChanged(); // Refresh the list
            dialog.dismiss();
        });

        // Cancel button action
        btnCancelTask.setOnClickListener(v -> dialog.dismiss());
    }


    // Convert date string to milliseconds
    private long convertDateToMillis(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateString);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Set reminder using AlarmManager
    private void setReminder(Task task, long timeInMillis) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title", task.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            Log.d("Reminder", "Reminder set for: " + task.getTitle() + " at " + timeInMillis);
        }
    }

    // Handle task deletion
    @Override
    public void onDeleteTask(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Task task = taskList.get(position);
                    // Gọi firebaseHelper để xóa task
                    firebaseHelper.deleteTaskFromFirebase(task.getFirebaseId(), success -> {
                        if (success) {
                            // Xóa thành công từ Firebase, giờ xóa từ SQLite
                            databaseHelper.deleteTask(task.getId()); // Remove from SQLite
                            taskList.remove(position);
                            taskAdapter.notifyItemRemoved(position); // Cập nhật UI
                        } else {
                            Toast.makeText(this, "Failed to delete task from Firebase", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }


    // Handle task editing
    @Override
    public void onEditTask(int position) {
        showTaskDialog(position);
    }

    @Override
    public void onTaskCompletionChanged(Task task, int position) {
        // Update task completion status in SQLite and Firebase
        databaseHelper.updateTask(task);
        firebaseHelper.updateTaskInFirebase(task);
        Toast.makeText(this, "Task \"" + task.getTitle() + "\" updated.", Toast.LENGTH_SHORT).show();
    }
}
