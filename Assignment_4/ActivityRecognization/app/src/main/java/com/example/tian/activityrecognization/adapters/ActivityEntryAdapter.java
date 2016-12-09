package com.example.tian.activityrecognization.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tian.activityrecognization.R;
import com.example.tian.activityrecognization.models.ActivityEntry;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Jules Voltaire on 12/8/2016.
 */

public class ActivityEntryAdapter extends ArrayAdapter<ActivityEntry> {

    public ActivityEntryAdapter(Context context, ArrayList<ActivityEntry> activities){
        super(context, 0, activities);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // replaces the view of each item in the listview
        ActivityEntry activity = getItem(position);

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_activity, parent, false);

        TextView actNameTextView = (TextView) convertView.findViewById(R.id.text_view_activity_name);
        TextView actTimeTextView = (TextView) convertView.findViewById(R.id.text_view_activity_time);

        actNameTextView.setText(activity.getActivity());
        Date d = new Date(TimeUnit.SECONDS.toMillis(activity.getTimeStamp()));
        actTimeTextView.setText(d.toString());
        return convertView;
//        return super.getView(position, convertView, parent);
    }
}
