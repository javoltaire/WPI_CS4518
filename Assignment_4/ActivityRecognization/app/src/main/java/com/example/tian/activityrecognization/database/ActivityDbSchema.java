package com.example.tian.activityrecognization.database;

/**
 * @author Jules Voltaire on 12/8/2016.
 */

public class ActivityDbSchema {

    public static final class ActivityTable{
        public static final String NAME = "activities";

        public static final class Cols{
            public static final String ID = "id";
            public static final String ACTIVITY = "activity";
            public static final String CONFIDENCE = "confidence";
            public static final String TIMESTAMP = "timestamp";
        }
    }
}
