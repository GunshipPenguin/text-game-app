package com.gunshippenguin.textgame.events;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by rhys on 20/01/18.
 */
public class NewRegistrationEvent extends Event {
    private List<String> mPlayersInLobby;

    public NewRegistrationEvent(String phoneNumber, List<String> playersInLobby) {
        super(phoneNumber);
        mPlayersInLobby = playersInLobby;
    }

    public List<String> getPlayersInLobby() {
        return mPlayersInLobby;
    }

    @Override
    public void handleEvent(Activity activity) {

    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "new_registration");
        json.put("players_in_lobby", JsonUtils.stringListToJsonArray(mPlayersInLobby));
        return json;
    }
}
