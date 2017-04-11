package com.example.codenamebiscuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.codenamebiscuit.eventfragments.GridMainEventsFrag;
import com.example.codenamebiscuit.eventfragments.SwipeEvents;

import com.example.codenamebiscuit.helper.ChangePreferences;
import com.example.codenamebiscuit.helper.CreateDrawer;
import com.example.codenamebiscuit.helper.GPSTracker;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.facebook.FacebookSdk;
import com.mikepenz.iconics.view.IconicsImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wunderlist.slidinglayer.SlidingLayer;
import com.wunderlist.slidinglayer.transformer.AlphaTransformer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {
    private JSONObject currentUserId = new JSONObject();
    private SharedPreferences pref;
    private Toolbar toolbar;
    private SlidingLayer mSlidingLayer;
    private TextView swipeText;
    private SlidingUpPanelLayout mLayout;
    private FancyButton musicFancyButton, sportsButton, foodButton;
    private FancyButton healthButton, outdoorButton, entertainmentButton;
    private FancyButton familyButton, retailButton, performingButton;
    private IconicsImageView downArrow;
    private JSONObject preferences, removed;
    private GridMainEventsFrag eventsFrag;
    private SwipeEvents swipeEvents;
    private boolean touched;
    CreateDrawer createDrawer;
    private ProgressBar progressBar;
    List<JSONObject> prefList;
    GPSTracker gps;
    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);
    }


    /**********************************************************************************************
     * onCreate
     * assigns a layout resource file to the current activity
     * toolbar with navigation drawer is assigned a view from the activity_main layout
     * DrawerBuider API allows items to be added programatically to the navigation drawer
     *
     **********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.launch_layout);
        progressBar = (ProgressBar)findViewById(R.id.progress_launch);
        gps = new GPSTracker(this);


        pref = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = new JSONObject(); removed = new JSONObject();

        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        FacebookSdk.sdkInitialize(getApplicationContext());
        checkIfFbOrGoogleLogin(savedInstanceState);

        swipeEvents = new SwipeEvents();
        eventsFrag = new GridMainEventsFrag();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.swing_up_left, R.anim.exit);
        transaction.replace(R.id.fragment_container, eventsFrag, "mainFrag");
        transaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    /**********************************************************************************
     * View binding
     **********************************************************************************/
    private void bindViews() {
        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
        swipeText = (TextView) findViewById(R.id.swipeText);
        mSlidingLayer.setLayerTransformer(new AlphaTransformer());
        mSlidingLayer.setVisibility(View.INVISIBLE);
        musicFancyButton = (FancyButton)findViewById(R.id.btn_music);
        sportsButton = (FancyButton)findViewById(R.id.btn_sports);
        foodButton = (FancyButton)findViewById(R.id.btn_food);
        outdoorButton=(FancyButton)findViewById(R.id.btn_outdoors);
        healthButton=(FancyButton)findViewById(R.id.btn_health);
        entertainmentButton=(FancyButton)findViewById(R.id.btn_entertainment);
        performingButton=(FancyButton)findViewById(R.id.btn_performing);
        retailButton=(FancyButton)findViewById(R.id.btn_retail);
        familyButton=(FancyButton)findViewById(R.id.btn_family);
        downArrow = (IconicsImageView)findViewById(R.id.down_arrow);
        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSlidingLayer.isOpened())
                    mSlidingLayer.closeLayer(true);
                else
                    mSlidingLayer.openLayer(true);
            }
        });
        setupSlidingLayer();

        List<FancyButton> btnList = new ArrayList();
        btnList.add(musicFancyButton);btnList.add(foodButton); btnList.add(sportsButton);
        btnList.add(outdoorButton); btnList.add(healthButton); btnList.add(familyButton);
        btnList.add(retailButton); btnList.add(performingButton); btnList.add(entertainmentButton);
        setupPreferences(btnList);
        mSlidingLayer.animate().alpha(1.0f);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mSlidingLayer.isOpened()) {
                    mSlidingLayer.closeLayer(true);
                    return true; }
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    /**********************************************************************************************
     * Check if logged in user is logged in through facebook or through google
     * to obtain the correct user id to pass to the database which in
     * turn will provide all events based on a preferences selected by a user id
     * if facebook id and google id return null, redirect to the login screen
     **********************************************************************************************/
    private void checkIfFbOrGoogleLogin(Bundle savedstate) {
        if (pref.getString("user_id", null) == null) {
            Log.i("user id status", "null");
            Intent intent = new Intent(MainActivity.this, ChooseLogin.class);
            startActivity(intent);
            finish();

        } else {
            try {
                currentUserId.put("user_id", pref.getString("user_id", null));
            } catch (JSONException e) {
                e.printStackTrace(); }
            loadDrawer(savedstate);
            bindViews();
        }
    }

    /*********************************************************************************
     Create Menu
     **********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sliding_panel, menu);
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (mLayout != null) {
            if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                item.setTitle("Toggle Preferences");
            } else {
                item.setTitle("Toggle Preferences"); }
        }
        return true; }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item =menu.findItem(R.id.action_grid_to_full).setVisible(true);
        if(eventsFrag.isVisible())
            item.setIcon(R.drawable.ic_fullscreen_white_48dp);
        else if(swipeEvents.isVisible())
            item.setIcon(R.drawable.ic_grid_on_white_48dp);
        return super.onPrepareOptionsMenu(menu); }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (item.getItemId()) {
            case R.id.action_anchor:
                mSlidingLayer.openLayer(true);
                break;
            case R.id.action_toggle:
                if(mSlidingLayer.getVisibility()==View.VISIBLE)
                    mSlidingLayer.setVisibility(View.INVISIBLE);
                else
                    mSlidingLayer.setVisibility(View.VISIBLE);
                break;
             case R.id.action_grid_to_full:
                if(eventsFrag.isAdded()){
                    fragmentTransaction.replace(R.id.fragment_container, swipeEvents, "swipe");
                    fragmentTransaction.setCustomAnimations(R.anim.swing_up_left, R.anim.slide_out_down);
                    fragmentTransaction.commit();

                }
                else if(swipeEvents.isAdded()){
                    fragmentTransaction.replace(R.id.fragment_container, eventsFrag, "eventsFrag");
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);
                    fragmentTransaction.commit();
                }
        }
        return super.onOptionsItemSelected(item); }


    private void loadDrawer(Bundle savedState) {
        final String pic = pref.getString("user_image", null);
        final String fName = pref.getString("fName", null);
        final String lName = pref.getString("lName", null);
        final String email = pref.getString("email", null);
        FragmentManager manager = getSupportFragmentManager();
        createDrawer = new CreateDrawer(fName, lName, pic, email, savedState, toolbar,
                getApplicationContext(), this, manager, getLat(), getLng());
        createDrawer.loadDrawer(); }


    private String getLat(){
        double currentLat=0.0;
        if(gps.canGetLocation()){
            currentLat=gps.getLatitude();
        }
        return currentLat+""; }

    private String getLng(){
        double currentLng=0.0;
        if(gps.canGetLocation()){
            currentLng=gps.getLongitude();
        }
        return currentLng+""; }

    public void setupPreferences(final List<FancyButton> btnList) {
        try {
            preferences.put("user_id", pref.getString("user_id", null));
            removed.put("user_id", pref.getString("user_id", null));
            prefList = new QueryEventList(getString(R.string.PULL_USER_PREFERENCES)).execute(currentUserId).get();
            if (prefList.size() == 0) {
                mSlidingLayer.setVisibility(View.VISIBLE);
                mSlidingLayer.openLayer(true);
            }
            for (int i = 0; i < prefList.size(); i++) {
                if (Integer.parseInt(prefList.get(i).getString("preference_id")) > 0) {
                    Log.i("preferences" + i, prefList.get(i).getString("preference_id"));
                    btnList.get(Integer.parseInt(prefList.get(i).getString("preference_id")) - 1).setBackgroundColor(getColor(R.color.livinPink));
                    btnList.get(Integer.parseInt(prefList.get(i).getString("preference_id")) - 1).setSelected(true);
                    preferences.put("pref_id" + (Integer.parseInt(prefList.get(i).getString("preference_id"))), 1);
                    Log.i("true/false: pref_id:" + (Integer.parseInt(prefList.get(i).getString("preference_id"))), "1"); }
            }

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace(); }

        for (int i = 0; i < btnList.size(); i++) {
            final int finalI = i;
            btnList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touched = true;
                    if (!btnList.get(finalI).isSelected()) {
                        btnList.get(finalI).setBackgroundColor(getColor(R.color.livinPink));
                        btnList.get(finalI).setSelected(true);
                        try {
                            preferences.put("pref_id" + (finalI + 1), 1);
                        } catch (JSONException e) {
                            e.printStackTrace(); }
                        new ChangePreferences().execute(preferences);
                    } else {
                        btnList.get(finalI).setBackgroundColor(getColor(R.color.translivinPink));
                        btnList.get(finalI).setSelected(false);
                        try {
                            preferences.put("pref_id" + (finalI + 1), 0);
                        } catch (JSONException e) {
                            e.printStackTrace(); }
                        new ChangePreferences().execute(preferences);} }
            });
        }
    }

    private void setupSlidingLayer(){
        downArrow.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_down_white_48dp));
        mSlidingLayer.setOnInteractListener(new SlidingLayer.OnInteractListener() {
            @Override
            public void onOpen() {
                downArrow.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_up_white_48dp)); }

            @Override
            public void onShowPreview() { }

            @Override
            public void onClose() {
                downArrow.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_down_white_48dp)); }

            @Override
            public void onOpened() {
                downArrow.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_up_white_48dp)); }

            @Override
            public void onPreviewShowed() {
                downArrow.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_down_white_48dp));
                refresh(); }

            @Override
            public void onClosed() {
                downArrow.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_down_white_48dp));
                refresh(); }
        });
    }

    private void refresh(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(touched) {
            if(eventsFrag.isAdded()) {
                fragmentTransaction.detach(eventsFrag); fragmentTransaction.attach(eventsFrag);
            }
            else
                fragmentTransaction.detach(swipeEvents); fragmentTransaction.attach(swipeEvents);
            fragmentTransaction.commit();
        }
        touched=false; }

}