package com.example.codenamebiscuit.helper;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jaskarnjagpal on 2/15/17.
 */

public class SaveEventsOnSwipe extends AsyncTask<JSONObject, Void, Void> {
    private static final String DATABASE_STORE_SAVED_EVENTS =
            "http://athena.ecs.csus.edu/~teamone/php/store_saved_events.php";

    @Override
    protected Void doInBackground(JSONObject... params) {
        JSONObject userJSON = params[0];

        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        try {
            String data;
            if (userJSON != null)
                data = userJSON.toString();  // data is the JSONObject being sent to the php server
            else
                return null;


            // Connect to the URL
            URL url = new URL(DATABASE_STORE_SAVED_EVENTS);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());

            // POST the information to the URL
            wr.write( data );
            wr.flush();

            // Create a means to read the output from the PHP
            reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null) {
                Log.v("PrintLine", line);
            }

            wr.close(); // close OutputStreamWriter
            reader.close(); // close BufferedReader
        } catch (MalformedURLException e) {
            Log.e("MalformedURL", e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }

        return null;
    }
}
