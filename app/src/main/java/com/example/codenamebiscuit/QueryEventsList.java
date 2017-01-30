package com.example.codenamebiscuit;

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

import static android.R.attr.data;

/**
 * Created by Tommy on 1/30/17.
 */

public class QueryEventsList extends AsyncTask<Void, Void, ArrayList<JSONObject>> {
    private static final String DATABASE_MAIN_EVENTS_PULLER =
            "http://athena.ecs.csus.edu/~teamone/php/pull_main_events_list.php";

    private static ArrayList<JSONObject> mEventList;

    @Override
    protected ArrayList<JSONObject> doInBackground(Void... voids) {

        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        try {
            // Connect to the URL
            URL url = new URL(DATABASE_MAIN_EVENTS_PULLER);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());

            // POST the information to the URL
            wr.write( data );
            wr.flush();

            // Create a means to read the output from the PHP
            reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null) {
                Log.v("PrintLine", line);
                sb.append(line);
            }

            JSONArray jArray;
            jArray = new JSONArray(sb.toString());
            ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();

            if (jArray != null) {
                for (int i=0;i<jArray.length();i++){
                    eventList.add(jArray.getJSONObject(i));
                    Log.v("PrintLine", eventList.get(i).toString());
                }
            }

            wr.close(); // close OutputStreamWriter
            reader.close(); // close BufferedReader

            return eventList;
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

    @Override
    protected void onPostExecute(ArrayList<JSONObject> objs) {
        if (objs != null)
            mEventList = objs;
    }
}
