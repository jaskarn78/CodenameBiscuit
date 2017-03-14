package com.example.codenamebiscuit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.eventfragments.DeletedEventsFrag;
import com.example.codenamebiscuit.eventfragments.GridMainEventsFrag;
import com.example.codenamebiscuit.eventfragments.MainEventsFrag;
import com.example.codenamebiscuit.eventfragments.SavedEventsFrag;
import com.example.codenamebiscuit.eventfragments.SwipeEvents;

import com.example.codenamebiscuit.helper.ChangePreferences;
import com.example.codenamebiscuit.helper.CreateDrawer;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.facebook.FacebookSdk;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wunderlist.slidinglayer.LayerTransformer;
import com.wunderlist.slidinglayer.SlidingLayer;
import com.wunderlist.slidinglayer.transformer.AlphaTransformer;
import com.wunderlist.slidinglayer.transformer.RotationTransformer;
import com.wunderlist.slidinglayer.transformer.SlideJoyTransformer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mehdi.sakout.fancybuttons.FancyButton;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements View.OnLongClickListener, MainEventsFrag.GetDataInterface, DeletedEventsFrag.GetDeletedEventsInterface,
        GridMainEventsFrag.GetMainDataInterface, SavedEventsFrag.GetSavedDataInterface, SwipeEvents.GetMainSwipeDataInterface{
    private JSONObject currentUserId = new JSONObject();
    private SharedPreferences pref;
    private Toolbar toolbar;
    private ArrayList<JSONObject> data, deletedData, savedData;
    private static final int RC_LOCATION_SERVICE=123;
    private static final int RC_STORAGE_SERVICE=124;
    private SlidingLayer mSlidingLayer;
    private TextView swipeText;
    private SlidingUpPanelLayout mLayout;
    private FancyButton musicFancyButton, sportsButton, foodButton;
    private FancyButton healthButton, outdoorButton, entertainmentButton;
    private FancyButton familyButton, retailButton, performingButton;
    private JSONObject preferences = new JSONObject();
    private boolean isActive = false;
    List<JSONObject> prefList;
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



        pref = PreferenceManager.getDefaultSharedPreferences(this);

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            String perms[] = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            EasyPermissions.requestPermissions(MainActivity.this, "This app requires location services", RC_LOCATION_SERVICE, perms); }


        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);


        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Black.ttf");
        tv.setTypeface(typeface);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.i("activity started: ", "main activity");

        /**verify that user is logged in either through fb or google
        //if not looged in, redirect to chooseLogin activity*/
        checkIfFbOrGoogleLogin(savedInstanceState);
        bindViews();

        swipeText.setTypeface(typeface);

        GridMainEventsFrag eventsFrag = new GridMainEventsFrag();
        // Normal app init code..
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.swing_up_left, R.anim.exit);
            ft.add(R.id.fragment_container, eventsFrag, "mainFrag");
            ft.commit();
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return;}
    }


    @Override
    protected void onResume() {
        super.onResume(); }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    /**
     * View binding
     */
    private void bindViews() {
        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
        swipeText = (TextView) findViewById(R.id.swipeText);
        mSlidingLayer.setLayerTransformer(new AlphaTransformer());
        musicFancyButton = (FancyButton)findViewById(R.id.btn_music);
        sportsButton = (FancyButton)findViewById(R.id.btn_sports);
        foodButton = (FancyButton)findViewById(R.id.btn_food);
        outdoorButton=(FancyButton)findViewById(R.id.btn_outdoors);
        healthButton=(FancyButton)findViewById(R.id.btn_health);
        entertainmentButton=(FancyButton)findViewById(R.id.btn_entertainment);
        performingButton=(FancyButton)findViewById(R.id.btn_performing);
        retailButton=(FancyButton)findViewById(R.id.btn_retail);
        familyButton=(FancyButton)findViewById(R.id.btn_family);
        final List<FancyButton> btnList = new ArrayList();
        btnList.add(musicFancyButton);btnList.add(foodButton); btnList.add(sportsButton);
        btnList.add(outdoorButton); btnList.add(healthButton); btnList.add(familyButton);
        btnList.add(retailButton); btnList.add(performingButton); btnList.add(entertainmentButton);
        try {
            preferences.put("user_id", pref.getString("user_id", null));
            prefList = new QueryEventList(getString(R.string.PULL_USER_PREFERENCES)).execute(currentUserId).get();
            for(int i=0; i<prefList.size(); i++) {
                Log.i("preferences" + i, prefList.get(i).getString("preference_id"));
                if(Integer.parseInt(prefList.get(i).getString("preference_id"))>0) {
                    btnList.get(i).setBackgroundColor(getColor(R.color.livinPink));
                    btnList.get(i).setSelected(true);

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        for(int i=0; i<btnList.size(); i++){
            final int finalI = i;
            //btnList.get(i).setSelected(false);
            btnList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!btnList.get(finalI).isSelected()){
                        btnList.get(finalI).setBackgroundColor(getColor(R.color.livinPink));
                        btnList.get(finalI).setSelected(true);
                        try {
                            preferences.put("pref_id"+(finalI+1), 1);
                            Log.i("pref_id"+(finalI), preferences.getString("pref_id"+(finalI+1)));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        btnList.get(finalI).setBackgroundColor(getColor(R.color.translivinPink));
                        btnList.get(finalI).setSelected(false);
                        try {
                            preferences.put("pref_id"+(finalI+1), 0);
                            Log.i("pref_id"+(finalI), preferences.getString("pref_id"+(finalI+1)));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    new ChangePreferences().execute(preferences); } });
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mSlidingLayer.isOpened()) {
                    mSlidingLayer.closeLayer(true);
                    return true;
                }

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
       // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.sliding_panel, menu);
       MenuItem item = menu.findItem(R.id.action_toggle);
       if (mLayout != null) {
           if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
               item.setTitle("Toggle Preferences");
           } else {
               item.setTitle("Toggle Preferences");
           }
       }
       return true;
   }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_anchor:
                mSlidingLayer.openLayer(true);
                break;
            case R.id.action_toggle:
               if(mSlidingLayer.getVisibility()==View.VISIBLE)
                   mSlidingLayer.setVisibility(View.GONE);
                else
                    mSlidingLayer.setVisibility(View.VISIBLE);

        }
        return super.onOptionsItemSelected(item);
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

        EmbeddedFragment embeddedFragment = new EmbeddedFragment(getApplicationContext(), this, savedState);
        embeddedFragment.setupSideDrawer();


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
