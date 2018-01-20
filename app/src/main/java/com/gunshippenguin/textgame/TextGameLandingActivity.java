
package com.gunshippenguin.textgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class TextGameLandingActivity extends AppCompatActivity {
    private LandingTextReceiver mReceiver = null;
    private ArrayList<String> mPlayerNumbers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_game_landing);

        if (mReceiver == null) {
            mReceiver = new LandingTextReceiver();
            registerReceiver(mReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        }


        if (mPlayerNumbers == null) {
            mPlayerNumbers = new  ArrayList<String>();
        }

        Button startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TextGameLandingActivity.this, TextGameMainActivity.class);
                intent.putStringArrayListExtra("PLAYER_NUMBERS", mPlayerNumbers);
                startActivity(intent);
            }
        });

    }

    protected void eventHandler(Event event) {
        // process event
        try {
            String eventType = event.getEvent().getString("event_type");

            // Handle (server -> client) events
            switch(eventType) {
                case Event.GAME_LOBBY_STARTED:
                    break;
                case Event.NEW_REGISTRATION:
                    break;
                case Event.GAME_STARTING:
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
                                    events.add(new Event(event, messages[i].getDisplayOriginatingAddress()));
                                } catch(JSONException e) {
                                    Log.e("BROKEN_JSON",e.getMessage());
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
                            TextGameLandingActivity.this.eventHandler(events.get(i));
                        }
                    }
                    mPendingResult.finish();
                }
            };
            asyncHandler.execute();

        }
    }
}
