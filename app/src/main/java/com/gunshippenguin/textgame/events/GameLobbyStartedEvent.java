package com.gunshippenguin.textgame.events;

import android.app.Activity;

public class GameLobbyStartedEvent extends Event {

    public GameLobbyStartedEvent(String phoneNumber) {
        super(phoneNumber);
    }

    @Override
    public void handleEvent(Activity activity) {

    }
}
