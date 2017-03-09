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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.asha.nightowllib.NightOwl;
import com.example.codenamebiscuit.MainActivity;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.eventfragments.DeletedEventsFrag;
import com.example.codenamebiscuit.eventfragments.GridMainEventsFrag;
import com.example.codenamebiscuit.eventfragments.MainEventsFrag;
import com.example.codenamebiscuit.eventfragments.SavedEventsFrag;
import com.example.codenamebiscuit.eventfragments.SwipeEvents;
import com.example.codenamebiscuit.settings.UserSettingsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;
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
    Drawer result=null;
    AccountHeader headerResult=null;
    private FragmentManager fragmentManager;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;

    GPSTracker gps;
    LatLng latLng;

    public CreateDrawer(String fName, String lName, String pic, String email, Bundle savedState,
                        Toolbar toolbar, Context context, Activity activity, FragmentManager fragmentManager) {
        this.fName=fName; this.lName=lName;
        this.pic=pic; this.email=email;
        this.savedState=savedState; this.toolbar=toolbar;
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
                //.withDrawerLayout(R.layout.crossfade_drawer)
                //.withDrawerWidthDp(70)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Grid Events").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withSelectable(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Full Screen").withIcon(GoogleMaterial.Icon.gmd_fullscreen).withIdentifier(2).withSelectable(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Saved Events").withIcon(GoogleMaterial.Icon.gmd_save).withIdentifier(3).withSelectable(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Deleted Events").withIcon(GoogleMaterial.Icon.gmd_delete).withIdentifier(4).withSelectable(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Preferences").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(5).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Current coordinates").withIcon(GoogleMaterial.Icon.gmd_gps).withIdentifier(7).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Fragment fragment = null;
                        if (drawerItem != null) {
                            Intent intent = null;

                            if (drawerItem.getIdentifier()==1) {
                                fragment = new GridMainEventsFrag().newInstance();
                            } else if (drawerItem.getIdentifier()==2) {
                                fragment = new SwipeEvents().newInstance();
                            } else if (drawerItem.getIdentifier()==3) {
                                fragment = new SavedEventsFrag().newInstance();
                            } else if (drawerItem.getIdentifier()==4) {
                                fragment = new DeletedEventsFrag().newInstance();
                            } else if (drawerItem.getIdentifier()==5) {
                                intent = new Intent(activity, UserSettingsActivity.class);
                            } else if (drawerItem.getIdentifier()==6) {
                                //fragment = new GridMainEventsFrag();
                            }else if(drawerItem.getIdentifier()==7){
                                getLocation();
                            }
                            if (intent != null) {
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent); }
                            if(fragment!=null){
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                                ft.replace(R.id.fragment_container, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                                fragmentManager.executePendingTransactions();
                                result.closeDrawer();
                                return true;
                            } }
                        return true; }
                })
                .withGenerateMiniDrawer(true)
                .withSavedInstance(savedState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        //get the CrossfadeDrawerLayout which will be used as alternative DrawerLayout for the Drawer
        //the CrossfadeDrawerLayout library can be found here: https://github.com/mikepenz/CrossfadeDrawerLayout
        //crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();

        //define maxDrawerWidth
        //crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(context.getApplicationContext()));
        //add second view (which is the miniDrawer)
        //final MiniDrawer miniResult = result.getMiniDrawer();
        //build the view for the MiniDrawer
        //View view = miniResult.build(context);
        //set the background of the MiniDrawer as this would be transparent
        //view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(context, com.mikepenz.materialdrawer.R.attr.material_drawer_background,
                //com.mikepenz.materialdrawer.R.color.material_drawer_dark_primary_icon));
        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        //crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close

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
