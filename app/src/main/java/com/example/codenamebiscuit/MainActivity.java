package com.example.codenamebiscuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.codenamebiscuit.helper.VolleySingleton;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.facebook.FacebookSdk;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private JSONObject currentUserId = new JSONObject();
    private String userId;
    private ArrayList<JSONObject> eventData;
    private SharedPreferences pref;
    private Toolbar toolbar;
    private android.app.FragmentTransaction fragmentTransaction;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    MainEventsFrag frag;


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

        setContentView(R.layout.launch_layout);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView tv = (TextView)findViewById(R.id.toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Black.ttf");
        tv.setTypeface(typeface);
        FacebookSdk.sdkInitialize(getApplicationContext());

        //verify that user is logged in either through fb or google
        //if not looged in, redirect to chooseLogin activity
        Log.i("activity started: ", "main activity");

        checkIfFbOrGoogleLogin(savedInstanceState);
        // Normal app init code...

        //loads the navigation drawer and adds fragments
        if(savedInstanceState==null) {
            MainEventsFrag eventsFrag = new MainEventsFrag();
            fragmentTransaction = getFragmentManager().beginTransaction();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, eventsFrag, "mainFrag").commit();
            getSupportFragmentManager().popBackStack();
        }
        RequestQueue queue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
    }

    private void loadDrawer(Bundle savedState){
        final String pic = pref.getString("user_image", null);
        final String fName = pref.getString("fName", null);
        final String lName = pref.getString("lName", null);
        final String email = pref.getString("email", null);


        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder)
                        .fit().centerCrop().into(imageView);}
        });

        IProfile profile = new ProfileDrawerItem().withName(fName + " " + lName).withIcon(Uri.parse(pic)).withEmail(email).withIdentifier(100);

        headerResult = new AccountHeaderBuilder().withActivity(this).withTranslucentStatusBar(true).withHeaderBackground(R.drawable.header)
                .addProfiles(profile).withActivity(this).withSavedInstance(savedState).build();

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder().withActivity(this).withToolbar(toolbar)
                .withAccountHeader(headerResult).addDrawerItems(
                        new SecondaryDrawerItem().withName("Home").withIcon(R.drawable.ic_home_black_24dp).withIdentifier(1),
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
                        Fragment fragment = null;
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        if (drawerItem != null) {
                            Intent intent = null;

                            if (drawerItem.getIdentifier() == 1) {
                                fragment = new MainEventsFrag();

                            } else if (drawerItem.getIdentifier() == 2) {
                                //MainEventsFrag main = (MainEventsFrag)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                intent = new Intent(MainActivity.this, SwipeEvents.class);
                                //intent.putExtra("jArray", main.getEventList().toString());

                            } else if (drawerItem.getIdentifier() == 3) {
                                fragment = new SavedEventsFrag();
                            } else if (drawerItem.getIdentifier() == 4) {
                                fragment = new DeletedEventsFrag();

                            } else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(getApplicationContext(), UserSettingsActivity.class);
                            }

                            if (intent != null)
                                startActivity(intent);

                            if(fragment!=null)
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                                        .commit();

                        }
                        return false;
                    }
                })
                .withSavedInstance(savedState).withShowDrawerOnFirstLaunch(true).build();}

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();

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


        if (pref.getString("user_id", null)==null){
            Log.i("user id status", "null");
            Intent intent = new Intent(MainActivity.this, ChooseLogin.class);
            startActivity(intent);
            finish();

        }else{
            try {
                currentUserId.put("user_id", pref.getString("user_id", null));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadDrawer(savedstate);

        }
    }
    /**********************************************************************************************
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
}
