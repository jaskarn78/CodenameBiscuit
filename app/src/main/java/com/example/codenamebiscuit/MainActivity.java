package com.example.codenamebiscuit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.helper.RecyclerItemClickListener;
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
import java.util.ArrayList;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private EventAdapter mEventAdapter;
    private JSONObject currentUserId = new JSONObject();


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

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int position) {
                Log.v("input", String.valueOf(position));
                Toast.makeText(getApplicationContext(), "Click on item "+position+"", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        if (AccessToken.getCurrentAccessToken() == null) {
            Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
            startActivity(intent);
        }

        try {
            currentUserId.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
            Log.v("PrintLine", AccessToken.getCurrentAccessToken().getUserId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadEventData();

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
        new QueryEventList(mEventAdapter, mRecyclerView).execute(currentUserId);
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
