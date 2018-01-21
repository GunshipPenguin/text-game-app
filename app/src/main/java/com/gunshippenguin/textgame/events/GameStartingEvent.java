package com.gunshippenguin.textgame.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gunshippenguin.textgame.CapturePoint;
import com.gunshippenguin.textgame.EnemySpawn;
import com.gunshippenguin.textgame.TextGameMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class GameStartingEvent extends Event {

    private static final String TIMESTAMP_KEY = "textgame.events.timestampkey";
    private static final String PLAYER_NUMBERS_KEY = "textgame.events.playernumberskey";
    private static final String CAPTURE_POINTS_KEY = "textgame.events.capturepointskey";
    private static final String ENEMY_SPAWNS_KEY = "textgame.events.enemyspawnskey";
    private static final String BUNDLE_KEY = "textgame.gamestart.bundle";

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
        Intent intent = new Intent(activity, TextGameMainActivity.class);
        Bundle gameDataBundle = new Bundle();

        gameDataBundle.putSerializable(TIMESTAMP_KEY,mTimeStamp);
        gameDataBundle.putSerializable(PLAYER_NUMBERS_KEY,(ArrayList<String>)mPlayerNumbers);
        gameDataBundle.putSerializable(CAPTURE_POINTS_KEY,(ArrayList<CapturePoint>)mCapturePoints);
        gameDataBundle.putSerializable(ENEMY_SPAWNS_KEY,(ArrayList<EnemySpawn>)mEnemySpawns);

        intent.putExtra(BUNDLE_KEY,gameDataBundle);

        activity.startActivity(intent);
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
