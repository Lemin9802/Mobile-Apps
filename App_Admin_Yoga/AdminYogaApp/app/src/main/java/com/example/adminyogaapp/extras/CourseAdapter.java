package com.example.adminyogaapp.extras;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.adminyogaapp.R;
import com.example.adminyogaapp.models.Course;

import java.util.ArrayList;

public class CourseAdapter extends BaseAdapter {

    private Activity activity;
    final ArrayList<Course> listCourse;

    public CourseAdapter(Activity activity, ArrayList<Course> listCourse) {
        this.activity = activity;
        this.listCourse = listCourse;
    }

    @Override
    public int getCount() {
        return listCourse.size();
    }

    @Override
    public Object getItem(int position) {
        return listCourse.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = activity.getLayoutInflater();

        convertView = inflater.inflate(R.layout.item_view, null);

        Course course = (Course) getItem(position);

        ((TextView) convertView.findViewById(R.id.name)).setText(course.getName());
        ((TextView) convertView.findViewById(R.id.description)).setText(course.getTimeStart());
        ((TextView) convertView.findViewById(R.id.date)).setText(String.format("Day of week: %s", course.getDayOfWeek()));

        return convertView;
    }
}
