package com.example.codenamebiscuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.helper.SetupDrawer;
import com.example.codenamebiscuit.rv.ClickListener;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ClickListener{
    private RecyclerView mRecyclerView;
    private EventAdapter mEventAdapter;
    private JSONObject currentUserId = new JSONObject();
    private String userId;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<JSONObject> eventData;
    private SharedPreferences pref;
    private Toolbar toolbar;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private SharedPreferences prefs;
    private int SPLASH_TIME_OUT;




    /**********************************************************************************************
     * onCreate
     * assigns a layout resource file to the current activity
     * toolbar with navigation drawer is assigned a view from the activity_main layout
     * DrawerBuider API allows items to be added programatically to the navigation drawer
     *
     **********************************************************************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);


        TextView tv = (TextView)findViewById(R.id.toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Black.ttf");
        tv.setTypeface(typeface);

        mEventAdapter = new EventAdapter(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        setupRecyclerView();
        setupSwipeDownRefresh();

        swipeContainer.setRefreshing(true);
        checkIfFbOrGoogleLogin(savedInstanceState);
        //setupNavDrawer(savedInstanceState);


        //st.show();
        loadEventData();

        /** Custom stylable toast**/
        StyleableToast st = new StyleableToast(getApplicationContext(), "Loading Events...Please Wait", Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#ff9dfc"));
        st.setTextColor(Color.WHITE);
        st.setIcon(R.drawable.ic_autorenew_white_24dp);
        st.spinIcon();
        st.setMaxAlpha();

    }

    private void loadDrawer(Bundle savedState){
        final String pic = prefs.getString("user_image", null);
        final String fName = prefs.getString("fName", null);
        final String lName = prefs.getString("lName", null);
        final String email = prefs.getString("email", null);
        SetupDrawer setup = new SetupDrawer(headerResult, result, toolbar,
                currentUserId, mEventAdapter, userId, getApplicationContext(), fName, lName, email, pic);
        setup.setupNavDrawer(savedState, this);
    }


    /**********************************************************************************************
     * When activity resumes after a pause, check to see if any new events have been added
     * set swipeContainer.setRefreshing to true
     * load the event data
     * set swipecontainer.setRefreshing to false
     **********************************************************************************************/
    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        swipeContainer.setRefreshing(true);
        loadEventData();
        swipeContainer.setRefreshing(false);

    }

    @Override
    public void onStart() {
        super.onStart();
        swipeContainer.setRefreshing(true);
        loadEventData();
        swipeContainer.setRefreshing(false);
    }
    @Override
    public void onPause(){
        super.onPause();
    }


    /**********************************************************************************************
     * Check if logged in user is logged in through facebook or through google
     * to obtain the correct user id to pass to the database which in
     * turn will provide all events based on a preferences selected by a user id
     * if facebook id and google id return null, redirect to the login screen
     **********************************************************************************************/
    private void checkIfFbOrGoogleLogin(Bundle savedstate) {
        if (AccessToken.getCurrentAccessToken() == null && pref.getString("user_idG", null) == null) {
            Intent intent = new Intent(MainActivity.this, ChooseLogin.class);
            startActivity(intent);
        }

        if (AccessToken.getCurrentAccessToken() != null) {
            try {
                currentUserId.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
                userId = AccessToken.getCurrentAccessToken().getUserId();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadDrawer(savedstate);
        }
        if (pref.getString("user_idG", null) != null && AccessToken.getCurrentAccessToken() == null) {
            try {
                currentUserId.put("user_id", pref.getString("user_idG", null));
                userId = pref.getString("user_idG", null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadDrawer(savedstate);
        }
        //onClick();
    }



    /**********************************************************************************************
     * sets up recycler view and assigns layout
     * assigns mEventAdapter which contains all event information retrieved from MySQL request
     * recycler view is assigned a linear layout
     **********************************************************************************************/
    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_events);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mRecyclerView.setAdapter(mEventAdapter);
        mEventAdapter.setClickListener(this);
    }


    /**********************************************************************************************
     * setup up swipe container to refresh event list when performing down swipe gesture
     * when swipeContainer.setRefresh(true) load the event data
     * after data has been loaded, set swipeContainer.setRefresh(false)
     **********************************************************************************************/
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


    /**********************************************************************************************
     * HTTP request to run python script which contains sql command
     * to retrieve all event data filtered by user id
     **********************************************************************************************/
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
       // MenuInflater menuInflater = getMenuInflater();
       // menuInflater.inflate(R.menu.main_page_menu, menu);
        return true;
    }


    /**********************************************************************************************
     * Verify which item in the options menu was clicked and begin appropriate activity
     * @param item
     * @return
     **********************************************************************************************/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**********************************************************************************************
     * Handles the drop down functionality in the list view of the event data
     * When image button is clicked, additional event information is revealed
     * @param view
     * @param position
     **********************************************************************************************/


    @Override
    public void itemClicked(View view, int position) {
        RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.extend);
        if (layout.getVisibility() == View.GONE)
            layout.setVisibility(View.VISIBLE);
        else
            layout.setVisibility(View.GONE);
    }
}
