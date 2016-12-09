package com.example.tian.activityrecognization.models;

/**
 * @author Jules Voltaire on 12/8/2016.
 */

public class ActivityEntry {
    //region private variables
    private long id;
    private String activity;
    private long confidence;
    private long timeStamp;
    //endregion

    //region Getters
    public long getId() {
        return id;
    }

    public String getActivity() {
        return activity;
    }

    public long getConfidence() {
        return confidence;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
    //endregion


    //region Constructor
    public ActivityEntry(long id, String activity, long confidence, long timeStamp){
        this.id = id;
        this.activity = activity;
        this.confidence = confidence;
        this.timeStamp = timeStamp;
    }
    //endregion
}
