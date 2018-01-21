package com.gunshippenguin.textgame.events;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;
import com.gunshippenguin.textgame.TextGameLandingActivity;

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
        TextGameLandingActivity uiHook = (TextGameLandingActivity) activity;
        uiHook.cleanStatefulTransitions(mPlayersInLobby.size());

        for (int i = 0; i < mPlayersInLobby.size(); i++) {
            uiHook.addPlayer(mPlayersInLobby.get(i));
        }

        if (!uiHook.lobbyLeader()) {
            uiHook.getStartButton(uiHook).setEnabled(false);
            uiHook.getJoinButton(uiHook).setEnabled(false);
        }
    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "new_registration");
        json.put("players_in_lobby", JsonUtils.stringListToJsonArray(mPlayersInLobby));
        return json;
    }
}
