package com.gunshippenguin.textgame.events;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;
import com.gunshippenguin.textgame.R;
import com.gunshippenguin.textgame.TextGameLandingActivity;

public class GameLobbyStartedEvent extends Event {

    public GameLobbyStartedEvent(String phoneNumber) {
        super(phoneNumber);
    }

    @Override
    public void handleEvent(Activity activity) {
        TextGameLandingActivity uiHook = (TextGameLandingActivity) activity;
        uiHook.setLobbyLeader(true);
        uiHook.getStartButton(uiHook).setText(R.string.start_game);
        uiHook.getJoinButton(uiHook).setEnabled(false);
        uiHook.getStartButton(uiHook).setEnabled(true);
    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "game_lobby_started");
        return json;
    }
}
