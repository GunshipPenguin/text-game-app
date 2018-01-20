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

import java.util.List;

public class TextGameLandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_game_landing);
        registerReceiver(new LandingTextReceiver(), new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
    }

    protected void dummyHandler(String number) {
        // send a toast
        Toast.makeText(this,number,Toast.LENGTH_LONG).show();
    }

    public class LandingTextReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final PendingResult pendingResult = goAsync();
            AsyncTask<Void,Void,SmsMessage[]> asyncHandler = new AsyncTask<Void, Void, SmsMessage[]>() {
                @Override
                protected SmsMessage[] doInBackground(Void... voids) {
                    if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                        pendingResult.finish();
                        return messages;
                    }
                    pendingResult.finish();
                    return null;
                }

                @Override
                protected void onPostExecute(SmsMessage[] results) {
                    if (results.equals(null))
                        return;

                    for (int i = 0; i < results.length; ++i) {
                        TextGameLandingActivity.this.dummyHandler(results[i].getDisplayOriginatingAddress());
                    }
                }
            };
            asyncHandler.execute();

        }
    }
}
