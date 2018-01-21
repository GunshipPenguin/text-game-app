package com.gunshippenguin.textgame.events;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import com.gunshippenguin.textgame.CapturePoint;
import com.gunshippenguin.textgame.EnemySpawn;

public class GameStartingEvent extends Event {
    Date mTimeStamp;
    List<String> mPlayerNumbers;
    List<CapturePoint> mCapturePoints;
    List<EnemySpawn> mEnemySpawns;

    public GameStartingEvent(String phoneNumber, Date timeStamp, List<String> playerNumbers,
                             List<CapturePoint> capturePoints, List<EnemySpawn> enemySpawns) {
        super(phoneNumber);
        mEnemySpawns = enemySpawns;
        mTimeStamp = timeStamp;
        mPlayerNumbers = playerNumbers;
        mCapturePoints = capturePoints;
    }

    @Override
    public void handleEvent(Activity activity) {

    }
}
