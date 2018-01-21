package com.gunshippenguin.textgame;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an enemy spawn time
 */
public class TreasureSpawn implements Serializable {
    Date mTime;
    double mLatitude;
    double mLongitude;
    boolean mTaken;

    public TreasureSpawn(Date time, double latitude, double longitude) {
        mTime = time;
        mLatitude = latitude;
        mLongitude = longitude;
        mTaken = false;
    }

    public Date getTime() {
        return mTime;
    }

    public double getLat() { return mLatitude; }

    public double getLong() { return mLongitude; }

    public boolean isTaken() { return mTaken; }

    public void setTaken() { mTaken = true; }
}
