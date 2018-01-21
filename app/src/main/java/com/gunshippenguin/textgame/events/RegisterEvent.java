package com.gunshippenguin.textgame.events;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rhys on 20/01/18.
 */

public class RegisterEvent extends Event {
    private String mHostNumber;

    public RegisterEvent(String phoneNumber, String hostNumber) {
        super(phoneNumber);
        mHostNumber = hostNumber;
    }

    @Override
    public void handleEvent(Activity activity) {

    }

    private String getHostNumber() {
        return mHostNumber;
    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "register");
        json.put("host_number", getHostNumber().toString());
        return json;
    }
}
