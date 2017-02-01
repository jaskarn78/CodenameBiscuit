package com.example.codenamebiscuit;

/**
 * Created by jaskarnjagpal on 2/1/17.
 */

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
import java.util.ArrayList;


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
import java.util.ArrayList;

/**
 * Created by Tommy on 1/30/17.
 */

public class pushUserId extends AsyncTask<JSONObject, Void, Void> {
    private static final String DATABASE_MAIN_EVENTS_PULLER =
            "http://athena.ecs.csus.edu/~teamone/php/pull_main_events_list.php";


    @Override
    protected Void doInBackground(JSONObject... objs) {
        JSONObject userJSON = objs[0];

        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        try {
            String data;
            if (userJSON != null)
                data = userJSON.toString();  // data is the JSONObject being sent to the php server
            else
                return null;


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
