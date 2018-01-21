package com.gunshippenguin.textgame.events;


import com.gunshippenguin.textgame.TreasureSpawn;

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


    public static List<TreasureSpawn> jsonTreasureSpawnArrayToList(JSONArray array) throws JSONException {
        List<TreasureSpawn> list = new ArrayList<>();

        for(int i=0;i<array.length();i++) {
            JSONObject pointJson = array.getJSONObject(i);
            list.add(new TreasureSpawn(new Date(pointJson.getLong("timestamp")),
                    pointJson.getDouble("latitude"),pointJson.getDouble("longitude")));
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


    public static JSONArray treasureSpawnListToJsonArray(List<TreasureSpawn> list) throws JSONException {
        JSONArray array = new JSONArray();

        for (TreasureSpawn treasureSpawn : list) {
            JSONObject esJson = new JSONObject();
            esJson.put("timestamp", treasureSpawn.getTime().getTime());
            esJson.put("latitude", treasureSpawn.getLat());
            esJson.put("longitude",treasureSpawn.getLong());
            array.put(esJson);
        }

        return array;
    }
}
