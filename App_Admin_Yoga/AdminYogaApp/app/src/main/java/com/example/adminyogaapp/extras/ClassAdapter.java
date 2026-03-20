package com.example.adminyogaapp.extras;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.adminyogaapp.R;
import com.example.adminyogaapp.models.YogaClass;

import java.util.ArrayList;

public class ClassAdapter extends BaseAdapter implements Filterable {

    private Activity activity;
    private ArrayList<YogaClass> listClass;
    private ArrayList<YogaClass> listClassOld;

    public ClassAdapter(Activity activity, ArrayList<YogaClass> listClass) {
        this.activity = activity;
        this.listClass = listClass;
        this.listClassOld = listClass;
    }

    @Override
    public int getCount() {
        return listClass.size();
    }

    @Override
    public Object getItem(int position) {
        return listClass.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        convertView = inflater.inflate(R.layout.item_view, null);

        YogaClass yogaClass = (YogaClass) getItem(position);
        ((TextView) convertView.findViewById(R.id.name)).setText(yogaClass.getName());
        ((TextView) convertView.findViewById(R.id.description)).setText(yogaClass.getTeacher());
        ((TextView) convertView.findViewById(R.id.date)).setText(String.format("Day of week: %s", yogaClass.getDate()));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    listClass = listClassOld;
                } else {
                    ArrayList<YogaClass> classList = new ArrayList<>();

                    for (YogaClass yogaClass : listClassOld) {
                        if (yogaClass.getTeacher().toLowerCase().contains(strSearch.toLowerCase()) || yogaClass.getDate().contains(strSearch)) {
                            classList.add(yogaClass);
                        }
                    }

                    listClass = classList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listClass;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listClass = (ArrayList<YogaClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
