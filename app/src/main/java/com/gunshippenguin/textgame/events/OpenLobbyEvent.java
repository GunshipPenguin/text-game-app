package com.gunshippenguin.textgame.events;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rhys on 20/01/18.
 */

public class OpenLobbyEvent extends Event {

    public OpenLobbyEvent(String phoneNumber) {
        super(phoneNumber);
    }

    @Override
    public void handleEvent(Activity activity) {
        //TODO: send
    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "open_lobby");
        return json;
    }
}
