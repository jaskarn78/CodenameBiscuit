package com.example.codenamebiscuit.helper;

/**
 * Created by jaskarnjagpal on 4/12/17.
 */

import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by jaskarnjagpal on 2/2/17.
 */

public class DistanceCalculator extends AsyncTask<JSONObject, Void, ArrayList<JSONObject>> {

    private String main_events;
    private ArrayList<JSONObject> distanceList;

    public DistanceCalculator(String main_events) {
        this.main_events = main_events;
    }

    @Override
    protected ArrayList<JSONObject> doInBackground(JSONObject... params) {
        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        try {

            // Connect to the URL
            URL url = new URL(main_events);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());

            // Create a means to read the output from the PHP
            reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jArray;
            jArray = new JSONArray(sb.toString());
            distanceList = new ArrayList<JSONObject>();

            for (int i = 0; i < jArray.length(); i++) {
                distanceList.add(jArray.getJSONObject(i));
                Log.i("JArray",jArray.getJSONObject(i).toString());
            }

            wr.close(); // close OutputStreamWriter
            reader.close(); // close BufferedReader

            return distanceList;
        } catch (MalformedURLException e) {
            Log.e("MalformedURL", e.toString());
            return null;
        } catch (IOException e) {
            Log.e("IOException", e.toString());
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}