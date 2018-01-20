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
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TextGameLandingActivity extends AppCompatActivity {
    private LandingTextReceiver mReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_game_landing);

        if (mReceiver.equals(null)) {
            mReceiver = new LandingTextReceiver();
            registerReceiver(mReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        }
    }

    protected void dummyHandler(String number) {
        // send a toast
        Toast.makeText(this,number,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReceiver.equals(null)) {
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

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final PendingResult pendingResult = goAsync();
            AsyncTask<Void,Void,List<JSONObject> > asyncHandler = new AsyncTask<Void, Void, List<JSONObject> >() {
                @Override
                protected List<JSONObject> doInBackground(Void... voids) {
                    if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                        List<JSONObject> events = new ArrayList<JSONObject>();

                        pendingResult.finish();
                        return events;
                    }
                    pendingResult.finish();
                    return null;
                }

                @Override
                protected void onPostExecute(List<JSONObject> events) {
                    /*if (results.equals(null))
                        return;

                    for (int i = 0; i < results.length; ++i) {
                        TextGameLandingActivity.this.dummyHandler(results[i].getDisplayOriginatingAddress());
                    }*/
                }
            };
            asyncHandler.execute();

        }
    }
}
