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
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.codenamebiscuit.ArchivedEvents;
import com.example.codenamebiscuit.MainActivity;
import com.example.codenamebiscuit.MapActivity;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.example.codenamebiscuit.settings.UserSettingsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import java.util.List;


/**
 * Created by jaskarnjagpal on 3/3/17.
 */

public class CreateDrawer {
    private String fName, lName, pic, email, userId;
    private Bundle savedState;
    private Toolbar toolbar;
    private Context context;
    private Activity activity;
    private Bundle bundle;
    Drawer result=null;
    AccountHeader headerResult=null;
    private FragmentManager fragmentManager;
    private int livinPink, overlay;
    private int livinBlack, livinWhite;
    private PrimaryDrawerItem gridEvents;
    private PrimaryDrawerItem mapEvents;
    private PrimaryDrawerItem archivedEvents;
    private PrimaryDrawerItem account;
    private PrimaryDrawerItem logOut;
    GPSTracker gps;
    LatLng latLng;
    private Bitmap bitmap;
    SharedPreferences preferences;


    public CreateDrawer(Bundle savedState, Toolbar toolbar, Activity activity, String userId) {

        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        this.pic=preferences.getString("user_image", null);
        this.fName=preferences.getString("fName", null);
        this.lName=preferences.getString("lName", null);
        this.email=preferences.getString("email", null);
        this.userId=userId;
        this.savedState=savedState; this.toolbar=toolbar;
        this.activity=activity;
        this.bundle = new Bundle();

        livinPink=activity.getColor(R.color.livinPink);
        overlay=activity.getColor(R.color.black_overlay);
        livinBlack=activity.getColor(R.color.livinBlack);
        livinWhite=activity.getColor(R.color.livinWhite);
        gridEvents = new PrimaryDrawerItem();

        archivedEvents = new PrimaryDrawerItem();
        account = new PrimaryDrawerItem();
        logOut = new PrimaryDrawerItem();
        mapEvents = new PrimaryDrawerItem();

    }

    public void loadDrawer() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder)
                        .fitCenter().centerCrop().into(imageView);
            }
        });

        IProfile profile = new ProfileDrawerItem().withName(fName + " " + lName).withIcon(Uri.parse(pic)).withEmail(email).withIdentifier(100);
        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .addProfiles(profile)
                .withCurrentProfileHiddenInList(true)
                .withProfileImagesClickable(true)
                .withHeaderBackground(R.drawable.livbg)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .withSavedInstance(savedState)
                .build();

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withSliderBackgroundColor(activity.getColor(R.color.black_overlay))
                //.withDelayDrawerClickEvent(150)
                .withDelayOnDrawerClose(50)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        gridEvents
                                .withName("Grid Events")
                                .withIcon(R.drawable.ic_home_white_48dp)
                                .withIdentifier(1)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinWhite)
                                .withSelectedIconColor(livinWhite)
                                .withSelectedColor(activity.getColor(R.color.translivinPink))
                                .withSetSelected(true),
                        new DividerDrawerItem(),

                        archivedEvents
                                .withIdentifier(2)
                                .withName("Archived Events")
                                .withIcon(R.drawable.ic_archive_white_48dp)
                                .withTextColor(livinWhite)
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        mapEvents
                                .withIdentifier(3)
                                .withName("Launch Map")
                                .withIcon(R.drawable.ic_map_white_48dp)
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
                                .withName("Log Out")
                                .withIcon(R.drawable.ic_exit_to_app_white_48dp)
                                .withIdentifier(5)
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
                        if (drawerItem != null) {
                            Intent intent = null;

                            if (drawerItem.getIdentifier()==1)
                                intent = new Intent(activity, MainActivity.class);
                            else if(drawerItem.getIdentifier()==2)
                                intent = new Intent(activity, ArchivedEvents.class);
                            else if(drawerItem.getIdentifier()==3) {
                                intent = new Intent(activity, MapActivity.class);
                            }
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
                                intent.putExtra("userId", userId);
                                activity.startActivity(intent); }
                            }
                        return false; }
                })
                .withSavedInstance(savedState)
                .withShowDrawerOnFirstLaunch(false)
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
                    toolbar.setBackgroundColor(activity.getColor(R.color.livinBlack));
                    v.setBackgroundColor(activity.getColor(R.color.livinBlack));
                    title.setTextColor(livinPink);

                }else {
                    toolbar.setBackgroundColor(activity.getColor(R.color.livinPink));
                    v.setBackgroundColor(activity.getColor(R.color.material_drawer_background));
                    title.setTextColor(activity.getColor(R.color.livinBlack)); } } } };


    public void setBundle(Bundle bundle){
        this.bundle=bundle;
    }

    public void setBitmap(Bitmap picture){
        bitmap=picture;
    }

    public AccountHeader getHeaderResult(){
        return headerResult;
    }

}
