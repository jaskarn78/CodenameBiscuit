package com.example.codenamebiscuit;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.example.codenamebiscuit.eventfragments.GridMainEventsFrag;
import com.example.codenamebiscuit.eventfragments.SwipeEvents;
import com.example.codenamebiscuit.helper.CreateDrawer;
import com.example.codenamebiscuit.helper.GPSTracker;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.facebook.FacebookSdk;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;
import com.jaredrummler.materialspinner.MaterialSpinner;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import br.com.mauker.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity {
    private JSONObject currentUserId = new JSONObject();
    private SharedPreferences pref;
    private Toolbar toolbar;
    private GridMainEventsFrag eventsFrag;
    private SwipeEvents swipeEvents;
    private FrameLayout revealFrame;
    private boolean touched;
    CreateDrawer createDrawer;
    FABRevealMenu fabMenu;
    private MaterialSpinner toolbarSpinner;
    private MaterialSearchView searchView;
    private File file;
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

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.launch_layout);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        gps = new GPSTracker(this);
        searchView = (MaterialSearchView)findViewById(R.id.search_view);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        fabMenu = (FABRevealMenu)findViewById(R.id.reveal);
        toolbarSpinner = (MaterialSpinner) findViewById(R.id.spinner);
        revealFrame = (FrameLayout)findViewById(R.id.revealFrame);
        setupSpinner(); FacebookSdk.sdkInitialize(getApplicationContext());
        checkIfFbOrGoogleLogin(savedInstanceState);
        if(eventsFrag!=null && eventsFrag.getData()!=null)
            datasize = eventsFrag.getData().size();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("resumed", getIntent().getBooleanExtra("touched", false)+"");
        if(eventsFrag!=null) {
            if(eventsFrag.fancyViewShowing())
                eventsFrag.onBackPressed();
            touched = true; refresh();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(eventsFrag!=null && eventsFrag.fancyViewShowing())
            eventsFrag.onBackPressed();
        else if(!swipeEvents.isVisible() && !fabMenu.isShowing() && eventsFrag!=null) {
            new FancyAlertDialog.Builder(this).setActivity(this)
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
        }else if(swipeEvents.isVisible() && !fabMenu.isShowing()){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);
            ft.addToBackStack("swipeEvents");
            ft.replace(R.id.fragment_container, eventsFrag, "eventsFrag");
            ft.commit(); }
        else if(eventsFrag!=null && eventsFrag.getTouchedValue()){
            if(fabMenu.isShowing())
                fabMenu.closeMenu();
            touched=true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    refresh();} }, 800);
        }else if(eventsFrag!=null && eventsFrag.fancyViewShowing())
            eventsFrag.onBackPressed();
        else
            fabMenu.closeMenu();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event); }

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

            if(file!=null){
                Log.i("File path********", file.getPath());
            }

            bundle.putString("currentUserId", userId);

            swipeEvents = new SwipeEvents();
            swipeEvents.setArguments(bundle);

            eventsFrag = new GridMainEventsFrag();
            eventsFrag.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
            transaction.replace(R.id.fragment_container, eventsFrag, "eventsFrag");
            transaction.commit();} }



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
        MenuItem search = menu.findItem(R.id.search_action);
        TextView textView = (TextView)findViewById(R.id.toolbar_title);
        if(eventsFrag!=null && swipeEvents!=null) {
            if (eventsFrag.isAdded()) {
                textView.setVisibility(View.GONE);
                search.setVisible(true);
                revealFrame.setVisibility(View.VISIBLE);
                toolbarSpinner.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.ic_fullscreen_white_48dp);}
            else if (swipeEvents.isAdded()) {
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
        } return super.onOptionsItemSelected(item); }



    private void refresh(){
        if(touched) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(fragment); ft.attach(fragment); ft.commitNow();
        }touched=false; }

    public void setupSpinner() {
        toolbarSpinner.setItems("Nearest", "Furthest", "Earliest", "Latest", "Top Rated"); }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false); } }
            return; }

        super.onActivityResult(requestCode, resultCode, data);
    }

}