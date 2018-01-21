
package com.gunshippenguin.textgame;

import android.*;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gunshippenguin.textgame.events.Event;
import com.gunshippenguin.textgame.events.InvalidEventException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class TextGameLandingActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 17;
    private LandingTextReceiver mReceiver = null;
    private ArrayList<String> mPlayerNumbers = null;
    private Button mStartButton = null;
    private Button mJoinButton = null;
    private ListView mLobbyListings = null;
    private PlayerAdapter mAdapter = null;
    private Boolean mLobbyLeader = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_game_landing);

        if (mReceiver == null) {
            mReceiver = new LandingTextReceiver();
            registerReceiver(mReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        }

        if (mPlayerNumbers == null) {
            mPlayerNumbers = new ArrayList<String>();
        }

        if (mAdapter == null) {
            mAdapter = new PlayerAdapter(this,R.layout.player_name);
        }
        getLobbyList(this).setAdapter(mAdapter);
    }

    public void onClickStart(View v) {
        if (mLobbyLeader) {
            handleRequestStartGame();
        } else {
            handleRequestOpenLobby();
        }
    }

    public void onClickPause(View v) {
        handleSelfRegistration();
    }

    protected void eventHandler(Event event) {
        // process event
        try {
            String eventType = event.getEvent().getString("event_type");

            // Handle (server -> client) events
            switch(eventType) {
                case Event.GAME_LOBBY_STARTED:
                    handleGameLobbyStarted(event);
                    break;
                case Event.NEW_REGISTRATION:
                    handleNewRegistration(event);
                    break;
                case Event.GAME_STARTING:
                    handleGameStarting(event);
                    break;
                default:
                    break;

            }
        } catch(JSONException e) {
            Log.e("BROKEN_EVENT",e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReceiver == null) {
            mReceiver = new LandingTextReceiver();
            registerReceiver(mReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Until you grant the permission, we can't get your friends' names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Button getStartButton(AppCompatActivity activity) {
        if (mStartButton == null) {
            mStartButton = activity.findViewById(R.id.startButton);
        }
        return mStartButton;
    }

    public Button getJoinButton(AppCompatActivity activity) {
        if (mJoinButton == null) {
            mJoinButton = activity.findViewById(R.id.joinButton);
        }
        return mJoinButton;
    }

    public ListView getLobbyList(AppCompatActivity activity) {
        if (mLobbyListings == null) {
            mLobbyListings = activity.findViewById(R.id.playerList);
        }
        return mLobbyListings;
    }

    public Boolean lobbyLeader() {
        return mLobbyLeader;
    }

    public void setLobbyLeader(Boolean isLeader) {
        mLobbyLeader = isLeader;
    }

    public Boolean cleanStatefulTransitions(int newNumPlayers) {
        // Something went wrong, either lost something in
        // as state transition or skipped adding a player
        if (newNumPlayers > (mPlayerNumbers.size() + 1)) {
            mAdapter.clear();
            mPlayerNumbers.clear();
            return true;
        }
        return false;
    }

    public void addPlayer(String phoneNumber) {
        if (!mPlayerNumbers.contains(phoneNumber)) {
            mPlayerNumbers.add(phoneNumber);
            mAdapter.add(getContactNameByNumber(phoneNumber));
        }
    }

    private void handleRequestOpenLobby() {
        // request the server to open the lobby
    }

    private void handleGameLobbyStarted(Event event) {
        mLobbyLeader = true;
        getStartButton(this).setText(R.string.start_game);
        getStartButton(this).setEnabled(true);
        getJoinButton(this).setEnabled(false);
    }

    private void handleSelfRegistration() {
        //send a request to the server to join a lobby
    }

    private void handleNewRegistration(Event event) {
        try {
            JSONArray numbers = event.getEvent().getJSONArray("players_in_lobby");

            for (int i = 0; i < numbers.length(); i++) {
                String temp = numbers.getString(i);
                if (!mPlayerNumbers.contains(temp)) {
                    mPlayerNumbers.add(temp);
                    mAdapter.add(getContactNameByNumber(temp));
                }
            }

            if (!mLobbyLeader) {
                getJoinButton(this).setEnabled(false);
                getStartButton(this).setEnabled(false);
            }

        } catch(JSONException e) {
            Log.e("REGISTRATION_BROKEN",e.getMessage());
        }
    }

    private void handleRequestStartGame() {
        //we are requesting the server to start the game
    }

    private void handleGameStarting(Event event) {
        String stringifiedJSON = event.getEvent().toString();
        Deflater compressor = new Deflater();
        compressor.setInput(stringifiedJSON.getBytes());
        byte[] result = new byte[300];
        compressor.finish();
        compressor.deflate(result);
        String compressedJSON = Base64.encodeToString(result,Base64.DEFAULT);

        // launch an intent
    }

    public String getContactNameByNumber(String number) {
        String name = "?";

        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

            ContentResolver contentResolver = getContentResolver();
            Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                    ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

            try {
                if (contactLookup != null && contactLookup.getCount() > 0) {
                    contactLookup.moveToNext();
                    name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    // String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                }
            } finally {
                if (contactLookup != null) {
                    contactLookup.close();
                }
            }

        }
        return name;
    }

    public class PlayerAdapter extends ArrayAdapter<String> {
        public PlayerAdapter(Context context, int layoutID) {
            super(context,layoutID);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String playerName = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_name,parent,false);
            }

            TextView name = (TextView) convertView.findViewById(R.id.playerName);
            name.setText(playerName);
            return convertView;
        }
    }

    public class LandingTextReceiver extends BroadcastReceiver {
        private PendingResult mPendingResult;
        @Override
        public void onReceive(final Context context, final Intent intent) {
            mPendingResult = goAsync();
            AsyncTask<Void,Void,List<Event> > asyncHandler = new AsyncTask<Void, Void, List<Event> >() {
                @Override
                protected List<Event> doInBackground(Void... voids) {
                    List<Event> events = new ArrayList<Event>();
                    if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                        for (int i = 0; i < messages.length; ++i) {
                            // if the message is from someone unexpected, this will error out
                            // because we won't be able to parse the format
                            Boolean failed = false;
                            Inflater decompresser = new Inflater();
                            byte[] messageText = Base64.decode(messages[i].getMessageBody(),Base64.DEFAULT);
                            decompresser.setInput(messageText,0,messageText.length);
                            byte[] result = new byte[100];
                            StringBuilder stringifiedJSON = new StringBuilder();
                            while (!decompresser.finished()) {
                                try{
                                    decompresser.inflate(result);
                                } catch(DataFormatException e) {
                                    failed = true;
                                    break;
                                }

                                stringifiedJSON.append(new String(result));
                            }

                            if (!failed) {
                                try {
                                    JSONObject event = new JSONObject(stringifiedJSON.toString());
                                    events.add(Event.fromJson(messages[i].getDisplayOriginatingAddress(),event));
                                } catch(JSONException e) {
                                    Log.e("BROKEN_JSON",e.getMessage());
                                } catch(InvalidEventException e2) {
                                    Log.e("BROKEN_EVENT",e2.getMessage());
                                }
                            }
                        }
                    }

                    return events;
                }

                @Override
                protected void onPostExecute(List<Event> events) {
                    if (events != null) {
                        for (int i = 0; i < events.size(); ++i) {
                            events.get(i).handleEvent(TextGameLandingActivity.this);
                        }
                    }
                    mPendingResult.finish();
                }
            };
            asyncHandler.execute();

        }
    }
}
