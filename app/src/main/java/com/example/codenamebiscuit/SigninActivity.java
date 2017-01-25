package com.example.codenamebiscuit;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Tommy on /23/17.
 */

public class SigninActivity extends AsyncTask<String, Void, JSONArray> {
    /* DATABASE VARIABLE NAMES FOR USERS TABLE */
    private static final String USER_NAME_FIELD    = "user_name";
    private static final String USER_EMAIL_FIELD   = "user_email";
    private static final String USER_ID_FIELD      = "user_id";

    private String mUserEmail;

    @Override
    protected JSONArray doInBackground(String... strings) {
        String link = strings[0];
        String checkUserIdentifier = strings[1];
        String createUserIdentifier = strings[2];
        String userIdIdentifier = strings[3];
        String userId = strings[4];
        mUserEmail = strings[5];

        if (userId.equals("")) {
            Log.e("FacebookEmailRequest", "Could not pull user email from Facebook");
            return null;
        }

        try {
            // Create encoded URL for POST user_id
            String data = URLEncoder.encode(userIdIdentifier, "UTF-8")
                    + "=" + URLEncoder.encode(userId, "UTF-8");

            String urlLink = link + "/" + checkUserIdentifier;

            JSONArray jsonArray = postToDatabase(urlLink, data);

            if (jsonArray == null) {
                JSONObject jsonObject = createUserJSON();
                if (jsonObject != null) {
                    urlLink = link + "/" + createUserIdentifier;
                    data = jsonObject.toString();
                    jsonArray = postToDatabase(urlLink, data);
                }
            }

            return jsonArray;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONArray result) {
        if (result != null) {
            if (result.length() != 0) {
                Log.v("SUCCESS", result.toString());
            } else {
                Log.v("FAILURE", "womp");
            }
        }
        Log.v("NULLLLLL", "womp");
    }


    /**
     * postToDatabase
     *  Takes the data and POSTS it to the php server, with URL "link"
     * @param link: URL of the php server
     * @param data: Data being POSTED to the server
     * @return JSONArray: the return value from php server
     */
    private JSONArray postToDatabase(String link, String data) {
        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        try {
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

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null) {
                Log.v("PrintLine", line);
                sb.append(line);
            }

            // Create an array of the user's profile information
            JSONArray jsonArray = new JSONArray(sb.toString());

            if (jsonArray.length() == 0) {
                return null;
            } else {
                return jsonArray;
            }

            // Catch all errors associated with posting to
        } catch (MalformedURLException e) {
            Log.e("MalformedURL", e.toString());
            return null;
        } catch (IOException e) {
            Log.e("IOException", e.toString());
            return null;
        } catch (JSONException e) {
            Log.e("JSONException", e.toString());
            return null;
        } finally {
            try {
                if (wr != null)
                    wr.close();
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
