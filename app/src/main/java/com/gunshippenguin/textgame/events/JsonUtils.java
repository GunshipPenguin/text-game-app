package com.gunshippenguin.textgame.events;

import com.gunshippenguin.textgame.CapturePoint;

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

    public static JSONArray stringListToJsonArray(List<String> list) throws JSONException {
        JSONArray array = new JSONArray();

        for (String string : list) {
            array.put(string);
        }

        return array;
    }
}
