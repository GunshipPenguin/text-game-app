package com.gunshippenguin.textgame.events;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

public class GameLobbyStartedEvent extends Event {

    public GameLobbyStartedEvent(String phoneNumber) {
        super(phoneNumber);
    }

    @Override
    public void handleEvent(Activity activity) {

    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "game_lobby_started");
        return json;
    }
}
