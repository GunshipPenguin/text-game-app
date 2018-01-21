package com.gunshippenguin.textgame.events;

import android.app.Activity;

import com.gunshippenguin.textgame.CapturePoint;
import com.gunshippenguin.textgame.EnemySpawn;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

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

    public static Event fromJson(String phoneNumber, JSONObject eventJson) throws JSONException, InvalidEventException {
        switch(eventJson.getString("event_type")) {
            // Bootstrapping sequence events
            case "open_lobby": return new OpenLobbyEvent(phoneNumber);
            case "game_lobby_started": return new GameLobbyStartedEvent(phoneNumber);
            case "register": return new RegisterEvent(phoneNumber, eventJson.getString("host_number"));
            case "new_registration": return new NewRegistrationEvent(phoneNumber, JsonUtils.parseStringArray(
                eventJson.getJSONArray("players_in_lobby")));
            case "start_game": return new StartGameEvent(phoneNumber);
            case "game_starting":
                Date timeStamp = new Date(eventJson.getLong("timestamp"));
                List<String> playerNumbers = JsonUtils.parseStringArray(eventJson.getJSONArray("player_numbers"));
                List<CapturePoint> capturePoints = JsonUtils.parseCapturePointArray(
                        eventJson.getJSONArray("capture_points"));
                List <EnemySpawn> enemySpawns = JsonUtils.parseEnemySpawnArray(
                        eventJson.getJSONArray("enemy_spawns"));
                return new GameStartingEvent(phoneNumber, timeStamp, playerNumbers, capturePoints, enemySpawns);
            default:
                throw new InvalidEventException();
        }
    }

    public abstract void handleEvent(Activity activity);

}
