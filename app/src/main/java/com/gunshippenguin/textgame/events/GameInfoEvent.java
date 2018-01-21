package com.gunshippenguin.textgame.events;

import android.app.Activity;
import android.content.Context;

import com.gunshippenguin.textgame.R;
import com.gunshippenguin.textgame.TextGameMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class GameInfoEvent extends Event implements DisplayableInterface{
    private String message;

    public GameInfoEvent(String phoneNumber, String message) {
        super(phoneNumber);
        this.message = message;
    }

    @Override
    public void handleEvent(Activity activity) { }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getSenderNumber() {
        return getPhoneNumber();
    }

    @Override
    public int getColor(Context c) {
        return c.getResources().getColor(R.color.blue);
    }

    @Override
    public boolean isChatMessage() {
        return false;
    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "game_info_start");
        return json;
    }
}
