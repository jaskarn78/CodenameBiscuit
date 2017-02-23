package com.example.codenamebiscuit;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.helper.SetupDrawer;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewDeletedEvents extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private EventAdapter mEventAdapter;
    private JSONObject currentUserId = new JSONObject();
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences prefs;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private String userID;
    private Toolbar toolbar;
    private int SPLASH_TIME_OUT;
    private ArrayList<JSONObject> eventData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_view_deleted_events);

        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);


        TextView tv = (TextView)findViewById(R.id.toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Black.ttf");
        tv.setTypeface(typeface);

        Bundle extras = getIntent().getExtras();
        try {
            userID = extras.getString("user_id");
            Log.i("user id", userID);
            currentUserId.put("user_id", extras.get("user_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setupRecyclerView();
        loadEventData();
        setupSwipeDownRefresh();
        loadDrawer(savedInstanceState);
    }
    private void loadDrawer(Bundle savedState){
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final String pic = prefs.getString("user_image", null);
        final String fName = prefs.getString("fName", null);
        final String lName = prefs.getString("lName", null);
        final String email = prefs.getString("email", null);
        SetupDrawer setup = new SetupDrawer(headerResult, result, toolbar, currentUserId,
                mEventAdapter, userID, getApplicationContext(), fName, lName, email, pic);
        setup.setupNavDrawer(savedState, this);
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
        SPLASH_TIME_OUT=1000;

    }

    private void setupRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_deleted_events);
        //mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
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
        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
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
     * This method will run an async task to issue a HTTP request to retrieve deleted
     * event information based on the current user id.
     * event information is then loaded into an ArrayList of type JSONObject
     *
     */
    private void loadEventData() {
        if(mRecyclerView!=null)
            mRecyclerView.setVisibility(View.VISIBLE);
        QueryEventList list = (QueryEventList)
                new QueryEventList( mEventAdapter, getString(R.string.DATABASE_DELETED_EVENTS_PULLER), this).execute(currentUserId);
        eventData = list.getEventList();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.main_page_menu, menu);

        return true;
    }

}
