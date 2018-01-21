package com.gunshippenguin.textgame.events;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by rhys on 21/01/18.
 */

public class CaptureTreasureEvent extends Event {
    Date mTime;
    int mTreasureNumber;

    public CaptureTreasureEvent(String phoneNumber, Date timestamp, int treasureNumber) {
        super(phoneNumber);
        mTime = timestamp;
        mTreasureNumber = treasureNumber;
    }

    @Override
    public void handleEvent(Activity activity) {

    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "capture_treasure");
        json.put("timestamp", mTime.getTime());
        json.put("treasure_number", mTreasureNumber);

        return json;
    }
}
