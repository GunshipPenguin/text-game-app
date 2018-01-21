package com.gunshippenguin.textgame;

import android.telephony.SmsManager;

public class SmsUtils {

    private SmsUtils() {}

    public static void sendMessage(String number, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, message, null, null);
    }
}
