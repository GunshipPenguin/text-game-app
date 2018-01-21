package com.gunshippenguin.textgame.events;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by rhys on 21/01/18.
 */

public class DefeatEnemyEvent extends Event {
    Date mTime;
    int mCapturePoint;

    public DefeatEnemyEvent(String phoneNumber, Date timestamp, int capturePoint) {
        super(phoneNumber);
        mTime = timestamp;
        mCapturePoint = capturePoint;
    }

    @Override
    public void handleEvent(Activity activity) {

    }

    @Override
    public JSONObject getJson() throws JSONException {
        return null;
    }
}
