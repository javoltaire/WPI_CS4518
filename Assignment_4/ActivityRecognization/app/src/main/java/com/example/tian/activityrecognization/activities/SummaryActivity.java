package com.example.tian.activityrecognization.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tian.activityrecognization.R;
import com.example.tian.activityrecognization.adapters.ActivityEntryAdapter;
import com.example.tian.activityrecognization.database.ActivityBaseHelper;
import com.example.tian.activityrecognization.database.ActivityDbSchema;
import com.example.tian.activityrecognization.models.ActivityEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SummaryActivity extends AppCompatActivity {

    private TextView mTimeFrameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        mTimeFrameTextView = (TextView)findViewById(R.id.text_view_time_frame);
        ListView activitiesListView = (ListView)findViewById(R.id.list_view_activites);
        ActivityEntryAdapter adapter = new ActivityEntryAdapter(this, getActivities());
        activitiesListView.setAdapter(adapter);
    }

    //region Methods

    /**
     * Loops over all items in cursor to create activities
     * @return
     */
    private ArrayList<ActivityEntry> getActivities(){
        // Getting a reference to the database
        ActivityBaseHelper mDbHelper = new ActivityBaseHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        ArrayList<ActivityEntry> entries = new ArrayList<>();

        // Setting up the query
        String[] projection = {
                ActivityDbSchema.ActivityTable.Cols.ID,
                ActivityDbSchema.ActivityTable.Cols.ACTIVITY,
                ActivityDbSchema.ActivityTable.Cols.CONFIDENCE,
                ActivityDbSchema.ActivityTable.Cols.TIMESTAMP
        };

        long cutOff = ((new Date().getTime())/1000) - (24 * 60 * 60);
        String selection = ActivityDbSchema.ActivityTable.Cols.TIMESTAMP + " > ?";
        String[] selectionArgs = { Long.toString(cutOff) };

        String sortOrder =  ActivityDbSchema.ActivityTable.Cols.TIMESTAMP + " DESC";

        long from = 0;
        long to = 0;

        Cursor c = db.query(
                ActivityDbSchema.ActivityTable.NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        c.moveToFirst();
        // Keep getting the next one until there is no more
        while(c.moveToNext()){
            ActivityEntry ae = CreateActivityFromCursor(c);
            if(ae != null) {
                if(ae.getTimeStamp() < from)
                    from = ae.getTimeStamp();
                if(ae.getTimeStamp() > to)
                    to = ae.getTimeStamp();
                entries.add(ae);
            }
        }

        // convert the to and from dates to actual dates and display them
        if(entries.size() > 0){
            Date f = new Date(TimeUnit.SECONDS.toMillis(from));
            Date t = new Date(TimeUnit.SECONDS.toMillis(to));
            mTimeFrameTextView.setText(f.toString() + " - " + t.toString());
        }

        c.close();
        db.close();
        mDbHelper.close();

        return entries;
    }

    /**
     * uses the cursor object to create a single Activity entry
     * @param c
     * @return
     */
    private ActivityEntry CreateActivityFromCursor(Cursor c){
        try{
            // Getting all the indexes
            int idIndex = c.getColumnIndexOrThrow(ActivityDbSchema.ActivityTable.Cols.ID);
            int actIndex = c.getColumnIndexOrThrow(ActivityDbSchema.ActivityTable.Cols.ACTIVITY);
            int confIndex = c.getColumnIndexOrThrow(ActivityDbSchema.ActivityTable.Cols.CONFIDENCE);
            int timeStampIdex = c.getColumnIndexOrThrow(ActivityDbSchema.ActivityTable.Cols.TIMESTAMP);
            // Getting the actual data
            long id = c.getLong(idIndex);
            String activity = c.getString(actIndex);
            long confidence = c.getLong(confIndex);
            long timeStamp = c.getLong(timeStampIdex);
            // Converting to model object
            return new ActivityEntry(id, activity, confidence, timeStamp);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
