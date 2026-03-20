package com.example.adminyogaapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adminyogaapp.extras.DbHelper;
import com.example.adminyogaapp.R;
import com.example.adminyogaapp.extras.ClassAdapter;
import com.example.adminyogaapp.models.YogaClass;

import java.util.ArrayList;

public class ViewClassActivity extends AppCompatActivity {

    ArrayList<YogaClass> classes;
    SearchView searchView;
    ClassAdapter classViewAdapter;
    ListView listViewClass;
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class);
        dbHelper = new DbHelper(this);
        dbHelper.syncClassesWithFirebase();


        classes = getAllClasses();


        listViewClass = findViewById(R.id.list_class);
        searchView = findViewById(R.id.search_bar);
        classViewAdapter = new ClassAdapter(this, classes);
        listViewClass.setAdapter(classViewAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                classViewAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                classViewAdapter.getFilter().filter(text);
                return false;
            }
        });

        listViewClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                YogaClass yogaClass = (YogaClass) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(ViewClassActivity.this, EditClassActivity.class);
                intent.putExtra("CLASS_ID", yogaClass.getId());
                startActivity(intent);
            }
        });
    }

    protected void onRestart() {
        super.onRestart();
        classes = getAllClasses();
        classViewAdapter.notifyDataSetChanged();
    }


    public ArrayList<YogaClass> getAllClasses() {
        ArrayList<YogaClass> classList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.TABLE_CLASS, null);

        if (cursor.moveToFirst()) {
            do {
                YogaClass yogaClass = new YogaClass();
                yogaClass.setId(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_CLASS_ID)));
                yogaClass.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_CLASS_NAME)));
                yogaClass.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_CLASS_DATE)));
                yogaClass.setCourseId(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_CLASS_COURSE_ID)));
                yogaClass.setTeacher(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_CLASS_TEACHER)));
                yogaClass.setComment(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_CLASS_COMMENT)));
                classList.add(yogaClass);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return classList;
    }
}