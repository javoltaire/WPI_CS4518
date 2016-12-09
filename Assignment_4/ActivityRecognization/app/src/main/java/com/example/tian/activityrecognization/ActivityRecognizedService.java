package com.example.tian.activityrecognization;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.tian.activityrecognization.database.ActivityBaseHelper;
import com.example.tian.activityrecognization.database.ActivityDbSchema;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Date;
import java.util.List;

/**
 * Created by tian on 11/26/16.
 */

public class ActivityRecognizedService extends IntentService {

    public static final String TAG = ActivityRecognizedService.class.getSimpleName();
    //region Database instances
    private ActivityBaseHelper mDbHelper;
    private SQLiteDatabase db;
    //endregion

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        mDbHelper = new ActivityBaseHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();
        for( DetectedActivity activity : probableActivities ) {
            ContentValues values = new ContentValues();
            String act = null;              // Will store the activity name
            int conf = 0;                   // Will store the confidence value
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e(TAG, "In Vehicle: " + activity.getConfidence() );
                    act = "In Vehicle";
                    conf = activity.getConfidence();
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e(TAG, "On Bicycle: " + activity.getConfidence() );
                    act = "On Bicycle";
                    conf = activity.getConfidence();
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e(TAG, "On Foot: " + activity.getConfidence() );
                    act = "On Foot";
                    conf = activity.getConfidence();
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e(TAG, "Running: " + activity.getConfidence() );
                    act = "Running";
                    conf = activity.getConfidence();
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e(TAG, "Still: " + activity.getConfidence() );
                    act = "Still";
                    conf = activity.getConfidence();
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e(TAG, "Tilting: " + activity.getConfidence() );
                    act = "Tilting";
                    conf = activity.getConfidence();
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e(TAG, "Walking: " + activity.getConfidence() );
                    act = "Walking";
                    conf = activity.getConfidence();
                    if( activity.getConfidence() >= 75 ) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        builder.setContentText( "Are you walking?" );
                        builder.setSmallIcon( R.mipmap.ic_launcher );
                        builder.setContentTitle( getString( R.string.app_name ) );
                        NotificationManagerCompat.from(this).notify(0, builder.build());
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e(TAG, "Unknown: " + activity.getConfidence() );
                    act = "Unknown";
                    conf = activity.getConfidence();
                    break;
                }
            }
            if(act != null && conf >=75){
                // Add to the content value and insert in database only if value is above 75
                values.put(ActivityDbSchema.ActivityTable.Cols.ACTIVITY, act);
                values.put(ActivityDbSchema.ActivityTable.Cols.CONFIDENCE, conf);
                long timeStamp = (new Date().getTime())/1000;
                values.put(ActivityDbSchema.ActivityTable.Cols.TIMESTAMP, timeStamp);
                db.insert(ActivityDbSchema.ActivityTable.NAME, null, values);
            }
        }
    }
}
