package com.example.codenamebiscuit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.codenamebiscuit.eventfragments.ArchivedEvents;
import com.example.codenamebiscuit.helper.RoundedImageView;
import com.example.codenamebiscuit.requests.QueryEventList;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.acct_info);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getColor(R.color.livinWhite));
        collapsingToolbarLayout.setExpandedTitleColor(getColor(android.R.color.transparent));
        bindViews();
        loadProfileImage();
        setupArchivedEvent();
        setupUpcomingEvents();
        setupRemovedEvent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp);
        upArrow.setColorFilter(getResources().getColor(R.color.livinWhite), PorterDuff.Mode.SRC_ATOP);
        this.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return upcoming;
    }
    private int getSavedEventCount(){
        int size=0;
        try {
            size=new QueryEventList(getString(R.string.DATABASE_SAVED_EVENTS_PULLER), userId).execute().get().size();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return size;
    }
    private int getRemovedEventCount(){
        int size=0;
        try {
            size=new QueryEventList(getString(R.string.DATABASE_DELETED_EVENTS_PULLER), userId).execute().get().size();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return size;
    }

}
