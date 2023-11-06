package com.example.myapplication.service;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.model.MessageEvent;
import com.example.myapplication.ui.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class testerActivity extends AppCompatActivity {

    String TAG = "testerActivity";

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        // Handle the event
        Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
        if(event.message.equals("cancelRide") ){


            AlertDialog.Builder builder = new AlertDialog.Builder(this);  // 'this' is the current Activity context
            builder.setTitle("Ride Cancelled");  // Set the title of the dialog
            builder.setMessage("This ride has been cancelled, try again.");  // Set the message

            // Add positive button (usually "OK" or "Yes" button)
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Code to execute when the button is clicked
                    // For example:
                    // Toast.makeText(getApplicationContext(), "OK clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                    startActivity(intent);
                }
            });

        // Finally, show the dialog
            builder.create().show();
        }
        else if(event.message.equals("completeRide"))
        {
            String fare = event.fare;
            Log.d(TAG, "onMessageEvent: Fare is "+fare);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);  // 'this' is the current Activity context
            builder.setTitle("Ride Completed!");  // Set the title of the dialog
            builder.setMessage("Fare is "+fare+". Please pay the rider.");  // Set the message

            // Add positive button (usually "OK" or "Yes" button)
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Code to execute when the button is clicked
                    // For example:
                    // Toast.makeText(getApplicationContext(), "OK clicked", Toast.LENGTH_SHORT).show();
                    // TODO: 10/27/2023 take me to RideDetails where cost and stuff will be handled
                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                    startActivity(intent);
                }
            });

            // Finally, show the dialog
            builder.create().show();
        }
        else if(event.message.equals("pickedUp"))
        {
            // TODO: 10/27/2023 implement a method that will be run in passengers app
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);
        Button button = findViewById(R.id.send);
        PushNotification pushNotification = new PushNotification();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        // Your network operation here
//                        sendNotification();
//                        LocationDB locationDB = new LocationDB() ;
//                       locationDB.getUserName("1uwuzEe8qShjD1EN6dJrmEZsR8y1", new GetUserNameCallback() {
//                          @Override
//                          public void onUserNameRecieved(String name) {
//                              Log.d(TAG, "run: "+ name );
//
//                          }
//                      });
                        LocationDB locationDB = new LocationDB();
                        locationDB.deleteFromBookedPassenger("5eEHiNS0mIW9CAC6xqbFVdvplnH3" , "ItIDT7jlUDOMRheIs4fif8DTc0A2");
//                         pushNotification.pickedUpFlag("fbyU3dlwQ56zm-KWgQqyzr:APA91bEoN-I15jP2D2yQjTO7wq3Y_CT4veFjc3cmph5in1IPsTOh9NsXV8VdxTh0BNMZT0NQNnZttLd7Y9-KDEh8fj6Sr9PHThfKKQgEDtTWBAyZK4h7gLQ1R3S3D9A9Tgh8og99wFMc");
                        pushNotification.cancelRide("fbyU3dlwQ56zm-KWgQqyzr:APA91bEoN-I15jP2D2yQjTO7wq3Y_CT4veFjc3cmph5in1IPsTOh9NsXV8VdxTh0BNMZT0NQNnZttLd7Y9-KDEh8fj6Sr9PHThfKKQgEDtTWBAyZK4h7gLQ1R3S3D9A9Tgh8og99wFMc");
                    }
                }).start();

            }
        });
    }










    private void sendNotification() {
//        String token = "dB5woOZVQL-arPz4sW_Eyz:APA91bHpOtukD2JqytIPu3Sh9H6OwMNlUfi2VN-WU9WlNyy_Xnpwhuwv1F-rjBR5YYBUWEm3BhTopC6oyP-QzSFG08_3lrDVlTHPvsmvy69l2A6wZKTJh2d70Jav1ygVcF9B4Zf_LChL";
       String token = "fbyU3dlwQ56zm-KWgQqyzr:APA91bEoN-I15jP2D2yQjTO7wq3Y_CT4veFjc3cmph5in1IPsTOh9NsXV8VdxTh0BNMZT0NQNnZttLd7Y9-KDEh8fj6Sr9PHThfKKQgEDtTWBAyZK4h7gLQ1R3S3D9A9Tgh8og99wFMc";
//      String token = "dIZ__2BxR_ioe2FVmmPntm:APA91bGOKbo3obJv6SB1losMROPoiZVE_ncvWTWf_XxMtbWenHG4aMNWPeffEGhgxX3Jjt5v8irsAKDouJXyQoMpjIMERtj3ZPuDL7rVgU_mQHPS1nN3SsFHxseM_GU32N_1YjY6W9qB";
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject notificationJsonObject = new JSONObject();
        JSONObject dataJsonObject = new JSONObject();  // Added this for the data payload
        JSONObject wholeObject = new JSONObject();
        Log.d(TAG, "sendNotification: ");
        try {
            notificationJsonObject.put("title", "Dekh Beta");
            notificationJsonObject.put("body", "oy bala");

            dataJsonObject.put("notification_id", "normal");  // Add your notification_id to the data payload

            wholeObject.put("to", token);
            wholeObject.put("notification", notificationJsonObject);
            wholeObject.put("data", dataJsonObject);  // Include the data payload in the whole object
        } catch(Exception e) {
            Log.d("error", "sendNotification: " + e);
        }


        RequestBody requestBody = RequestBody.create(mediaType , wholeObject.toString());
        Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .addHeader("Authorization" , "key=AAAA6tg_1MQ:APA91bGpqnbF7JL_0WGE972cHOjRKZv0TqW7MuzgU0vhloZ9BSwfqGonqCmhNGjXohbBWxWIZRcVW7_iC7tg7kVdsztBPbJsT83etjR736p9gHng9zMl0Lx2ykWq32yQS7jAZtY4oumw")
                .addHeader("Content-Type" ,"application/json")
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                Log.d("success", "sendNotification: success");
            }
            else{
                Log.d("!success", "Response: " + response.body().string());

            }
        } catch (IOException e) {
            Log.d(" error ", "sendNotification: " + e);
        }


    }
}