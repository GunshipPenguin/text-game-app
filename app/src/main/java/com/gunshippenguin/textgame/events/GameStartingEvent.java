package com.gunshippenguin.textgame.events;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import com.gunshippenguin.textgame.CapturePoint;
import com.gunshippenguin.textgame.EnemySpawn;

import org.json.JSONException;
import org.json.JSONObject;

public class GameStartingEvent extends Event {
    Date mTimeStamp;
    Date mGameEnd;
    List<String> mPlayerNumbers;
    List<CapturePoint> mCapturePoints;
    List<EnemySpawn> mEnemySpawns;


    public GameStartingEvent(String phoneNumber, Date timeStamp, List<String> playerNumbers,
                             List<CapturePoint> capturePoints, List<EnemySpawn> enemySpawns,
                             Date gameEnd) {
        super(phoneNumber);
        mEnemySpawns = enemySpawns;
        mTimeStamp = timeStamp;
        mGameEnd = gameEnd;
        mPlayerNumbers = playerNumbers;
        mCapturePoints = capturePoints;
    }

    @Override
    public void handleEvent(Activity activity) {

    }
    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_type", "game_starting");
        json.put("timestamp", mTimeStamp.getTime());
        json.put("capture_points", JsonUtils.capturePointsListToJsonArray(mCapturePoints));
        json.put("enemy_spawns", JsonUtils.enemySpawnListToJsonArray(mEnemySpawns));
        json.put("game_end", mGameEnd.getTime());

        return json;
    }
}
