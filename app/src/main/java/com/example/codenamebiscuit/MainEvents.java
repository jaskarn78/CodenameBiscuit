package com.example.codenamebiscuit;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.eventfragments.DeletedEventsFrag;
import com.example.codenamebiscuit.eventfragments.GridMainEventsFrag;
import com.example.codenamebiscuit.eventfragments.SavedEventsFrag;
import com.example.codenamebiscuit.eventfragments.SwipeEvents;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

public class MainEvents extends Fragment {
    private View mContentView;
    private Handler mHandler;
    private Runnable mShowContentRunnable = new Runnable() {
        @Override
        public void run() {
            //setContentShown(true);

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_events);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        mContentView = inflater.inflate(R.layout.activity_main_events, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);
        return mContentView;


    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //setContentView(mContentView);
        initiUI();
    }
    private void initiUI(){
        //setContentShown(false);
        mHandler = new Handler();
        mHandler.postDelayed(mShowContentRunnable, 2000);
        final ViewPager viewPager = (ViewPager)mContentView.findViewById(R.id.vp_horizontal_ntb);

        viewPager.setAdapter(new MyPagerAdapter(getFragmentManager()));
        final String[] colors = getResources().getStringArray(R.array.livColors);

        final NavigationTabBar navigationTabBar = (NavigationTabBar)mContentView.findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_grid_on_white_48dp),
                        Color.parseColor(colors[2]))
                        .title("Grid")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fullscreen_white_48dp),
                        Color.parseColor(colors[2]))
                        .title("Full Screen")
                        .build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);
    }
    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        private Bundle bundle;
        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0:
                    return GridMainEventsFrag.newInstance(bundle);
                case 1:
                    return SwipeEvents.newInstance(bundle);
                default:
                    return GridMainEventsFrag.newInstance(bundle);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
