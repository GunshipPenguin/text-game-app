package com.gunshippenguin.textgame;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class SmsUtils {

    private SmsUtils() {}

    public static void sendMessage(String number, String message, Context context) {
        SmsManager sms = SmsManager.getDefault();
        PendingIntent pi;
        pi = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);

        sms.sendTextMessage(number, null, message, pi, null);
    }
}
