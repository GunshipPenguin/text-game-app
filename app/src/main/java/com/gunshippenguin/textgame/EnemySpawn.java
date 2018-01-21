package com.gunshippenguin.textgame;

import java.util.Date;

/**
 * Represents an enemy spawn time
 */
public class EnemySpawn {
    int mPointNumber;
    Date mTime;

    public EnemySpawn(Date time, int pointNumber) {
        mTime = time;
        mPointNumber = pointNumber;
    }

    public Date getTime() {
        return mTime;
    }

    public int getPointNumber() {
        return mPointNumber;
    }
}
