package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class testerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);
        Button button = findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        // Your network operation here
                        sendNotification();
                    }
                }).start();

            }
        });
    }

    private void sendNotification() {
        String token = "dB5woOZVQL-arPz4sW_Eyz:APA91bHpOtukD2JqytIPu3Sh9H6OwMNlUfi2VN-WU9WlNyy_Xnpwhuwv1F-rjBR5YYBUWEm3BhTopC6oyP-QzSFG08_3lrDVlTHPvsmvy69l2A6wZKTJh2d70Jav1ygVcF9B4Zf_LChL";
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject notificationJsonObject = new JSONObject();
        JSONObject wholeObject = new JSONObject();

        try {
            notificationJsonObject.put("title" , "Dekh Beta");
            notificationJsonObject.put("body", "oy bala");
            wholeObject.put("to" , token);
            wholeObject.put("notification" , notificationJsonObject);
        }
        catch(Exception e ){
            Log.d("error", "sendNotification: "+ e);
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