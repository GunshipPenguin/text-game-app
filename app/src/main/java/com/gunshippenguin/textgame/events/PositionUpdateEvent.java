package com.gunshippenguin.textgame.events;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class PositionUpdateEvent extends Event {
    private LatLng mLatLng;
    private Date mTime;

    public PositionUpdateEvent(String phoneNumber, Date timestamp, double latitude, double longitude) {
        super(phoneNumber);
        mLatLng = new LatLng(latitude, longitude);
        mTime = timestamp;
    }

    @Override
    public void handleEvent(Activity activity) {

    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "position_update");
        json.put("timestamp", mTime.getTime());
        json.put("latitude", mLatLng.latitude);
        json.put("longitude", mLatLng.longitude);

        return json;
    }
}
