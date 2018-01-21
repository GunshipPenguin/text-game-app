package com.gunshippenguin.textgame.events;

import android.app.Activity;
import android.content.Context;

import com.gunshippenguin.textgame.R;
import com.gunshippenguin.textgame.TextGameMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by rhys on 21/01/18.
 */

public class CaptureTreasureEvent extends Event implements DisplayableInterface {
    Date mTime;
    int mTreasureNumber;

    public CaptureTreasureEvent(String phoneNumber, Date timestamp, int treasureNumber) {
        super(phoneNumber);
        mTime = timestamp;
        mTreasureNumber = treasureNumber;
    }

    @Override
    public void handleEvent(Activity activity) {
        TextGameMainActivity uiHook = (TextGameMainActivity) activity;

        uiHook.incrementPlayerByNumber(this.getPhoneNumber());
        uiHook.removeTreasureAtIndex(mTreasureNumber);
        uiHook.setEnemyScore();
    }

    @Override
    public String getMessage() {
        return "Treasure worth " + mTreasureNumber + " was just claimed!";
    }

    @Override
    public String getSenderNumber() {
        return getPhoneNumber();
    }

    @Override
    public int getColor(Context c) {
        return c.getResources().getColor(R.color.green);
    }

    @Override
    public boolean isChatMessage() {
        return false;
    }


    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "capture_treasure");
        json.put("timestamp", mTime.getTime());
        json.put("treasure_number", mTreasureNumber);

        return json;
    }
}
