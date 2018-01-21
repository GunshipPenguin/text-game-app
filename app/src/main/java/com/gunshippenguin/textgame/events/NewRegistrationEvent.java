package com.gunshippenguin.textgame.events;

import android.app.Activity;

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
}
