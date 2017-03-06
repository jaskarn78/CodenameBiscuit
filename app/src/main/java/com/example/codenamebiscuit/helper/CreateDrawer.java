package com.example.codenamebiscuit.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.codenamebiscuit.MainActivity;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.eventfragments.DeletedEventsFrag;
import com.example.codenamebiscuit.eventfragments.GridMainEventsFrag;
import com.example.codenamebiscuit.eventfragments.MainEventsFrag;
import com.example.codenamebiscuit.eventfragments.SavedEventsFrag;
import com.example.codenamebiscuit.eventfragments.SwipeEvents;
import com.example.codenamebiscuit.settings.UserSettingsActivity;
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
import android.support.v4.app.FragmentManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by jaskarnjagpal on 3/3/17.
 */

public class CreateDrawer {
    private String fName, lName, pic, email;
    private Bundle savedState;
    private Toolbar toolbar;
    private Context context;
    private Activity activity;
    private FragmentManager fragmentManager;
    GPSTracker gps;
    LatLng latLng;

    public CreateDrawer(String fName, String lName, String pic, String email, Bundle savedState,
                        Toolbar toolbar, Context context, Activity activity, FragmentManager fragmentManager) {
        this.fName=fName;
        this.lName=lName;
        this.pic=pic;
        this.email=email;
        this.savedState=savedState;
        this.toolbar=toolbar;
        this.context=context;
        this.activity=activity;
        this.fragmentManager=fragmentManager;

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

        AccountHeader headerResult = new AccountHeaderBuilder().withActivity(activity).withTranslucentStatusBar(true).withHeaderBackground(R.drawable.header)
                .addProfiles(profile).withActivity(activity).withSavedInstance(savedState).build();

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder().withActivity(activity).withToolbar(toolbar)
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
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Fragment fragment = null;
                        if (drawerItem != null) {
                            Intent intent = null;

                            if (drawerItem.getIdentifier() == 1) {
                                fragment = new MainEventsFrag();
                            } else if (drawerItem.getIdentifier() == 2) {
                                fragment = new SwipeEvents();
                            } else if (drawerItem.getIdentifier() == 3) {
                                fragment = new SavedEventsFrag();
                            } else if (drawerItem.getIdentifier() == 4) {
                                fragment = new DeletedEventsFrag();
                            } else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(activity, UserSettingsActivity.class);
                            } else if (drawerItem.getIdentifier() == 6) {
                                fragment = new GridMainEventsFrag();
                            } else if (drawerItem.getIdentifier() == 7) {
                                getLocation();
                            }
                            if (intent != null) {
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent); }

                            if (fragment != null) {
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                                        .commit(); } }
                        return false; }
                }) .withSavedInstance(savedState).withShowDrawerOnFirstLaunch(true).build();
    }
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
            geocoder = new Geocoder(context, Locale.getDefault());
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

}
