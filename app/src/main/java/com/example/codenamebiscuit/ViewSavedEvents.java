package com.example.codenamebiscuit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.rv.EventAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewSavedEvents extends AppCompatActivity {
    private static final String DATABASE_SAVED_EVENTS_PULLER =
            "http://athena.ecs.csus.edu/~teamone/php/pull_saved_events_list.php";
    private RecyclerView mRecyclerView;
    private EventAdapter mEventAdapter;
    private JSONObject currentUserId = new JSONObject();
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<JSONObject> eventData;
    private SharedPreferences pref;
    private int positionClick;
    private int SPLASH_TIME_OUT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_saved_events);
        Bundle extras = getIntent().getExtras();
        try {
            String userID = extras.getString("user_id");
            Log.i("user id", userID);
            currentUserId.put("user_id", extras.get("user_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setupRecyclerView();
        loadEventData();
        setupSwipeDownRefresh();
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        loadEventData();
    }
    @Override
    public void onStart(){
        super.onStart();
        swipeContainer.setRefreshing(true);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //this method will executed once timer runs out
                swipeContainer.setRefreshing(false);
                //close the activity

            }
        }, SPLASH_TIME_OUT);
        SPLASH_TIME_OUT=2000;

    }

    private void setupRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_saved_events);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mEventAdapter = new EventAdapter(this);
        mRecyclerView.setAdapter(mEventAdapter);
    }
    private void setupSwipeDownRefresh(){
        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer_saved_events);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEventData();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadEventData() {
        if(mRecyclerView!=null)
            mRecyclerView.setVisibility(View.VISIBLE);
        QueryEventList list = (QueryEventList) new QueryEventList(mEventAdapter, DATABASE_SAVED_EVENTS_PULLER).execute(currentUserId);
        eventData =list.getEventList();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_page_menu, menu);

        return true;
    }


}
