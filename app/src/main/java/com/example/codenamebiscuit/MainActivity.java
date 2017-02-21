package com.example.codenamebiscuit;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.codenamebiscuit.rv.ClickListener;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ClickListener{
    private RecyclerView mRecyclerView;
    private EventAdapter mEventAdapter;
    private JSONObject currentUserId = new JSONObject();
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<JSONObject> eventData;
    private SharedPreferences pref;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        mPlanetTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);




        if (findViewById(R.id.swipeContainer) != null) {
            if (savedInstanceState != null) {
                return;
            }
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        setupRecyclerView();
        setupSwipeDownRefresh();
        loadEventData();
        checkIfFbOrGoogleLogin();


    }


    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        mEventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadEventData();


    }
    @Override
    public void onPause(){
        super.onPause();
    }

    private void checkIfFbOrGoogleLogin() {
        if (AccessToken.getCurrentAccessToken() == null && pref.getString("user_idG", null) == null) {
            Intent intent = new Intent(MainActivity.this, ChooseLogin.class);
            startActivity(intent);
        }

        if (AccessToken.getCurrentAccessToken() != null) {
            try {
                currentUserId.put("user_id", AccessToken.getCurrentAccessToken().getUserId());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (pref.getString("user_idG", null) != null && AccessToken.getCurrentAccessToken() == null) {
            try {
                currentUserId.put("user_id", pref.getString("user_idG", null));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //onClick();
    }


    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_events);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mEventAdapter = new EventAdapter(this);
        mRecyclerView.setAdapter(mEventAdapter);
        mEventAdapter.setClickListener(this);
    }

    private void setupSwipeDownRefresh() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
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

    private void loadEventData() {
        QueryEventList list = (QueryEventList)
                new QueryEventList(mEventAdapter, getString(R.string.DATABASE_MAIN_EVENTS_PULLER), this).execute(currentUserId);
        eventData = list.getEventList();
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
        if (itemId == R.id.ChangeView) {
            Intent intent = new Intent(this, SwipeEvents.class);
            intent.putExtra("user_id", currentUserId + "");
            ArrayList<String> data = new ArrayList<String>();
            for (int i = 0; i < mEventAdapter.getObject().size(); i++) {
                data.add(mEventAdapter.getObject().get(i).toString());

            }
            JSONArray jsonArray = new JSONArray(mEventAdapter.getObject());
            intent.putStringArrayListExtra("data", data);
            intent.putExtra("jArray", jsonArray.toString());

            startActivity(intent);
            return true;
        }
        if (itemId == R.id.events_list_saved) {
            Intent intent = new Intent(this, ViewSavedEvents.class);
            try {
                intent.putExtra("user_id", currentUserId.get("user_id").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //RefWatcher refWatcher = MainActivity.getRefWatcher(this);
        //refWatcher.watch(this);
    }


    @Override
    public void itemClicked(View view, int position) {
        RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.extend);
        if (layout.getVisibility() == View.GONE) {
            layout.setVisibility(View.VISIBLE);

        }

        else {
            layout.setVisibility(View.GONE);

        }
    }
}
