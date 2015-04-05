package com.golfmarin.golf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by michaelHahn on 3/15/15.
 * Listener to receive golf course or hole number from wearable
 */
public class DataLayerListener extends WearableListenerService {

    private static final String TAG = "HandheldDataLayer";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(HoleActivity.WEARABLE_MESSAGE_HOLE_PATH)) {

            final String message = new String(messageEvent.getData());
            Log.v("myTag", "Message path received on watch is: " + messageEvent.getPath());
            Log.v("myTag", "Message received on watch is: " + message);

            // Save hole number to shared preference for a persistent state
            Context ctx = getApplicationContext();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor prefEditor = sharedPref.edit();
            prefEditor.putString("hole",message);
            prefEditor.commit();

            String storedMessage = sharedPref.getString("hole", "");
            Log.i(TAG, "Saved hole number: " + storedMessage);


            // Broadcast message to wearable activity for display
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("path", HoleActivity.WEARABLE_MESSAGE_HOLE_PATH);
            messageIntent.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }

    }

}
