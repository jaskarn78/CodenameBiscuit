package com.example.codenamebiscuit;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
    private int SPLASH_TIME_OUT;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    // Make sure to be using android.support.v7.app.ActionBarDrawerToggle version.
    // The android.support.v4.app.ActionBarDrawerToggle has been deprecated.
    private ActionBarDrawerToggle drawerToggle;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);


        // Set a Toolbar to replace the ActionBar.
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);


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

    /**
     * setup for navigation drawer
     * @return
     */
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        swipeContainer.setRefreshing(true);
        loadEventData();
        swipeContainer.setRefreshing(false);
        // mEventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeContainer.setRefreshing(true);
        loadEventData();
        swipeContainer.setRefreshing(false);
        //close the activity

    }
    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * Check if logged in user is logged in through facebook or through google
     * to obtain the correct user id to pass to the database which in
     * turn will provide all events based on a preferences selected by a user id
     */
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


    /**
     * sets up recycler view and assigns layout
     * assigns mEventAdapter which contains all event information retrieved from MySQL request
     *
     */
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

    /**
     * sets up refresh layout
     *when swiping down to refresh, method calls loadEventData() to
     * retrieve any new events that may have been added
     */

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

    /**
     * HTTP request to run python script which contains sql command
     * to retrieve all event data filtered by user id
     */
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

    /**
     * Check to see which menu item is clicked
     *
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return super.onOptionsItemSelected(item);
        }

        //if "User Settings" is selected, start user settings activity
        if (itemId == R.id.events_list_menu_action) {
            Intent startUserSettingsActivity = new Intent(this, UserSettingsActivity.class);
            startActivity(startUserSettingsActivity);
            return true;
        }
        //If "Change View" is selected, start full screen swiping activity
        //pass data from mEventAdapter to next activity via intent
        //need to create arraylist to pass data via intent
        //cannot pass objects unless object is parcelable
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
        //if "Saved Events" is selected, start Saved Events activity
        //User id is passed via intent to retrieve saved event information for current user
        if (itemId == R.id.events_list_saved) {
            Intent intent = new Intent(this, ViewSavedEvents.class);
            try {
                intent.putExtra("user_id", currentUserId.get("user_id").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(intent);

        }
        if(itemId==R.id.events_list_deleted){
            Intent intent = new Intent(this, ViewDeletedEvents.class);
            try{
                intent.putExtra("user_id", currentUserId.get("user_id").toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();;
    }

    /**
     * Handles the drop down functionality in the list view of the event data
     * When image button is clicked, additional event information is revealed
     * @param view
     * @param position
     */

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
