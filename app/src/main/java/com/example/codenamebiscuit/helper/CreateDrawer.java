package com.example.codenamebiscuit.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.codenamebiscuit.UserAccount;
import com.example.codenamebiscuit.eventfragments.ArchivedEvents;
import com.example.codenamebiscuit.MainActivity;
import com.example.codenamebiscuit.eventfragments.ClusterMap;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.example.codenamebiscuit.settings.UserSettingsActivity;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by jaskarnjagpal on 3/3/17.
 */

public class CreateDrawer {
    private String fName, lName, pic, email, userId;
    private Bundle savedState;
    private Toolbar toolbar;
    private Activity activity;
    private AccountHeader headerResult=null;
    private int livinPink;
    private int livinWhite;
    private PrimaryDrawerItem gridEvents;
    private PrimaryDrawerItem mapEvents;
    private PrimaryDrawerItem archivedEvents;
    private PrimaryDrawerItem account;
    private PrimaryDrawerItem logOut;
    private SharedPreferences preferences;
    protected static final int REQUEST_CODE = 1;



    public CreateDrawer(Bundle savedState, Toolbar toolbar, Activity activity, String userId, FragmentManager fm) {
        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        this.pic=preferences.getString("user_image", null);
        this.fName=preferences.getString("fName", null);
        this.lName=preferences.getString("lName", null);
        this.email=preferences.getString("email", null);
        this.userId=userId;
        this.savedState=savedState; this.toolbar=toolbar;
        this.activity=activity;
        FragmentManager fm1 = fm;

        livinPink=activity.getColor(R.color.livinPink);
        livinWhite=activity.getColor(R.color.livinWhite);
        gridEvents = new PrimaryDrawerItem();

        archivedEvents = new PrimaryDrawerItem();
        account = new PrimaryDrawerItem();
        logOut = new PrimaryDrawerItem();
        mapEvents = new PrimaryDrawerItem(); }

    public void loadDrawer() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                ImageLoader.loadImageFromUri(imageView.getContext(), uri, imageView); }
        });

        //navigation drawer creation
        IProfile profile = new ProfileDrawerItem().withName(fName + " " + lName).withIcon(Uri.parse(pic)).withEmail(email).withIdentifier(100);
        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .addProfiles(profile)
                .withCurrentProfileHiddenInList(true)
                .withProfileImagesClickable(true)
                .withHeaderBackground(R.drawable.login_bg)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .withSavedInstance(savedState)
                .build();

           new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withSliderBackgroundColor(activity.getColor(R.color.black_overlay))
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        gridEvents
                                .withName("Upcoming Events")
                                .withIcon(R.drawable.ic_home_white_48dp)
                                .withIdentifier(1)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinWhite)
                                .withSelectedIconColor(livinWhite)
                                .withSelectedColor(activity.getColor(R.color.translivinPink))
                                .withSetSelected(false),
                        new DividerDrawerItem(),

                        archivedEvents
                                .withIdentifier(2)
                                .withName("Archived Events")
                                .withIcon(R.drawable.ic_archive_white_48dp)
                                .withTextColor(livinWhite)
                                .withSelectedColor(activity.getColor(R.color.translivinPink))
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        mapEvents
                                .withIdentifier(3)
                                .withName("Launch Map")
                                .withIcon(R.drawable.ic_map_white_48dp)
                                .withTextColor(livinWhite)
                                .withSelectedColor(activity.getColor(R.color.translivinPink))
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
                                .withSelectedColor(activity.getColor(R.color.translivinPink))
                                .withSelectable(false),

                        new DividerDrawerItem(),
                        logOut
                                .withName("Log Out")
                                .withIcon(R.drawable.ic_exit_to_app_white_48dp)
                                .withIdentifier(5)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectedColor(activity.getColor(R.color.translivinPink))
                                .withSelectable(false),
                        new DividerDrawerItem())
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(activity, MainActivity.class);
                            } else if (drawerItem.getIdentifier() == 2)
                                intent = new Intent(activity, ArchivedEvents.class);
                            else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(activity, ClusterMap.class);
                            } else if (drawerItem.getIdentifier() == 4) {
                                intent = new Intent(activity, UserAccount.class);
                                intent.putExtra("user_image", pic);
                                intent.putExtra("user_name", fName+" "+lName);
                                intent.putExtra("user_email", email);
                            } else if (drawerItem.getIdentifier() == 5) {
                                preferences.edit().clear().apply();
                                if (LoginManager.getInstance() != null)
                                    LoginManager.getInstance().logOut();
                                else
                                    App.getGoogleApiHelper().mGoogleApiClient.disconnect();
                                intent = new Intent(activity, ChooseLogin.class);
                            }
                            if (intent != null) {
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("userId", userId);
                                activity.startActivity(intent);
                            }
                        }return false;
                    }
                })
                .withSavedInstance(savedState)
                .withShowDrawerOnFirstLaunch(false)
                .build();

    }
    public void setBackground(Drawable headerBackground) {
        headerResult.getHeaderBackgroundView().setImageDrawable(headerBackground);
    }

}
