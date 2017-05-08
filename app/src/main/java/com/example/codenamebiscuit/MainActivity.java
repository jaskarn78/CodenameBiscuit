package com.example.codenamebiscuit;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.codenamebiscuit.eventfragments.GridMainEventsFrag;
import com.example.codenamebiscuit.eventfragments.SwipeEvents;

import com.example.codenamebiscuit.requests.RunQuery;
import com.example.codenamebiscuit.helper.CreateDrawer;
import com.example.codenamebiscuit.helper.GPSTracker;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.facebook.FacebookSdk;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.hlab.fabrevealmenu.view.FABRevealMenu;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikepenz.iconics.view.IconicsImageView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.com.mauker.materialsearchview.MaterialSearchView;
import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {
    private JSONObject currentUserId = new JSONObject();
    private SharedPreferences pref;
    private Toolbar toolbar;
    private JSONObject preferences, removed;
    private GridMainEventsFrag eventsFrag;
    private SwipeEvents swipeEvents;
    private FrameLayout revealFrame;
    private boolean touched;
    CreateDrawer createDrawer;
    List<JSONObject> prefList;
    FABRevealMenu fabMenu;
    private View fabMenuView;
    private MaterialSpinner toolbarSpinner;
    private MaterialSearchView searchView;
    private int datasize;

    Bundle bundle = new Bundle();
    private String userId;

    GPSTracker gps;



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
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        gps = new GPSTracker(this);
        searchView = (MaterialSearchView)findViewById(R.id.search_view);


        pref = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = new JSONObject(); removed = new JSONObject();

        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarSpinner = (MaterialSpinner) findViewById(R.id.spinner);
        revealFrame = (FrameLayout)findViewById(R.id.revealFrame);
        setupSpinner();

        FacebookSdk.sdkInitialize(getApplicationContext());
        final FloatingActionButton fabReveal = (FloatingActionButton)findViewById(R.id.fab_reveal);
        fabMenu = (FABRevealMenu) findViewById(R.id.reveal);
            if(fabReveal!=null && fabMenu!=null){
                View customView = View.inflate(this, R.layout.preferences_layout,null);
                fabMenuView = customView;
                fabMenu.setCustomView(customView);
                fabMenu.bindAncherView(fabReveal); }

        checkIfFbOrGoogleLogin(savedInstanceState);
        if(eventsFrag!=null & eventsFrag.isAdded())
            datasize = eventsFrag.getData().size();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(eventsFrag!=null && datasize<eventsFrag.getData().size()) {
            touched = true;
            refresh();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(!swipeEvents.isVisible() && !fabMenu.isShowing()) {
            new FancyAlertDialog.Builder(this).setActivity(this)
                    .setTextTitle("Exit")
                    .setPositiveColor(R.color.livinPink).setNegativeColor(R.color.black)
                    .setPositiveButtonText("Exit").setTextSubTitle("Are you sure you would like to exit?")
                    .setNegativeButtonText("Cancel")
                    .setSubtitleColor(R.color.black).setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                @Override
                public void OnClick(View view, Dialog dialog) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                @Override
                public void OnClick(View view, Dialog dialog) {
                    dialog.dismiss();
                }
            }).build().show();

        }else if(swipeEvents.isVisible()){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);
            ft.addToBackStack("swipeEvents");
            ft.replace(R.id.fragment_container, eventsFrag, "eventsFrag");
            ft.commit(); }

        else fabMenu.closeMenu();
    }

    /**********************************************************************************
     * View binding
     **********************************************************************************/
    private void bindViews() {
        IconicsImageView closeButton = (IconicsImageView) findViewById(R.id.exit_icon);

        FancyButton musicFancyButton = (FancyButton) fabMenuView.findViewById(R.id.btn_music);
        FancyButton sportsButton = (FancyButton) fabMenuView.findViewById(R.id.btn_sports);
        FancyButton foodButton = (FancyButton) fabMenuView.findViewById(R.id.btn_food);
        FancyButton outdoorButton = (FancyButton) fabMenuView.findViewById(R.id.btn_outdoors);
        FancyButton healthButton = (FancyButton) fabMenuView.findViewById(R.id.btn_health);
        FancyButton entertainmentButton = (FancyButton) fabMenuView.findViewById(R.id.btn_entertainment);
        FancyButton charityButton = (FancyButton) fabMenuView.findViewById(R.id.btn_charity);
        FancyButton retailButton = (FancyButton) fabMenuView.findViewById(R.id.btn_retail);
        FancyButton familyButton = (FancyButton) fabMenuView.findViewById(R.id.btn_family);

        List<FancyButton> btnList = new ArrayList();
        btnList.add(musicFancyButton);btnList.add(foodButton); btnList.add(sportsButton);
        btnList.add(outdoorButton); btnList.add(healthButton); btnList.add(familyButton);
        btnList.add(retailButton); btnList.add(charityButton); btnList.add(entertainmentButton);
        setupPreferences(btnList);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                fabMenu.closeMenu();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        refresh();} }, 800);} });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);

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
        if (pref.getString("user_id", null)==null) {
            Intent intent = new Intent(MainActivity.this, ChooseLogin.class);
            startActivity(intent);
        } else {
            try {
                userId=pref.getString("user_id", null);
                currentUserId.put("user_id", userId);
            } catch (JSONException e) { e.printStackTrace(); }

            createDrawer = new CreateDrawer(savedstate, toolbar, this, userId, getSupportFragmentManager());
            createDrawer.loadDrawer();

            bindViews();
            bundle.putString("currentUserId", userId);

            swipeEvents = new SwipeEvents();
            swipeEvents.setArguments(bundle);

            eventsFrag = new GridMainEventsFrag();
            eventsFrag.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
            transaction.replace(R.id.fragment_container, eventsFrag, "eventsFrag");
            transaction.commit();

        }
    }



    /*********************************************************************************
     Create Menu
     **********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sliding_panel, menu);
        menu.findItem(R.id.search_action).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(!searchView.isOpen())
                    searchView.openSearch();
                else searchView.closeSearch();
                return false; } });
        return true; }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item =menu.findItem(R.id.action_grid_to_full).setVisible(true);
        //MenuItem refresh = menu.findItem(R.id.refresh);
        MenuItem search = menu.findItem(R.id.search_action);
        TextView textView = (TextView)findViewById(R.id.toolbar_title);
        if(eventsFrag!=null && swipeEvents!=null) {
            if (eventsFrag.isAdded()) {
                textView.setVisibility(View.GONE);
                //refresh.setVisible(true);
                search.setVisible(true);
                revealFrame.setVisibility(View.VISIBLE);
                toolbarSpinner.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.ic_fullscreen_white_48dp);}
            else if (swipeEvents.isAdded()) {
                //refresh.setVisible(false);
                textView.setVisibility(View.VISIBLE);
                toolbarSpinner.setVisibility(View.GONE);
                revealFrame.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_grid_on_white_48dp);} }
        return super.onPrepareOptionsMenu(menu); }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);
        switch (item.getItemId()) {
             case R.id.action_grid_to_full:
                if(eventsFrag.isAdded()){
                    fragmentTransaction.replace(R.id.fragment_container, swipeEvents, "swipeFrag");
                    fragmentTransaction.commitNowAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();}

                else if(swipeEvents.isAdded()){
                    fragmentTransaction.replace(R.id.fragment_container, eventsFrag, "eventsFrag");
                    fragmentTransaction.commitNowAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();}
        }
        return super.onOptionsItemSelected(item); }


    public void setupPreferences(final List<FancyButton> btnList) {
        try {
            preferences.put("user_id", userId);
            removed.put("user_id", userId);
            prefList = new QueryEventList(getString(R.string.PULL_USER_PREFERENCES), userId).execute().get();

            for (int i = 0; i < prefList.size(); i++) {
                if (Integer.parseInt(prefList.get(i).getString("preference_id")) > 0) {
                    btnList.get(Integer.parseInt(prefList.get(i).getString("preference_id")) - 1).setBackgroundColor(getColor(R.color.livinPink));
                    btnList.get(Integer.parseInt(prefList.get(i).getString("preference_id")) - 1).setSelected(true);
                    preferences.put("pref_id" + (Integer.parseInt(prefList.get(i).getString("preference_id"))), 1);} }

        } catch (JSONException | InterruptedException | ExecutionException e) {e.printStackTrace(); }
        for (int i = 0; i < btnList.size(); i++) {
            final int finalI = i;
            btnList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touched = true;
                    if (!btnList.get(finalI).isSelected()) {
                        btnList.get(finalI).setBackgroundColor(getColor(R.color.livinPink));
                        btnList.get(finalI).setSelected(true);
                        try { preferences.put("pref_id" + (finalI + 1), 1); }
                        catch (JSONException e) { e.printStackTrace(); }
                        new RunQuery(getString(R.string.PUSH_USER_PREFERENCES)).execute(preferences);}
                    else {
                        btnList.get(finalI).setBackgroundColor(getColor(R.color.transparentPink));
                        btnList.get(finalI).setSelected(false);
                        try { preferences.put("pref_id" + (finalI + 1), 0);
                        } catch (JSONException e) { e.printStackTrace(); }
                        new RunQuery(getString(R.string.PUSH_USER_PREFERENCES)).execute(preferences);} } }); }

    }

    private void refresh(){
        if(touched) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(fragment); ft.attach(fragment); ft.commitNow();
        }touched=false;
    }

    public void setupSpinner() {
        toolbarSpinner.setItems("Nearest", "Furthest", "Earliest", "Latest");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false); } }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}