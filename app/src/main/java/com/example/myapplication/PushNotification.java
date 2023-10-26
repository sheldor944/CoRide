package com.example.myapplication;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PushNotification {
    String TAG = "PushNotification";

    public void pickedUpFlag(String token)
    {
        new Thread(new Runnable() {
            public void run() {
                // Your network operation here
                pickedUpFlagUtility(token);
            }
        }).start();
    }
    public  void cancelRide(String token)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cancelRideUtility(token);
            }
        }).start();
    }
    public  void completeRide(String token)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                completeRideUtility(token);
            }
        }).start();
    }
    public void completeRideUtility(String token)
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject notificationJsonObject = new JSONObject();
        JSONObject dataJsonObject = new JSONObject();  // Added this for the data payload
        JSONObject wholeObject = new JSONObject();
        Log.d(TAG, "sendNotification: completeRide ");
        try {
            notificationJsonObject.put("title", "Complete");
            notificationJsonObject.put("body", "RIDE HAS BEEN Completed");

            dataJsonObject.put("notification_id", "completeRide");  // Add your notification_id to the data payload

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
    public void cancelRideUtility(String token)
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject notificationJsonObject = new JSONObject();
        JSONObject dataJsonObject = new JSONObject();  // Added this for the data payload
        JSONObject wholeObject = new JSONObject();
        Log.d(TAG, "sendNotification: cancelRide ");
        try {
            notificationJsonObject.put("title", "CANCEL!!!");
            notificationJsonObject.put("body", "RIDE HAS BEEN CANCELED");

            dataJsonObject.put("notification_id", "cancelRide");  // Add your notification_id to the data payload

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

    public void pickedUpFlagUtility(String token)
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject notificationJsonObject = new JSONObject();
        JSONObject dataJsonObject = new JSONObject();  // Added this for the data payload
        JSONObject wholeObject = new JSONObject();
        Log.d(TAG, "sendNotification: ");
        try {
            notificationJsonObject.put("title", "Dekh Beta");
            notificationJsonObject.put("body", "oy bala");

            dataJsonObject.put("notification_id", "pickedUp");  // Add your notification_id to the data payload

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
