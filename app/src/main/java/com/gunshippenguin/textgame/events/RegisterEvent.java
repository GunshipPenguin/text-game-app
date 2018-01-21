package com.gunshippenguin.textgame.events;

import android.app.Activity;

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
}
