package com.gunshippenguin.textgame.events;

import android.app.Activity;
import android.content.Context;

import com.gunshippenguin.textgame.R;
import com.gunshippenguin.textgame.TextGameMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatMessageEvent extends Event implements DisplayableInterface{
    private String message;

    public ChatMessageEvent(String phoneNumber, String message) {
        super(phoneNumber);
        this.message = message;
    }

    @Override
    public void handleEvent(Activity activity) {
        TextGameMainActivity uiHook = (TextGameMainActivity) activity;
        sendToNumbers(uiHook.getPlayerNumbers(), activity.getApplicationContext());
        uiHook.eventData.add(0, (DisplayableInterface)this);
        uiHook.mAdapter.notifyItemInserted(0);
    }

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
        return c.getResources().getColor(R.color.lightGrey);
    }

    @Override
    public boolean isChatMessage() {
        return true;
    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "chat_message");
        json.put("message", message);
        return json;
    }
}
