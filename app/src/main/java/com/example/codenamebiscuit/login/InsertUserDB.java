package com.example.codenamebiscuit.login;

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
 * Created by Tommy on /23/17.
 */

public class InsertUserDB extends AsyncTask<JSONObject, Void, Boolean> {
    private static final String DATABASE_CONNECTION_LINK =
            "http://athena.ecs.csus.edu/~teamone/php/user_insert2.php";

    @Override
    protected Boolean doInBackground(JSONObject... objs) {
        JSONObject userInfo = objs[0];

        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        try {
            String data;
            if (userInfo != null)
                data = userInfo.toString();  // data is the JSONObject being sent to the php server
            else
                return false;

            // Connect to the URL
            URL url = new URL(DATABASE_CONNECTION_LINK);
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

            return true;
        } catch (MalformedURLException e) {
            Log.e("MalformedURL", e.toString());
            return false;
        } catch (IOException e) {
            Log.e("IOException", e.toString());
            return false;
        }
    }
}
