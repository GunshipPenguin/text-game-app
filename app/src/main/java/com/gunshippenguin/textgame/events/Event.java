package com.gunshippenguin.textgame.events;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.gunshippenguin.textgame.CapturePoint;
import com.gunshippenguin.textgame.EnemySpawn;
import com.gunshippenguin.textgame.SmsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Abstract base class for all events
 */
public abstract class Event {
    private String mPhoneNumber;

    public Event(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public final String getPhoneNumber() {
        return mPhoneNumber;
    }

    public static Event fromCompressedAndEncoded(String phoneNumber, String compressed) throws DataFormatException, JSONException, InvalidEventException {
        Inflater inflater = new Inflater();
        byte[] messageText = Base64.decode(compressed, Base64.DEFAULT);
        inflater.setInput(messageText,0,messageText.length);
        byte[] result = new byte[1024];

        StringBuilder decompressed = new StringBuilder();
        while (!inflater.finished()) {
            inflater.inflate(result);
            decompressed.append(new String(result));
        }

        return fromJson(phoneNumber, new JSONObject(decompressed.toString()));
    }

    public static Event fromJson(String phoneNumber, JSONObject eventJson) throws JSONException, InvalidEventException {
        switch(eventJson.getString("event_type")) {
            // Bootstrapping sequence events
            case "open_lobby": {
                return new OpenLobbyEvent(phoneNumber);
            }
            case "game_lobby_started": {
                return new GameLobbyStartedEvent(phoneNumber);
            }
            case "register": {
                return new RegisterEvent(phoneNumber, eventJson.getString("host_number"));
            }
            case "new_registration": {
                return new NewRegistrationEvent(phoneNumber, JsonUtils.jsonStringArrayToList(
                        eventJson.getJSONArray("players_in_lobby")));
            }
            case "start_game": { return new StartGameEvent(phoneNumber); }
            case "game_starting": {
                Date timestamp = new Date(eventJson.getLong("timestamp"));
                Date gameEnd = new Date(eventJson.getLong("game_end"));
                List<String> playerNumbers = JsonUtils.jsonStringArrayToList(eventJson.getJSONArray("player_numbers"));
                List<CapturePoint> capturePoints = JsonUtils.jsonCapturePointsArrayToList(
                        eventJson.getJSONArray("capture_points"));
                List<EnemySpawn> enemySpawns = JsonUtils.jsonEnemySpawnArrayToList(
                        eventJson.getJSONArray("enemy_spawns"));
                return new GameStartingEvent(phoneNumber, timestamp, playerNumbers, capturePoints, enemySpawns, gameEnd);
            }

            // Game events
            case "chat_message": {
                return new ChatMessageEvent(phoneNumber, eventJson.getString("message"));
            }
            case "position_update": {
                double latitude = eventJson.getDouble("latitude");
                double longitude = eventJson.getDouble("longitude");
                Date timestamp = new Date(eventJson.getLong("timestamp"));
                return new PositionUpdateEvent(phoneNumber, timestamp, latitude, longitude);
            }
            case "start_capture": {
                Date timestamp = new Date(eventJson.getLong("timestamp"));
                int capturePoint = eventJson.getInt("capture_point");
                return new StartCaptureEvent(phoneNumber, capturePoint, timestamp);
            }
            case "left_capture_point": {
                Date timestamp = new Date(eventJson.getLong("timestamp"));
                int capturePoint = eventJson.getInt("capture_point");
                return new LeftCapturePointEvent(phoneNumber, timestamp, capturePoint);
            }
            case "defeat_enemy": {
                Date timestamp = new Date(eventJson.getLong("timestamp"));
                int capturePoint = eventJson.getInt("capture_point");
                return new DefeatEnemyEvent(phoneNumber, timestamp, capturePoint);
            }
            default: throw new InvalidEventException();
        }
    }

    public abstract void handleEvent(Activity activity);
    public abstract JSONObject getJson() throws JSONException;

    // Compresses and encodes this event into a string
    private String compressAndEncode() throws JSONException {
        Deflater deflater = new Deflater();
        deflater.setInput(getJson().toString().getBytes());
        deflater.finish();

        byte[] result = new byte[1024];
        int len = deflater.deflate(result);

        return Base64.encodeToString(result, 0, len, Base64.DEFAULT);
    }

    public void sendToNumber(String number, Context context) {
        try {
            SmsUtils.sendMessage(number, compressAndEncode(), context);
        } catch (JSONException e) {
            Log.e("JSON", "JSON exception when sending to numbers");
        }
    }

    public void sendToNumbers(List<String> numbers, Context context) {
        String data;
        try {
            data = compressAndEncode();
        } catch (JSONException e) {
            Log.e("JSON", "JSON exception when sending to numbers");
            return;
        }

        for (String number : numbers) {
            SmsUtils.sendMessage(number, data, context);
        }
    }
}
