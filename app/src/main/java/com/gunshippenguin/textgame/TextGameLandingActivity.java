<<<<<<< HEAD
package com.gunshippenguin.textgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Button;
import android.view.View;
import android.content.Intent;

public class TextGameLandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_game_landing);

        Button startButton = (Button)findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TextGameLandingActivity.this, TextGameMainActivity.class));
            }
        });
    }
}
=======
package com.gunshippenguin.textgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class TextGameLandingActivity extends AppCompatActivity {
    private LandingTextReceiver mReceiver = null;
    private List<String> mPlayerNumbers = null;

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
    }

    protected void eventHandler(JSONObject event) {
        // process event
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
            AsyncTask<Void,Void,List<JSONObject> > asyncHandler = new AsyncTask<Void, Void, List<JSONObject> >() {
                @Override
                protected List<JSONObject> doInBackground(Void... voids) {
                    List<JSONObject> events = new ArrayList<JSONObject>();
                    if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                        for (int i = 0; i < messages.length; ++i) {
                            if (TextGameLandingActivity.this.mPlayerNumbers.contains(messages[i].getDisplayOriginatingAddress())
                                    || true) {
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
                                        events.add(event);
                                    } catch(JSONException e) {
                                        Log.e("BROKEN_JSON",e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                    return events;
                }

                @Override
                protected void onPostExecute(List<JSONObject> events) {
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
>>>>>>> 3e9c75654c6ecd3e71bce19a80f761365e82d63b
