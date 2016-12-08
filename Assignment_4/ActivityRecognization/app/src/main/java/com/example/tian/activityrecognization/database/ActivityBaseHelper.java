package com.example.tian.activityrecognization.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tian.activityrecognization.ActivityRecognizedService;

/**
 * @author Jules Voltaire on 12/8/2016.
 */

public class ActivityBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "ActivityDatabase.db";
    private static final String COMMA = ",";

    //region Types
    private static final String TEXT = " TEXT";
    private static final String INTEGER = " INTEGER";
    private static final String DATETIME = " DATETIME";
    //endregion

    //region Constraints
    private static final String NOT_NULL = " NOT NULL";
    private static final String DEFAULT = " DEFAULT";
    private static final String CURRENT_TIMESTAMP = " CURRENT_TIMESTAMP";
    //endregion

    public ActivityBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + ActivityDbSchema.ActivityTable.NAME + "(" +
                ActivityDbSchema.ActivityTable.Cols.ID + INTEGER + " PRIMARY KEY AUTOINCREMENT" + COMMA +
                ActivityDbSchema.ActivityTable.Cols.ACTIVITY + TEXT + COMMA +
                ActivityDbSchema.ActivityTable.Cols.CONFIDENCE + INTEGER + COMMA +
                ActivityDbSchema.ActivityTable.Cols.TIMESTAMP + DATETIME + DEFAULT + CURRENT_TIMESTAMP + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }



//
//    ActivityDbSchema.ActivityTable.Cols.TIMESTAMP + TIMESTAMP + DEFAULT + ")"
}
