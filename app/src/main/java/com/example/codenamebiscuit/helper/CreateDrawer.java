package com.example.codenamebiscuit.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.ArchivedEvents;
import com.example.codenamebiscuit.EmbeddedFragment;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.eventfragments.DeletedEventsFrag;
import com.example.codenamebiscuit.eventfragments.GridMainEventsFrag;
import com.example.codenamebiscuit.eventfragments.SavedEventsFrag;
import com.example.codenamebiscuit.eventfragments.SwipeEvents;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.example.codenamebiscuit.settings.UserSettingsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialize.Materialize;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


/**
 * Created by jaskarnjagpal on 3/3/17.
 */

public class CreateDrawer {
    private String fName, lName, pic, email;
    private Bundle savedState;
    private Toolbar toolbar;
    private Context context;
    private Activity activity;
    Drawer result=null;
    AccountHeader headerResult=null;
    private FragmentManager fragmentManager;
    private int livinPink, overlay;
    private int livinBlack, livinWhite;
    private int numOfSavedEvents, numOfDeletedEvents, totalNumEvents;
    private String currentLat, currentLng;
    private PrimaryDrawerItem gridEvents, fullScreen,archivedEvents,account,logOut;
    GPSTracker gps;
    LatLng latLng;
    SharedPreferences preferences;


    public CreateDrawer(String fName, String lName, String pic, String email, Bundle savedState,
                        Toolbar toolbar, Context context, Activity activity,
                        FragmentManager fragmentManager, String currentLat, String currentLng) {

        this.fName=fName; this.lName=lName;
        this.pic=pic; this.email=email;
        this.savedState=savedState; this.toolbar=toolbar;
        this.context=context;
        this.activity=activity;
        this.fragmentManager=fragmentManager;
        this.currentLat=currentLat;
        this.currentLng=currentLng;

        livinPink=context.getColor(R.color.livinPink);
        overlay=context.getColor(R.color.black_overlay);
        livinBlack=context.getColor(R.color.livinBlack);
        livinWhite=context.getColor(R.color.livinWhite);
        preferences=PreferenceManager.getDefaultSharedPreferences(context);
        gridEvents = new PrimaryDrawerItem();
        fullScreen = new PrimaryDrawerItem();
        archivedEvents = new PrimaryDrawerItem();
        account = new PrimaryDrawerItem();
        logOut = new PrimaryDrawerItem();
        //getNumberOfEvents();

    }

    public void loadDrawer() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder)
                        .fit().centerCrop().into(imageView);
            }
        });

        IProfile profile = new ProfileDrawerItem().withName(fName + " " + lName).withIcon(Uri.parse(pic)).withEmail(email).withIdentifier(100);
        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(profile)
                .withSavedInstance(savedState)
                .build();

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withSliderBackgroundColor(context.getColor(R.color.black_overlay))
                .withDelayDrawerClickEvent(150)
                //.withDelayOnDrawerClose(300)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        gridEvents
                                .withName("Grid Events")
                                .withIcon(R.drawable.ic_home_white_48dp)
                                .withIdentifier(1)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectedColor(context.getColor(R.color.translivinPink))
                                .withSetSelected(true),
                        new DividerDrawerItem(),
                        fullScreen
                                .withName("Full Screen")
                                .withIcon(R.drawable.ic_fullscreen_white_48dp)
                                .withIdentifier(2)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectedColor(context.getColor(R.color.translivinPink)),
                        new DividerDrawerItem(),

                        archivedEvents
                                .withIdentifier(3)
                                .withName("Archived Events")
                                .withIcon(R.drawable.ic_archive_white_48dp)
                                .withTextColor(livinWhite)
                                .withSelectable(false),
                        new DividerDrawerItem(),

                        account
                                .withName("Account")
                                .withIcon(R.drawable.ic_account_circle_white_48dp)
                                .withIdentifier(4)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectable(false),

                        new DividerDrawerItem(),
                        logOut
                                .withName("Log Out").withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(5)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectable(false)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D

                        Fragment fragment = null;
                        if (drawerItem != null) {
                            Intent intent = null;

                            if (drawerItem.getIdentifier()==1)
                                fragment = new GridMainEventsFrag().newInstance();
                            else if (drawerItem.getIdentifier()==2)
                                fragment = new SwipeEvents().newInstance();
                            else if(drawerItem.getIdentifier()==3)
                                intent = new Intent(activity, ArchivedEvents.class);
                            else if (drawerItem.getIdentifier()==4)
                                intent = new Intent(activity, UserSettingsActivity.class);
                            else if(drawerItem.getIdentifier()==5) {
                                preferences.edit().clear().apply();
                                preferences.edit().putString("user_id", null).apply();
                                preferences.edit().putString("user_image", null).apply();
                                preferences.edit().putString("fName", null).apply();
                                preferences.edit().putString("lName", null).apply();
                                preferences.edit().putString("email", null).apply();
                                intent = new Intent(activity, ChooseLogin.class);
                            }


                            if (intent != null) {
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent); }
                            if(fragment!=null){
                                Bundle bundle = new Bundle();
                                bundle.putString("currentLat", currentLat);
                                bundle.putString("currentLng", currentLng);
                                fragment.setArguments(bundle);
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.setCustomAnimations(R.anim.enter, R.anim.exit);
                                ft.replace(R.id.fragment_container, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                                fragmentManager.executePendingTransactions();

                                return false;
                            } }
                        return false; }
                })
                .withSavedInstance(savedState)
                .withShowDrawerOnFirstLaunch(true)
                .build();


    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            View v = result.getSlider();
            List<IDrawerItem> primaryDrawerItem = result.getDrawerItems();
            TextView title = (TextView)activity.findViewById(R.id.toolbar_title);
            if (drawerItem.getIdentifier()==8) {
                if(isChecked) {
                    Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
                    toolbar.setBackgroundColor(context.getColor(R.color.livinBlack));
                    v.setBackgroundColor(context.getColor(R.color.livinBlack));
                    title.setTextColor(livinPink);

                }else {
                    toolbar.setBackgroundColor(context.getColor(R.color.livinPink));
                    v.setBackgroundColor(context.getColor(R.color.material_drawer_background));
                    title.setTextColor(context.getColor(R.color.livinBlack)); } } } };

    /**********************************************************************************************
     * Obtains the users current location via GPS location services
     * Location is converted into Lat and Lng coordinates
     * Coordinates are then passed into a Geocoder to retrieve the current address
     **********************************************************************************************/

    private void getLocation(){
        double latitude=0; double longitude=0;
        gps = new GPSTracker(context.getApplicationContext());
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            latLng = new LatLng(latitude, longitude);
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(activity.getApplicationContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                StyleableToast st = new StyleableToast(context.getApplicationContext(),
                        addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getLocality() + ", "
                                + addresses.get(0).getAdminArea() + " " + addresses.get(0).getPostalCode(), Toast.LENGTH_SHORT);
                st.spinIcon();
                st.setBackgroundColor(Color.BLUE);
                st.setTextColor(Color.WHITE);
                st.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public Drawer getResult(){
        return result;
    }
    public AccountHeader getHeader(){
        return headerResult;
    }

}
