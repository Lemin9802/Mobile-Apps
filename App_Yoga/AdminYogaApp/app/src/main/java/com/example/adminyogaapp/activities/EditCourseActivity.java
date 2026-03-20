package com.example.adminyogaapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adminyogaapp.extras.DbHelper;
import com.example.adminyogaapp.MainActivity;
import com.example.adminyogaapp.R;
import com.example.adminyogaapp.models.Course;

public class EditCourseActivity extends AppCompatActivity {

    private EditText editName, editTimeStart, editCapacity, editDuration, editPrice, editDescription;
    private Spinner spinnerDayOfWeek, spinnerClassType;
    private DbHelper dbHelper;
    private String courseId;
    Button buttonSave, buttonDelete;

    private TextView errorName;
    private TextView errorDayOfTheWeek;
    private TextView errorTimeStart;
    private TextView errorCapacity;
    private TextView errorDuration;
    private TextView errorPrice;
    private TextView errorClassType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);


        editName = findViewById(R.id.edit_name);
        spinnerDayOfWeek = findViewById(R.id.spinner_day_of_the_week);
        editTimeStart = findViewById(R.id.edit_time_start);
        editCapacity = findViewById(R.id.edittext_capacity);
        editDuration = findViewById(R.id.edittext_duration);
        editPrice = findViewById(R.id.edittext_price);
        spinnerClassType = findViewById(R.id.spinner_class_type);
        editDescription = findViewById(R.id.edittext_description);
        buttonSave = findViewById(R.id.button_save);


        errorName = findViewById(R.id.error_name);
        errorDayOfTheWeek = findViewById(R.id.error_day_of_the_week);
        errorTimeStart = findViewById(R.id.error_time_start);
        errorCapacity = findViewById(R.id.error_capacity);
        errorDuration = findViewById(R.id.error_duration);
        errorPrice = findViewById(R.id.error_price);
        errorClassType = findViewById(R.id.error_class_type);


        // Populate spinners
        ArrayAdapter<CharSequence> dayOfWeekAdapter = ArrayAdapter.createFromResource(this,
                R.array.days_of_week_array, android.R.layout.simple_spinner_item);
        dayOfWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeek.setAdapter(dayOfWeekAdapter);

        ArrayAdapter<CharSequence> classTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.class_type_array, android.R.layout.simple_spinner_item);
        classTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassType.setAdapter(classTypeAdapter);

        dbHelper = new DbHelper(this);

        // Get the data passed from the previous activity
        courseId = getIntent().getStringExtra("COURSE_ID");

        if (courseId != null) {
            populateFields(courseId);
            buttonDelete = findViewById(R.id.button_delete);
            buttonDelete.setVisibility(View.VISIBLE);

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete(courseId);
                    Intent intent = new Intent(EditCourseActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCourse();
                Intent intent = new Intent(EditCourseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    private void populateFields(String courseId) {
        // Simulated course loading; replace with actual data retrieval
        Course updateCourse = dbHelper.getCourseById(courseId);
        // Todo FETCH DETAIL COURSE AND UPDATE

        editName.setText(updateCourse.getName());
        spinnerDayOfWeek.setSelection(((ArrayAdapter) spinnerDayOfWeek.getAdapter()).getPosition(updateCourse.getDayOfWeek()));
        editTimeStart.setText(updateCourse.getTimeStart());
        editCapacity.setText(String.valueOf(updateCourse.getCapacity()));
        editDuration.setText(String.valueOf(updateCourse.getDuration()));
        editPrice.setText(String.valueOf(updateCourse.getPrice()));
        spinnerClassType.setSelection(((ArrayAdapter) spinnerClassType.getAdapter()).getPosition(updateCourse.getClassType()));
        editDescription.setText(updateCourse.getDescription());
    }


    private void saveCourse() {
        String name = editName.getText().toString();
        String dayOfWeek = spinnerDayOfWeek.getSelectedItem().toString();
        String timeStart = editTimeStart.getText().toString();
        int capacity = Integer.parseInt(editCapacity.getText().toString());
        float duration = Float.parseFloat(editDuration.getText().toString());
        float price = Float.parseFloat(editPrice.getText().toString());
        String classType = spinnerClassType.getSelectedItem().toString();
        String description = editDescription.getText().toString();

        if (validate()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DbHelper.COLUMN_COURSE_ID, courseId);
            values.put(DbHelper.COLUMN_COURSE_NAME, name);
            values.put(DbHelper.COLUMN_COURSE_DAY_OF_WEEK, dayOfWeek);
            values.put(DbHelper.COLUMN_COURSE_TIME_START, timeStart);
            values.put(DbHelper.COLUMN_COURSE_CAPACITY, capacity);
            values.put(DbHelper.COLUMN_COURSE_DURATION, duration);
            values.put(DbHelper.COLUMN_COURSE_PRICE, price);
            values.put(DbHelper.COLUMN_COURSE_CLASS_TYPE, classType);
            values.put(DbHelper.COLUMN_COURSE_DESCRIPTION, description);

            Course newCourse = new Course(name, dayOfWeek, timeStart, capacity, duration, price, classType, description);

            if (courseId != null) {
                // Update existing course
                newCourse.setId(courseId);
                db.update(DbHelper.TABLE_COURSE, values,
                        DbHelper.COLUMN_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});

                dbHelper.saveCourseFirebase(courseId, newCourse);
                finish();
            } else {
                String newId = dbHelper.saveCourseFirebase(newCourse);
                values.put(DbHelper.COLUMN_COURSE_ID, newId);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(DbHelper.TABLE_COURSE, null, values);

                if (newRowId != -1) {
                    Toast.makeText(this, "Yoga course saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error saving yoga course", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }


    private boolean validate() {
        boolean isValid = true;

        // Validate inputs
        if (editName.getText().toString().equals("")) {
            errorName.setText("Please select a day of the week.");
            errorName.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorName.setVisibility(View.GONE);
        }

        if (spinnerDayOfWeek.getSelectedItem().toString().equals("")) {
            errorDayOfTheWeek.setText("Please select a day of the week.");
            errorDayOfTheWeek.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorDayOfTheWeek.setVisibility(View.GONE);
        }

        if (editTimeStart.getText().toString().isEmpty()) {
            errorTimeStart.setText("Please enter the start time.");
            errorTimeStart.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorTimeStart.setVisibility(View.GONE);
        }

        if (editCapacity.getText().toString().isEmpty()) {
            errorCapacity.setText("Please enter the capacity.");
            errorCapacity.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorCapacity.setVisibility(View.GONE);
        }

        if (editDuration.getText().toString().isEmpty()) {
            errorDuration.setText("Please enter the duration.");
            errorDuration.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorDuration.setVisibility(View.GONE);
        }

        if (editPrice.getText().toString().isEmpty()) {
            errorPrice.setText("Please enter the price.");
            errorPrice.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorPrice.setVisibility(View.GONE);
        }

        if (spinnerClassType.getSelectedItem().toString().equals("")) {
            errorClassType.setText("Please select a class type.");
            errorClassType.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorClassType.setVisibility(View.GONE);
        }
        return isValid;
    }

    private void delete(String courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.TABLE_COURSE, DbHelper.COLUMN_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
        dbHelper.deleteCourseFirebase(courseId);
    }
}