package com.gunshippenguin.textgame.events;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by rhys on 21/01/18.
 */

public class StartCaptureEvent extends Event {
    Date mTime;
    int mCapturePoint;

    public StartCaptureEvent(String phoneNumber, int capturePoint, Date timestamp) {
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
        json.put("event_type", "start_capture");
        json.put("timestamp", mTime.getTime());
        json.put("capture_point", mCapturePoint);

        return json;
    }
}
