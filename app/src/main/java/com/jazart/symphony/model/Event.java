package com.jazart.symphony.model;

import android.location.Location;

import java.util.Date;

/**
 * Created by kendrickgholston on 4/15/18.
 */

class Event {
    private Date mDate;
    private String mHost;
    private Location mLocation; //Temporary data type until we implement Google Maps API



    public Event(Date date, String host, Location location){
        mLocation = location;
        mDate = date;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public Location getmLocation() {
        return mLocation;
    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
    }

    public String getmHost() {

        return mHost;
    }

    public void setmHost(String mHost) {
        this.mHost = mHost;
    }
}
