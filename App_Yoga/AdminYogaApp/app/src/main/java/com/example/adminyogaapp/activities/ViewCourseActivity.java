package com.example.adminyogaapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adminyogaapp.extras.DbHelper;
import com.example.adminyogaapp.extras.CourseAdapter;

import com.example.adminyogaapp.R;
import com.example.adminyogaapp.models.Course;

import java.util.ArrayList;

public class ViewCourseActivity extends AppCompatActivity {

    DbHelper dbHelper;
    ArrayList<Course> courses;
    CourseAdapter courseViewAdapter;
    ListView listViewCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);

        dbHelper = new DbHelper(this);
        dbHelper.syncCoursesWithFirebase();

        courses = getCourses();

        listViewCourse = findViewById(R.id.list_courses);
        courseViewAdapter = new CourseAdapter(this, courses);
        listViewCourse.setAdapter(courseViewAdapter);

        listViewCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Course course = (Course) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(ViewCourseActivity.this, EditCourseActivity.class);
                intent.putExtra("COURSE_ID", course.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        courses = getCourses();
        courseViewAdapter.notifyDataSetChanged();
    }

    public ArrayList<Course> getCourses() {
        ArrayList<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.TABLE_COURSE, null);

        if (cursor.moveToFirst()) {
            do {
                Course course = new Course();
                course.setId(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_COURSE_ID)));
                course.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_COURSE_NAME)));
                course.setDayOfWeek(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_COURSE_DAY_OF_WEEK)));
                course.setTimeStart(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_COURSE_TIME_START)));
                course.setCapacity(cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_COURSE_CAPACITY)));
                course.setDuration(cursor.getFloat(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_COURSE_DURATION)));
                course.setPrice(cursor.getFloat(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_COURSE_PRICE)));
                course.setClassType(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_COURSE_CLASS_TYPE)));
                course.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_COURSE_DESCRIPTION)));
                courseList.add(course);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return courseList;
    }
}