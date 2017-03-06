package com.example.codenamebiscuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.codenamebiscuit.eventfragments.DeletedEventsFrag;
import com.example.codenamebiscuit.eventfragments.GridMainEventsFrag;
import com.example.codenamebiscuit.eventfragments.MainEventsFrag;
import com.example.codenamebiscuit.eventfragments.SavedEventsFrag;
import com.example.codenamebiscuit.eventfragments.SwipeEvents;

import com.example.codenamebiscuit.helper.CreateDrawer;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.facebook.FacebookSdk;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements View.OnLongClickListener, MainEventsFrag.GetDataInterface, DeletedEventsFrag.GetDeletedEventsInterface,
        GridMainEventsFrag.GetMainDataInterface, SavedEventsFrag.GetSavedDataInterface, SwipeEvents.GetMainSwipeDataInterface{
    private JSONObject currentUserId = new JSONObject();
    private SharedPreferences pref;
    private Toolbar toolbar;
    private ArrayList<JSONObject> data, deletedData, savedData;
    private static final int RC_LOCATION_SERVICE=123;


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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.launch_layout);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            String perms[] = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            EasyPermissions.requestPermissions(MainActivity.this, "This app requires location services", RC_LOCATION_SERVICE, perms); }

        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Black.ttf");
        tv.setTypeface(typeface);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.i("activity started: ", "main activity");

        /**verify that user is logged in either through fb or google
        //if not looged in, redirect to chooseLogin activity*/
        checkIfFbOrGoogleLogin(savedInstanceState);

       // MainEventsFrag eventsFrag = new MainEventsFrag();
        GridMainEventsFrag eventsFrag = new GridMainEventsFrag();
        // Normal app init code..
        if (savedInstanceState == null) {
            android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, eventsFrag, "mainFrag").commit();}

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return;}
    }


    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        //loadData();

    }
    @Override
    public void onPause(){
        super.onPause();
        //loadData();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        //loadData();
    }

    @Override
    public void onStart() {
        super.onStart();
        //loadData();

        //loadData();
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
            loadDrawer(savedstate); }
    }

   /*********************************************************************************
     Create Menu
     **********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onLongClick(View v) {
        Log.i("view long clicked", v.getHeight()+"");

        return true;
    }

    private void loadDrawer(Bundle savedState) {
        final String pic = pref.getString("user_image", null);
        final String fName = pref.getString("fName", null);
        final String lName = pref.getString("lName", null);
        final String email = pref.getString("email", null);
        FragmentManager manager = getSupportFragmentManager();
        CreateDrawer createDrawer = new CreateDrawer(fName, lName, pic, email, savedState, toolbar, getApplicationContext(), this, manager);
        createDrawer.loadDrawer();


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);}

    public void loadData(){
        try {
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), this).execute(currentUserId).get();
            savedData = new QueryEventList(getString(R.string.DATABASE_SAVED_EVENTS_PULLER), this).execute(currentUserId).get();
            deletedData = new QueryEventList(getString(R.string.DATABASE_DELETED_EVENTS_PULLER), this).execute(currentUserId).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<JSONObject> getDeletedEventList() {
        return deletedData; }

    @Override
    public ArrayList<JSONObject> getUpdatedDeletedEventList() {
        try {
            deletedData= new QueryEventList(getString(R.string.DATABASE_DELETED_EVENTS_PULLER), this).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace(); }
        return deletedData; }

    @Override
    public ArrayList<JSONObject> getMainEventList() {
        return data; }

    @Override
    public ArrayList<JSONObject> getUpdatedEventList() {
        try {
            data= new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), this).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return data; }

    @Override
    public ArrayList<JSONObject> getSavedEventList() {
        return savedData; }

    @Override
    public ArrayList<JSONObject> getUpdatedSavedEventList() {
        try {
            savedData= new QueryEventList(getString(R.string.DATABASE_SAVED_EVENTS_PULLER), this).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return savedData; }

}
