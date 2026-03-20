package com.example.expandedtodolistapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final OnTaskListener onTaskListener;

    public TaskAdapter(List<Task> taskList, OnTaskListener onTaskListener) {
        this.taskList = taskList;
        this.onTaskListener = onTaskListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view, onTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, position);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView date;
        private final TextView notes;
        private final CheckBox completed;
        private final Button btnEdit;
        private final Button btnDelete;
        private final OnTaskListener onTaskListener;

        public TaskViewHolder(View itemView, OnTaskListener onTaskListener) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTaskTitle);
            date = itemView.findViewById(R.id.tvTaskDate);
            notes = itemView.findViewById(R.id.tvTaskNote);
            completed = itemView.findViewById(R.id.cbTaskCompleted);
            btnEdit = itemView.findViewById(R.id.btnEditTask);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
            this.onTaskListener = onTaskListener;

            // Edit task
            btnEdit.setOnClickListener(v -> onTaskListener.onEditTask(getAdapterPosition()));

            // Delete task
            btnDelete.setOnClickListener(v -> onTaskListener.onDeleteTask(getAdapterPosition()));
        }

        public void bind(Task task, int position) {
            title.setText(task.getTitle());
            date.setText(task.getDate());
            notes.setText(task.getNotes());
            completed.setChecked(task.isCompleted());

            // Listen for checkbox state changes
            completed.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) { // Ensure user interaction
                    task.setCompleted(isChecked);
                    onTaskListener.onTaskCompletionChanged(task, position);
                }
            });
        }
    }

    public interface OnTaskListener {
        void onEditTask(int position);

        void onDeleteTask(int position);

        void onTaskCompletionChanged(Task task, int position); // New method for checkbox updates
    }
}
