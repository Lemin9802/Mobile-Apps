package com.example.todolistapp;

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
        private final CheckBox completed;
        private final Button btnEdit;
        private final Button btnDelete;
        private final OnTaskListener onTaskListener;

        public TaskViewHolder(View itemView, OnTaskListener onTaskListener) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTaskTitle);
            date = itemView.findViewById(R.id.tvTaskDate);
            completed = itemView.findViewById(R.id.cbTaskCompleted);
            btnEdit = itemView.findViewById(R.id.btnEditTask);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
            this.onTaskListener = onTaskListener;

            // Thiết lập sự kiện cho nút sửa
            btnEdit.setOnClickListener(v -> onTaskListener.onEditTask(getAdapterPosition()));

            // Thiết lập sự kiện cho nút xóa
            btnDelete.setOnClickListener(v -> onTaskListener.onDeleteTask(getAdapterPosition()));
        }

        public void bind(Task task, int position) {
            title.setText(task.getTitle());
            date.setText(task.getDate());
            completed.setChecked(task.isCompleted());

            completed.setOnCheckedChangeListener((buttonView, isChecked) -> task.setCompleted(isChecked));
        }
    }

    public interface OnTaskListener {
        void onEditTask(int position);
        void onDeleteTask(int position);
    }
}
