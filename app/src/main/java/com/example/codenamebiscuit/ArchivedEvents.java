package com.example.codenamebiscuit;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.codenamebiscuit.eventfragments.DeletedEventsFrag;
import com.example.codenamebiscuit.eventfragments.SavedEventsFrag;
import com.example.codenamebiscuit.helper.RunQuery;
import com.geniusforapp.fancydialog.FancyAlertDialog;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

public class ArchivedEvents extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private int page;
    private String userId;
    private SharedPreferences sharedPreferences;
    private JSONObject currentUserId;
    private Bundle bundle;
    private SavedEventsFrag savedEventsFrag;
    private DeletedEventsFrag deletedEventsFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_archived_events);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId=getIntent().getStringExtra("userId");
        currentUserId = new JSONObject();
        try {
            currentUserId.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace(); }

        if(savedInstanceState!=null)
            page=savedInstanceState.getInt("page");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Saved Events");
        bundle = new Bundle();


        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp);
        upArrow.setColorFilter(getResources().getColor(R.color.livinWhite), PorterDuff.Mode.SRC_ATOP);
        this.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initiUI();
    }

    /*********************************************************************************
     Create Menu
     **********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.archived, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restore_dialog:
                sharedPreferences.edit().putBoolean("showDialog", false).apply();
                break;
            case R.id.restore_saved:
                FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(ArchivedEvents.this)
                        .setTextTitle("Restore Saved Events")
                        .setImageDrawable(getDrawable(R.mipmap.livlogoweb))
                        .setTextSubTitle(sharedPreferences.getString("fName", null)+" "
                                +sharedPreferences.getString("lName", null))
                        .setBody("Restore all saved events?")
                        .setPositiveButtonText("Continue")
                        .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                new RunQuery(getString(R.string.RESTORE_ALL_SAVED_EVENTS)).execute(currentUserId);
                                dialog.dismiss();
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.detach(savedEventsFrag); ft.attach(savedEventsFrag);
                                ft.commit();
                            }
                        })
                        .setNegativeButtonText("Exit")
                        .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                dialog.dismiss(); }
                        })
                        .build();
                alert.show();
                break;
            case R.id.restore_removed:
                alert = new FancyAlertDialog.Builder(ArchivedEvents.this)
                        .setTextTitle("Restore Removed Events")
                        .setImageDrawable(getDrawable(R.mipmap.livlogoweb))
                        .setTextSubTitle(sharedPreferences.getString("fName", null)+" "
                                +sharedPreferences.getString("lName", null))
                        .setBody("Restore all removed events?")
                        .setPositiveButtonText("Continue")
                        .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                new RunQuery(getString(R.string.RESTORE_ALL_DELETED_EVENTS)).execute(currentUserId);
                                dialog.dismiss();
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.detach(deletedEventsFrag); ft.attach(deletedEventsFrag);
                                ft.commit();
                            }
                        })
                        .setNegativeButtonText("Exit")
                        .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                dialog.dismiss(); }
                        })
                        .build();
                alert.show();
        }
            return false;
    }

    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("page", page);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        page = savedInstanceState.getInt("page");
    }


    private void initiUI(){
        final ViewPager viewPager = (ViewPager)findViewById(R.id.vp_horizontal_ntb);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        final String[] colors = getResources().getStringArray(R.array.livColors);

        final NavigationTabBar navigationTabBar = (NavigationTabBar)findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_save_black_48dp),
                        Color.parseColor(colors[2]))
                        .title("Saved")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_delete_black_48dp),
                        Color.parseColor(colors[2]))
                        .title("Removed")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setBehaviorEnabled(true);
        navigationTabBar.setViewPager(viewPager, page);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
                page=position;
                if(position==0)
                    toolbarTitle.setText("Saved Events");
                else
                    toolbarTitle.setText("Removed Events");
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }

        });

    }
    private class MyPagerAdapter extends FragmentPagerAdapter {

        public Bundle bundle  = new Bundle();
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0:
                    return savedEventsFrag=SavedEventsFrag.newInstance();
                case 1:
                    return deletedEventsFrag=DeletedEventsFrag.newInstance();
                default:
                    return savedEventsFrag=SavedEventsFrag.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
