package com.example.codenamebiscuit.eventfragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.MapViewPagerAdapter;
import com.example.codenamebiscuit.helper.Utils;
import com.github.nitrico.mapviewpager.MapViewPager;
import com.google.android.gms.maps.SupportMapFragment;
import com.mikepenz.iconics.view.IconicsButton;

/**
 * Created by jaskarnjagpal on 4/26/17.
 */

public class ClusterMap extends AppCompatActivity implements MapViewPager.Callback, ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private MapViewPager mvp;
    private int currentPage;
    private IconicsButton leftButton;
    private IconicsButton rightButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cluster_map);
        String user_id = getIntent().getStringExtra("userId");

        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        viewPager = (ViewPager) findViewById(R.id.viewPagerMap);
        leftButton = (IconicsButton) findViewById(R.id.left_nav);
        leftButton.setVisibility(View.GONE);
        rightButton=(IconicsButton) findViewById(R.id.right_nav);
        viewPager.setPageMargin(Utils.dp(this, 18));
        viewPager.addOnPageChangeListener(this);
        setupNavButtons();
        Utils.setMargins(viewPager, 0, 0, 0, 10);

        mvp = new MapViewPager.Builder(this).mapFragment(map).viewPager(viewPager)
                .position(0).adapter(new MapViewPagerAdapter(getSupportFragmentManager(), this, user_id, this))
                .callback(this).build();
    }

    private void setupNavButtons(){
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        });
    }

    @Override
    public void onMapViewPagerReady() {
        mvp.getMap().setPadding(0,
                Utils.dp(this, 40),
                Utils.getNavigationBarWidth(this),
                viewPager.getHeight() + Utils.getNavigationBarHeight(this));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position==0)
            leftButton.setVisibility(View.GONE);
        else leftButton.setVisibility(View.VISIBLE);
        if(position==viewPager.getAdapter().getCount()-1)
            rightButton.setVisibility(View.GONE);
        else rightButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
