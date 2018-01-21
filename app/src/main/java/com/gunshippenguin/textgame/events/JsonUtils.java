package com.gunshippenguin.textgame.events;

import com.gunshippenguin.textgame.CapturePoint;
import com.gunshippenguin.textgame.EnemySpawn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class containing JSON functions
 */
public class JsonUtils {
    private JsonUtils() {}

    /**
     * Given a JSONArray of strings, return it's analogous List<String>
     *
     * @param array Array to parse
     * @return A List of strings
     * @throws JSONException If there was an error parsing the JSONArray
     */
    public static List<String> parseStringArray(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<>();

        for(int i=0;i<array.length();i++){
            list.add(array.getString(i));
        }
        return list;
    }

    public static List<CapturePoint> parseCapturePointArray(JSONArray array) throws JSONException {
        List<CapturePoint> list = new ArrayList<>();

        for(int i=0;i<array.length();i++) {
            JSONObject pointJson = array.getJSONObject(i);
            list.add(new CapturePoint(pointJson.getDouble("latitude"), pointJson.getDouble("longitude"),
                    pointJson.getInt("point")));
        }
        return list;
    }

    public static List<EnemySpawn> parseEnemySpawnArray(JSONArray array) throws JSONException {
        List<EnemySpawn> list = new ArrayList<>();

        for(int i=0;i<array.length();i++) {
            JSONObject pointJson = array.getJSONObject(i);
            list.add(new EnemySpawn(new Date(pointJson.getLong("timestamp")), pointJson.getInt("point")));
        }
        return list;
    }
}
