package com.example.codenamebiscuit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.UserManager;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.helper.App;
import com.example.codenamebiscuit.helper.DownloadImage;
import com.example.codenamebiscuit.helper.DownloadProfilePic;
import com.example.codenamebiscuit.helper.GoogleApiHelper;
import com.example.codenamebiscuit.helper.RoundedImageView;
import com.example.codenamebiscuit.rv.ClickListener;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private static final int PROFILE_SETTING = 100000;




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

        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEventAdapter = new EventAdapter(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        setupRecyclerView();
        setupSwipeDownRefresh();

        swipeContainer.setRefreshing(true);
        loadEventData();
        checkIfFbOrGoogleLogin();
        setupNavDrawer(savedInstanceState);



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
    private void checkIfFbOrGoogleLogin() {
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
        }
        if (pref.getString("user_idG", null) != null && AccessToken.getCurrentAccessToken() == null) {
            try {
                currentUserId.put("user_id", pref.getString("user_idG", null));
                userId = pref.getString("user_idG", null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //onClick();
    }

    /**********************************************************************************************
     * Sets up navigation drawer with a list of activities
     * activities include: saved events, deleted events, and user preferences
     * sets current user profile information into the drawer
     **********************************************************************************************/
    private void setupNavDrawer(Bundle savedState) {
        final String pic = prefs.getString("user_image", null);
        final String fName = prefs.getString("fName", null);
        final String lName = prefs.getString("lName", null);
        final String email = prefs.getString("email", null);

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext())
                        .load(uri)
                        .placeholder(placeholder)
                        .fit()
                        .centerCrop()
                        .into(imageView);

            }
        });

        IProfile profile = new ProfileDrawerItem().withName(fName + " " + lName).withIcon(Uri.parse(pic)).withEmail(email).withIdentifier(100);

        //new DrawerBuilder().withActivity(this).build();
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(profile)
                .withActivity(this)
                .withSavedInstance(savedState)
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Home")
                .withIcon(R.drawable.ic_home_black_24dp).withIdentifier(100000);

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Full Screen").withIcon(R.drawable.ic_fullscreen_black_24dp).withIdentifier(2),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Saved Events").withIcon(R.drawable.ic_save_black_24dp).withIdentifier(3),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Deleted Events").withIcon(R.drawable.ic_delete_black_24dp).withIdentifier(4),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Preferences").withIcon(R.drawable.ic_settings_black_24dp).withIdentifier(5)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(MainActivity.this, MainActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {

                                intent = new Intent(MainActivity.this, SwipeEvents.class);
                                intent.putExtra("user_id", currentUserId + "");
                                ArrayList<String> data = new ArrayList<String>();
                                for (int i = 0; i < mEventAdapter.getObject().size(); i++) {
                                    data.add(mEventAdapter.getObject().get(i).toString());

                                }
                                JSONArray jsonArray = new JSONArray(mEventAdapter.getObject());
                                intent.putStringArrayListExtra("data", data);
                                intent.putExtra("jArray", jsonArray.toString());

                            } else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(MainActivity.this, ViewSavedEvents.class);
                                    intent.putExtra("user_id", userId);
                            } else if (drawerItem.getIdentifier() == 4) {
                                intent = new Intent(MainActivity.this, ViewDeletedEvents.class);
                                intent.putExtra("user_id", userId);

                            } else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(MainActivity.this, UserSettingsActivity.class);
                            }

                            if (intent != null) {
                                MainActivity.this.startActivity(intent);
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedState)
                .withShowDrawerOnFirstLaunch(true)
                .build();
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
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_page_menu, menu);
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
        if (layout.getVisibility() == View.GONE) {
            layout.setVisibility(View.VISIBLE);
        }
        else {
            layout.setVisibility(View.GONE);

        }
    }
}
