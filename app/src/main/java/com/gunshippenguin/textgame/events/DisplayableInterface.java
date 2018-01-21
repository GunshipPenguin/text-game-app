package com.gunshippenguin.textgame.events;

import android.content.Context;

public interface DisplayableInterface {
    public String getMessage();
    public String getSenderNumber();
    public int getColor(Context context);
    public boolean isChatMessage();
}