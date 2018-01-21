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
    public static List<String> jsonStringArrayToList(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<>();

        for(int i=0;i<array.length();i++){
            list.add(array.getString(i));
        }
        return list;
    }

    public static List<CapturePoint> jsonCapturePointsArrayToList(JSONArray array) throws JSONException {
        List<CapturePoint> list = new ArrayList<>();

        for(int i=0;i<array.length();i++) {
            JSONObject pointJson = array.getJSONObject(i);
            list.add(new CapturePoint(pointJson.getDouble("latitude"), pointJson.getDouble("longitude"),
                    pointJson.getInt("point")));
        }
        return list;
    }

    public static List<EnemySpawn> jsonEnemySpawnArrayToList(JSONArray array) throws JSONException {
        List<EnemySpawn> list = new ArrayList<>();

        for(int i=0;i<array.length();i++) {
            JSONObject pointJson = array.getJSONObject(i);
            list.add(new EnemySpawn(new Date(pointJson.getLong("timestamp")), pointJson.getInt("point")));
        }
        return list;
    }

    public static JSONArray stringListToJsonArray(List<String> list) throws JSONException {
        JSONArray array = new JSONArray();

        for (String string : list) {
            array.put(string);
        }

        return array;
    }

    public static JSONArray capturePointsListToJsonArray(List<CapturePoint> list) throws JSONException{
        JSONArray array = new JSONArray();

        for (CapturePoint capturePoint : list) {
            JSONObject cpJson = new JSONObject();
            cpJson.put("latitude", capturePoint.getLatLng().latitude);
            cpJson.put("longitude", capturePoint.getLatLng().longitude);
            cpJson.put("point" , capturePoint.getNumber());
            array.put(cpJson);
        }

        return array;
    }

    public static JSONArray enemySpawnListToJsonArray(List<EnemySpawn> list) throws JSONException {
        JSONArray array = new JSONArray();

        for (EnemySpawn enemySpawn : list) {
            JSONObject esJson = new JSONObject();
            esJson.put("point", enemySpawn.getPointNumber());
            esJson.put("timestamps", enemySpawn.getTime().getTime());
            array.put(esJson);
        }

        return array;
    }
}
