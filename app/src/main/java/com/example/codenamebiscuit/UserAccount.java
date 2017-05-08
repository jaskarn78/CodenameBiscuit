package com.example.codenamebiscuit;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.codenamebiscuit.eventfragments.ArchivedEvents;
import com.example.codenamebiscuit.helper.App;
import com.example.codenamebiscuit.helper.RoundedImageView;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.OptionalPendingResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by jaskarnjagpal on 5/8/17.
 */

public class UserAccount extends AppCompatActivity {
    private ImageView userImageView;
    private TextView removedEvents, savedEvents, upcoming;
    private TextView joinDate;
    private String userId, join_date;
    private LinearLayout upcomingLayout, savedLayout, removedLayout;
    private AppBarLayout appBar;
    private RelativeLayout clearCache, logOut;
    private SharedPreferences preferences;
    private Switch music, food, sports, health, family, retail, charity, entertainment, outdoors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.acct_info);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getColor(R.color.livinWhite));
        collapsingToolbarLayout.setExpandedTitleColor(getColor(android.R.color.transparent));
        bindViews(); loadProfileImage();
        setupArchivedEvent(); setupUpcomingEvents(); setupRemovedEvent();
        setupLogOutButton(); setupCacheButtion();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp);
        this.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if ((collapsingToolbarLayout.getHeight() + verticalOffset) < (2 * ViewCompat.getMinimumHeight(collapsingToolbarLayout))) {
                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                } else {
                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });


    }
    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }


    private void setupArchivedEvent() {
        savedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserAccount.this, ArchivedEvents.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    private String getJoinDate() {
        try {

            join_date = new QueryEventList(getString(R.string.PULL_USER_JOIN_DATE), userId).execute().get().get(0).getString("join_date");
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            Date date =sf.parse(join_date);
            return date.toLocaleString().substring(0, date.toLocaleString().length()-11);
        } catch (JSONException | InterruptedException | ExecutionException e1) {
            e1.printStackTrace(); } catch (ParseException e) {e.printStackTrace();
        }
        return "";
    }


    private void setupUpcomingEvents(){
        upcomingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupRemovedEvent(){
        removedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserAccount.this, ArchivedEvents.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    private void loadProfileImage() {
        final String userImage = getIntent().getStringExtra("user_image");
        Glide.with(this).load(userImage).asBitmap().centerCrop().into(new BitmapImageViewTarget(userImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                userImageView.setImageDrawable(circularBitmapDrawable);
            }

        });
    }

    private void bindViews(){
        userImageView = (ImageView) findViewById(R.id.user_image);
        removedEvents = (TextView)findViewById(R.id.removed_events);
        savedEvents = (TextView)findViewById(R.id.saved_events);
        upcoming = (TextView)findViewById(R.id.upcoming_events);
        joinDate = (TextView)findViewById(R.id.join_date);
        userId = getIntent().getStringExtra("userId");
        appBar = (AppBarLayout)findViewById(R.id.app_bar_layout);
        clearCache = (RelativeLayout)findViewById(R.id.clear_cache);
        logOut = (RelativeLayout)findViewById(R.id.log_out);

        music = (Switch)findViewById(R.id.music_switch);
        music.setChecked(true);
        food = (Switch)findViewById(R.id.food_switch);
        sports = (Switch)findViewById(R.id.sports_switch);
        health = (Switch)findViewById(R.id.health_switch);
        family = (Switch)findViewById(R.id.family_switch);
        retail = (Switch)findViewById(R.id.retail_switch);
        outdoors = (Switch)findViewById(R.id.outdoor_switch);
        charity = (Switch)findViewById(R.id.charity_switch);
        entertainment = (Switch)findViewById(R.id.entertainment_switch);

        upcomingLayout = (LinearLayout)findViewById(R.id.upcoming_layout);
        savedLayout = (LinearLayout)findViewById(R.id.saved_layout);
        removedLayout = (LinearLayout)findViewById(R.id.removed_layout);

        upcoming.setText(getUpcomingEventCount()+"");
        savedEvents.setText(getSavedEventCount()+"");
        removedEvents.setText(getRemovedEventCount()+"");
        joinDate.setText("Member Since\n  "+getJoinDate());
    }

    private int getUpcomingEventCount(){
        int upcoming=0;
        try {
            upcoming = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), userId).execute().get().size();
        } catch (InterruptedException | ExecutionException e) {e.printStackTrace();}
        return upcoming;
    }
    private int getSavedEventCount(){
        int size=0;
        try {
            size=new QueryEventList(getString(R.string.DATABASE_SAVED_EVENTS_PULLER), userId).execute().get().size();
        } catch (InterruptedException | ExecutionException e) {e.printStackTrace();}
        return size;
    }
    private int getRemovedEventCount(){
        int size=0;
        try {
            size=new QueryEventList(getString(R.string.DATABASE_DELETED_EVENTS_PULLER), userId).execute().get().size();
        } catch (InterruptedException | ExecutionException e) {e.printStackTrace();}
        return size;
    }

    private void setupLogOutButton(){

        logOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(UserAccount.this, ChooseLogin.class);

                    new FancyAlertDialog.Builder(UserAccount.this).setActivity(UserAccount.this)
                            .setPositiveColor(R.color.livinPink).setNegativeColor(R.color.black)
                            .setPositiveButtonText("Log out").setTextSubTitle("Are you sure you would like to log out?")
                            .setNegativeButtonText("Cancel")
                            .setSubtitleColor(R.color.black).setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            if(LoginManager.getInstance()!=null)
                                LoginManager.getInstance().logOut();
                            else {
                                App.getGoogleApiHelper().disconnect();
                            }
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    }).setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            dialog.dismiss();
                        }
                    }).build().show();}});
    }

    private void setupCacheButtion(){
        clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FancyAlertDialog.Builder(UserAccount.this).setActivity(UserAccount.this)
                        .setTextSubTitle("Clear Cache")
                        .setPositiveColor(R.color.livinPink).setNegativeColor(R.color.black)
                        .setPositiveButtonText("Clear Cache")
                        .setTextSubTitle("Are you sure you would like to clear the application cache?\nYou will be logged out")
                        .setNegativeButtonText("Cancel")
                        .setSubtitleColor(R.color.black).setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        deleteCache(UserAccount.this);
                        dialog.dismiss();
                        if(LoginManager.getInstance()!=null)
                            LoginManager.getInstance().logOut();
                        if(App.getGoogleApiHelper().isConnected())
                            App.getGoogleApiHelper().disconnect();
                        Intent intent = new Intent(UserAccount.this, ChooseLogin.class);
                        startActivity(intent);
                    }
                }).setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                    }
                }).build().show();}});
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;}
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {return false;}
    }



}
