package com.example.todolistapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskListener {

    private List<Task> taskList;
    private TaskAdapter taskAdapter;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("todo_prefs", MODE_PRIVATE);
        gson = new Gson();
        taskList = loadTasksFromPreferences();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(taskAdapter);

        findViewById(R.id.btnAddTask).setOnClickListener(v -> showTaskDialog(-1)); // Mở dialog thêm task
    }

    private List<Task> loadTasksFromPreferences() {
        String json = sharedPreferences.getString("task_list", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Task>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    private void saveTasksToPreferences() {
        String json = gson.toJson(taskList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("task_list", json);
        editor.apply();
    }

    private void showTaskDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(position >= 0 ? "Edit Task" : "Add Task");
        builder.setView(R.layout.dialog_add_task);

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText etTitle = dialog.findViewById(R.id.etTaskTitle);
        EditText etDate = dialog.findViewById(R.id.etTaskDate);
        EditText etNotes = dialog.findViewById(R.id.etTaskNotes);
        Button btnSaveTask = dialog.findViewById(R.id.btnSaveTask);
        Button btnCancelTask = dialog.findViewById(R.id.btnCancelTask);

        if (position >= 0) {
            Task task = taskList.get(position);
            etTitle.setText(task.getTitle());
            etDate.setText(task.getDate());
            etNotes.setText(task.getNotes());
        }

        btnSaveTask.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String date = etDate.getText().toString();
            String notes = etNotes.getText().toString();

            if (title.isEmpty() || date.isEmpty()) {
                return;
            }

            if (position >= 0) {
                taskList.get(position).setTitle(title);
                taskList.get(position).setDate(date);
                taskList.get(position).setNotes(notes);
            } else {
                // Thêm task mới
                Task newTask = new Task(title, date, notes, false); // Mặc định task mới không hoàn thành
                taskList.add(newTask);
            }

            taskAdapter.notifyDataSetChanged();
            saveTasksToPreferences();
            dialog.dismiss();
        });

        btnCancelTask.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onDeleteTask(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    taskList.remove(position);
                    taskAdapter.notifyItemRemoved(position);
                    saveTasksToPreferences();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onEditTask(int position) {
        showTaskDialog(position);
    }
}