package com.example.codenamebiscuit;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.Profile;

import org.json.JSONException;
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

public class SigninActivity extends AsyncTask<String, Void, Boolean> {
    /* DATABASE VARIABLE NAMES FOR USERS TABLE */
    private static final String USER_NAME_FIELD    = "user_name";
    private static final String USER_EMAIL_FIELD   = "user_email";
    private static final String USER_ID_FIELD      = "user_id";

    private String mUserEmail;

    @Override
    protected Boolean doInBackground(String... strings) {
        String link = strings[0];
        mUserEmail = strings[1];

        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        try {
            // Create User JSONObject
            JSONObject obj = createUserJSON();

            String data;
            if (obj != null)
                data = obj.toString();  // data is the JSONObject being sent to the php server
            else
                return false;

            // Connect to the URL
            URL url = new URL(link);
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


    /**
     * createUserJSON
     *  Creates a JSONObject with the user's information stored inside of it.
     *  Returns null if user profile does not exist
     * @return JSONObject: JSONObject {"user_id", "user_name", "user_email"}
     */
    private JSONObject createUserJSON() {
        // Grab the current user's facebook profile information
        Profile userProfile = Profile.getCurrentProfile();
        if (userProfile == null) {
            return null;
        }

        try {
            // grab the user information, if it does not exist it will be NULL
            JSONObject jsonUser = new JSONObject();
            jsonUser.put(USER_NAME_FIELD, userProfile.getName());
            jsonUser.put(USER_ID_FIELD, userProfile.getId());
            jsonUser.put(USER_EMAIL_FIELD, mUserEmail);

            return jsonUser;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
