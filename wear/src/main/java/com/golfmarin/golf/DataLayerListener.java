package com.golfmarin.golf;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Listener service to receive messages from the data layer
 */
public class DataLayerListener extends WearableListenerService {


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(HoleActivity.WEARABLE_MESSAGE_HOLE_PATH)) {

            final String message = new String(messageEvent.getData());
            Log.v("myTag", "Message path received on watch is: " + messageEvent.getPath());
            Log.v("myTag", "Message received on watch is: " + message);


            // Broadcast message to wearable activity for display
            //
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

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        DataMap dataMap;
        for (DataEvent event : dataEvents) {

            // Check the event type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                Log.v("myTag", "Path received on watch: " + path);
                //Verify the data path
                if (path.equals( HoleActivity.WEARABLE_DATA_PATH)) {
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.v("myTag", "DataMap received on watch: " + dataMap);

                    // Broadcast data to wearable activity for display
                    Intent dataIntent = new Intent();
                    dataIntent.setAction(Intent.ACTION_SEND);
                    dataIntent.putExtra("distances", dataMap.toBundle());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(dataIntent);

                } else if (path.equals(HoleActivity.WEARABLE_START_PATH)) {
                    // Create a local notification inviting the user to start the wearable app
                    Log.v("myTag", "Now start the wearable golf app");

                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                    sendLocalNotification(dataMap);

                } else {
                    dataMap = new DataMap();
                 }
            }
        }
    }

    private void sendLocalNotification(DataMap dataMap) {

        int notificationId = 001;

        // Create a pending intent that starts the wearable app and includes the selected course
        Intent startIntent = new Intent(this, HoleActivity.class).setAction(Intent.ACTION_MAIN);
        startIntent.putExtra("course", dataMap.getString("course"));

        PendingIntent startPendingIntent = PendingIntent.getActivity(this, 0, startIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notify =
            new NotificationCompat.Builder(this)
                .setContentTitle(dataMap.getString("title"))
                .setContentText(dataMap.getString("body"))
                .setSmallIcon(R.drawable.golf_marin)
                .setAutoCancel(true)
                .setContentIntent(startPendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notify);
    }
}


