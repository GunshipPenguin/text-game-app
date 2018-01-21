package com.gunshippenguin.textgame;

import com.google.android.gms.maps.model.LatLng;

/**
 * Represents a capture point
 */
public class CapturePoint {
    double mLatitude;
    double mLongitude;
    int mNumber;

    public CapturePoint(double latitude, double longitude, int number) {
        mLatitude = latitude;
        mLongitude = longitude;
        mNumber = number;
    }

    public LatLng getLatLng() {
        return new LatLng(mLatitude, mLongitude);
    }

    public int getNumber() {
        return mNumber;
    }
}
