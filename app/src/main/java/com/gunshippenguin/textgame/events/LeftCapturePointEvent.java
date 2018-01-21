package com.gunshippenguin.textgame.events;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class LeftCapturePointEvent extends Event {
    private Date mTime;
    private int mCapturePoint;

    public LeftCapturePointEvent(String phoneNumber, Date timestamp, int capturePoint) {
        super(phoneNumber);
        mTime = timestamp;
        mCapturePoint = capturePoint;
    }

    @Override
    public void handleEvent(Activity activity) {

    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "left_capture_point");
        json.put("timestamp", mTime.getTime());
        json.put("capture_point", mCapturePoint);

        return json;
    }
}
