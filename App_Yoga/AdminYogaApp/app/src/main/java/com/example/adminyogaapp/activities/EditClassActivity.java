package com.example.adminyogaapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adminyogaapp.extras.DbHelper;
import com.example.adminyogaapp.MainActivity;
import com.example.adminyogaapp.R;
import com.example.adminyogaapp.models.YogaClass;

import java.util.ArrayList;

public class EditClassActivity extends AppCompatActivity {

    private Spinner spinnerCourses;
    private EditText editName, editDate, editTeacher, editComment;
    private TextView errorName, errorDate, errorTeacher;
    private Button buttonSave, buttonDelete;
    private String classId;
    private String courseId;
    private ArrayList<String> courseIds;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        dbHelper = new DbHelper(this);
        editName = findViewById(R.id.edit_name);
        errorName = findViewById(R.id.error_name);
        spinnerCourses = findViewById(R.id.spinner_course);
        editDate = findViewById(R.id.edit_date);
        editTeacher = findViewById(R.id.edit_teacher);
        editComment = findViewById(R.id.edit_comment);
        errorDate = findViewById(R.id.error_date);
        errorTeacher = findViewById(R.id.error_teacher);
        buttonSave = findViewById(R.id.button_save_class);

        Intent intent = getIntent();
        classId = intent.getStringExtra("CLASS_ID");

        loadCourses();


        spinnerCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseId = courseIds.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (classId != null) {
            loadClassDetails(classId);
            buttonDelete = findViewById(R.id.button_delete);
            buttonDelete.setVisibility(View.VISIBLE);


            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete(classId);
                    Intent intent = new Intent(EditClassActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClass();
                Intent intent = new Intent(EditClassActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    private void loadCourses() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.TABLE_COURSE, null, null, null, null, null, null);

        ArrayList<String> courseDescriptions = new ArrayList<>();
        courseIds = new ArrayList<>();

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_COURSE_ID));
            String courseName = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_COURSE_NAME));
            Log.d("DBase", id);
            courseIds.add(id);
            courseDescriptions.add(courseName);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courseDescriptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapter);
    }


    private void loadClassDetails(String classId) {
        YogaClass yogaClass = dbHelper.getClassById(classId);

        editName.setText(yogaClass.getName());

        int coursePosition = courseIds.indexOf(yogaClass.getCourseId());

        spinnerCourses.setSelection(coursePosition);
        editDate.setText(yogaClass.getDate());
        editTeacher.setText(yogaClass.getTeacher());
        editComment.setText(yogaClass.getComment());


    }


    private void saveClass() {
        String name = editName.getText().toString();
        String date = editDate.getText().toString();
        String teacher = editTeacher.getText().toString();
        String comment = editComment.getText().toString();

        boolean isValid = true;

        if (name.isEmpty()) {
            errorName.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorName.setVisibility(View.GONE);
        }

        if (date.isEmpty()) {
            errorDate.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorDate.setVisibility(View.GONE);
        }

        if (teacher.isEmpty()) {
            errorTeacher.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorTeacher.setVisibility(View.GONE);
        }

        if (!isValid) {
            return;
        }

        if (classId != null) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DbHelper.COLUMN_CLASS_ID, classId);
            values.put(DbHelper.COLUMN_CLASS_NAME, name);
            values.put(DbHelper.COLUMN_CLASS_COURSE_ID, courseId);
            values.put(DbHelper.COLUMN_CLASS_DATE, date);
            values.put(DbHelper.COLUMN_CLASS_TEACHER, teacher);
            values.put(DbHelper.COLUMN_CLASS_COMMENT, comment);
            YogaClass newClass = new YogaClass(name, courseId, date, teacher, comment);
            newClass.setId(classId);
            db.update(DbHelper.TABLE_CLASS, values, DbHelper.COLUMN_CLASS_ID + "=?", new String[]{String.valueOf(classId)});
            dbHelper.saveClassFirebase(classId, newClass);
            finish();
        } else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DbHelper.COLUMN_CLASS_COURSE_ID, courseId);
            values.put(DbHelper.COLUMN_CLASS_DATE, date);
            values.put(DbHelper.COLUMN_CLASS_NAME, name);
            values.put(DbHelper.COLUMN_CLASS_TEACHER, teacher);
            values.put(DbHelper.COLUMN_CLASS_COMMENT, comment);

            YogaClass newClass = new YogaClass(name, courseId, date, teacher, comment);

            String newId = dbHelper.saveClassFirebase(newClass);
            values.put(DbHelper.COLUMN_CLASS_ID, newId);
            // Insert new class
            db.insert(DbHelper.TABLE_CLASS, null, values);
            finish();
        }
    }

    private void delete(String classId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.TABLE_CLASS, DbHelper.COLUMN_CLASS_ID + "=?", new String[]{String.valueOf(classId)});
        dbHelper.deleteClassFirebase(classId);
    }
}