package com.gunshippenguin.textgame;

import org.json.JSONObject;

/**
 * Created by gibber9809 on 2018-01-20.
 */

public class Event {

    // pre-game events

    //(client->server) : make game lobby
    final public static String OPEN_LOBBY = "open_lobby";
    //(server->client) : game lobby made
    final public static String GAME_LOBBY_STARTED = "game_lobby_started";
    //(client->server) : request registration
    final public static String REGISTER = "register";
    //(server->client) : new player added
    final public static String NEW_REGISTRATION = "new_registration";
    //(client->server) : request game start
    final public static String START_GAME = "start_game";
    //(server->client) : game is starting
    final public static String GAME_STARTING = "game_starting";

    // game events

    //(client->client) : chat message
    final public static String CHAT = "chat_message";
    //(client->client) : new position for some player
    final public static String POSITION_UPDATE = "position_update";
    //(client->clinet) : player started capturing a point
    final public static String START_CAPTURE = "start_capture";
    //(client->client) : player left a point
    final public static String LEFT_POINT = "left_capture_point";
    //(client->client) : an enemy was defeated
    final public static String ENEMY_DEFEATED = "defeat_enemy";

    private JSONObject mEvent;
    private String mPhoneNumber;

    Event() {
        mEvent = null;
        mPhoneNumber = null;
    }

    Event(JSONObject event, String phoneNumber) {
        mEvent = event;
        mPhoneNumber = phoneNumber;
    }

    public final String getPhoneNumber() {
        return mPhoneNumber;
    }

    public final JSONObject getEvent() {
        return mEvent;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public void setEvent(JSONObject event) {
        mEvent = event;
    }
}
