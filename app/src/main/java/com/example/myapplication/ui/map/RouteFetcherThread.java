package com.example.myapplication.ui.map;

import android.util.Log;

import com.example.myapplication.helper.RouteFetcherListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RouteFetcherThread extends Thread {
    private static final String TAG = "RouteFetcherThread";
    private static String API_KEY;
    private JSONObject jsonObject;
    private RouteFetcherListener routeFetcherListener;
    private String urlString = null;

    public RouteFetcherThread(String API_KEY, RouteFetcherListener routeFetcherListener) {
        super();
        this.API_KEY = API_KEY;
        this.routeFetcherListener = routeFetcherListener;
    }

    public void run() {
        Log.d(TAG, "run: current thread: " + Thread.currentThread().getName());
//        String urlString = "https://maps.googleapis.com/" +
//                "maps/" +
//                "api/" +
//                "directions/" +
//                "json?" +
//                "destination=" +
//                destLatLng.latitude +
//                "," +
//                destLatLng.longitude +
//                "&origin=" +
//                srcLatLng.latitude +
//                "," +
//                srcLatLng.longitude +
//                "&key=" + API_KEY;
        Log.d(TAG, "run: URL is: " + urlString);
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            Log.d(TAG, "run: connection has been opened. getting request code.");
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "run: received response code. verifying.");

            if(responseCode != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP Connection NOT OK");
                return;
            }

            Log.d(TAG, "run: response code is HTTP_OK");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();;
            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            Log.d(TAG, "run: fetched response in string. disconnecting now");
            in.close();
            connection.disconnect();

            jsonObject = new JSONObject(response.toString());
            Log.d(TAG, "RouteFetcherThread: run: jsonObject is created. Calling onComplete.");
            routeFetcherListener.onRouteFetchComplete(jsonObject);
        } catch (MalformedURLException e) {
            // Handle the MalformedURLException
            Log.d(TAG, "run: Malformed URL");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "run: IOException.");;
            // Handle the IOException
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(TAG, "run: JSON Error: " + e.getMessage());
        }
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }
}