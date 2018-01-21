package com.gunshippenguin.textgame.events;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rhys on 21/01/18.
 */

public class ChatMessageEvent extends Event {
    private String mMessage;

    public ChatMessageEvent(String phoneNumber, String message) {
        super(phoneNumber);
        mMessage = message;
    }

    @Override
    public void handleEvent(Activity activity) {

    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("message", mMessage);

        return json;
    }
}
