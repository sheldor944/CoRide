/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myapplication.data.model.MessageEvent;
import com.example.myapplication.ui.map.RideDetailsOnMapActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    String TAG = "FCM";
    private static final String CHANNEL_ID = "11223344";
    private static final String CHANNEL_NAME = "Chat Notifications";
    private boolean intentFlag = false;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "onMessageReceived: Received");
//        String notificationId = remoteMessage.getData().get("notification_id");

        if (remoteMessage.getData().size() > 0) {
            String notificationId = remoteMessage.getData().get("notification_id");
            Log.d(TAG, "onMessageReceived: id is  " + notificationId);
            if (notificationId != null) {
                if ("cancelRide".equals(notificationId)) {
                    // Perform action based on this ID
                    Log.d(TAG, "onMessageReceived:  rideCanceled ");
                    EventBus.getDefault().post(new MessageEvent("This is the message"));

                }
                if ("pickedUp".equals(notificationId)) {
                    Log.d(TAG, "onMessageReceived: PickedUp");
                    isPickedUP();
                }
                if ("completeRide".equals(notificationId)) {
                    Log.d(TAG, "onMessageReceived: ride has been Completed bro ");
                    isRideCompleted();
                }
                // You can add more conditions here for other IDs

                Log.d(TAG, "onMessageReceived: Recieved ");
                // Check if message contains a data payload.
                if (remoteMessage.getData().size() > 0) {
                    Log.d(TAG, "onMessageReceived: first if o gese ");
                    if ("SOME_VALUE".equals(remoteMessage.getData().get("key_name"))) {
                        Log.d(TAG, "onMessageReceived: Secong method ");

                    }
                }
                // Create a notification channel for API 26+
                createNotificationChannel();

            }
        }
    }

            private void notificationBuilder () {
                // Your method code here
                Intent intent = new Intent(this, ChatActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)  // Your icon
                        .setContentTitle("Your Notification Title")
                        .setContentText("Your Notification Text")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());
            }
            public boolean isRideCanceled () {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);  // 'this' is the current Activity context
                builder.setTitle("Dialog Title");  // Set the title of the dialog
                builder.setMessage("This is a sample message for the dialog.");  // Set the message

// Add positive button (usually "OK" or "Yes" button)
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Code to execute when the button is clicked
                        // For example:
                        // Toast.makeText(getApplicationContext(), "OK clicked", Toast.LENGTH_SHORT).show();
                    }
                });

// Optionally, add a negative button (usually "Cancel" or "No" button)
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Code to execute when the button is clicked
                        // For example:
                        // Toast.makeText(getApplicationContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                    }
                });

// Optionally, add a neutral button (usually used for options like "Remind me later")
                builder.setNeutralButton("Remind Me Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Code to execute when the button is clicked
                    }
                });

// Finally, show the dialog
                builder.create().show();

                return true;
            }
            public boolean isRideCompleted () {
                return true;
            }
            public boolean isPickedUP () {
                return true;
            }

            // New method to create the notification channel
            private void createNotificationChannel () {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
            }
        }
