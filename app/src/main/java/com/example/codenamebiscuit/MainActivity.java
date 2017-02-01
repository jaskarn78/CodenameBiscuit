package com.example.codenamebiscuit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.codenamebiscuit.login.FacebookLoginActivity;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private EventAdapter mEventAdapter;


    private static final String DATABASE_CONNECTION_LINK =
            "http://athena.ecs.csus.edu/~teamone/php/user_insert.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_events);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mEventAdapter = new EventAdapter();

        mRecyclerView.setAdapter(mEventAdapter);
        loadEventData();

        if (AccessToken.getCurrentAccessToken() == null) {
            Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
            startActivity(intent);
        }


    }
    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        loadEventData();
        //Refresh your stuff here
    }

    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadEventData() {
        mRecyclerView.setVisibility(View.VISIBLE);
        new QueryEventsList().execute();
    }

    public class QueryEventsList extends AsyncTask<Void, Void, ArrayList<JSONObject>> {
        private static final String DATABASE_MAIN_EVENTS_PULLER =
                "http://athena.ecs.csus.edu/~teamone/php/pull_main_events_list.php";

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
            if (objs != null) {
                ArrayList<JSONObject> eventList = objs;
                mEventAdapter.setEventData(eventList);
                mRecyclerView.setVisibility(View.VISIBLE);

            }

        }

    }


    /**********************************************************************************************
     Create Menu
     **********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_page_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.events_list_menu_action) {
            Intent startUserSettingsActivity = new Intent(this, UserSettingsActivity.class);
            startActivity(startUserSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
