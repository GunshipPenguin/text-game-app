package com.gunshippenguin.textgame.events;

import android.app.Activity;

/**
 * Created by rhys on 20/01/18.
 */

public class StartGameEvent extends Event {
    public StartGameEvent(String phoneNumber) {
        super(phoneNumber);
    }

    @Override
    public void handleEvent(Activity activity) {

    }
}
