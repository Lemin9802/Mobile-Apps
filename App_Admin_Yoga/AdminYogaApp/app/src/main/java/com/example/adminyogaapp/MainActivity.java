package com.example.adminyogaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.adminyogaapp.activities.EditClassActivity;
import com.example.adminyogaapp.activities.EditCourseActivity;
import com.example.adminyogaapp.activities.ViewClassActivity;
import com.example.adminyogaapp.activities.ViewCourseActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView card_course = findViewById(R.id.view_courses_card);
        CardView card_class = findViewById(R.id.view_classes_card);
        CardView card_add_course = findViewById(R.id.add_course_card);
        CardView card_add_class = findViewById(R.id.add_class_card);


        card_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewCourseActivity.class);
                startActivity(intent);
            }
        });

        card_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewClassActivity.class);
                startActivity(intent);
            }
        });


        card_add_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditCourseActivity.class);
                startActivity(intent);
            }
        });

        card_add_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditClassActivity.class);
                startActivity(intent);
            }
        });


    }
}