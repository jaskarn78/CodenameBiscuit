package com.example.codenamebiscuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.facebook.FacebookSdk;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.model.LatLng;
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
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    private JSONObject currentUserId = new JSONObject();
    private SharedPreferences pref;
    private Toolbar toolbar;
    private android.app.FragmentTransaction fragmentTransaction;
    private LocationManager mLocationManager;
    //save our header or result
    private AccountHeader headerResult = null;
    private LatLng latLng;
    private Location location;
    private Drawer result = null;
    private Bundle bundle;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.launch_layout);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        bundle = new Bundle();
        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);


        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Black.ttf");
        tv.setTypeface(typeface);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.i("activity started: ", "main activity");

        //verify that user is logged in either through fb or google
        //if not looged in, redirect to chooseLogin activity
        checkIfFbOrGoogleLogin(savedInstanceState);

        MainEventsFrag eventsFrag = new MainEventsFrag();

        // Normal app init code..
        if (savedInstanceState == null) {
            fragmentTransaction = getFragmentManager().beginTransaction();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, eventsFrag, "mainFrag").commit();
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // if the screen is in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

    }



    /**********************************************************************************************
     * Obtains the users current location via GPS location services
     * Location is converted into Lat and Lng coordinates
     * Coordinates are then passed into a Geocoder to retrieve the current address
     **********************************************************************************************/

    private void getLocation(){
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1, mLocationListener);
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());


            Geocoder geocoder;
            List<android.location.Address> addresses = new ArrayList<>();
            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StyleableToast st = new StyleableToast(getApplicationContext(),
                    addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getLocality() + ", "
                            + addresses.get(0).getAdminArea() + " " + addresses.get(0).getPostalCode(), Toast.LENGTH_SHORT);
            st.spinIcon();
            st.setBackgroundColor(Color.BLUE);
            st.setTextColor(Color.WHITE);
            st.show();
    }


    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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
                e.printStackTrace();
            }
            loadDrawer(savedstate);

        }
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


        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder)
                        .fit().centerCrop().into(imageView);
            }
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
                        new SecondaryDrawerItem().withName("Preferences").withIcon(R.drawable.ic_settings_black_24dp).withIdentifier(5),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Grid Events").withIcon(R.drawable.ic_grid_on_black_24dp).withIdentifier(6),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Current coordinates").withIcon(R.drawable.ic_gps_fixed_black_24dp).withIdentifier(7)
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
                            } else if(drawerItem.getIdentifier()==6){
                                fragment = new GridMainEventsFrag();
                            } else if(drawerItem.getIdentifier()==7){
                                getLocation();
                            }

                            if (intent != null)
                                startActivity(intent);

                            if (fragment != null)
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                                        .commit();

                        }
                        return false;
                    }
                })
                .withSavedInstance(savedState).withShowDrawerOnFirstLaunch(true).build();
    }
}
