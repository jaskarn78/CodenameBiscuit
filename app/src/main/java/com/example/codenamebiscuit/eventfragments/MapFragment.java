package com.example.codenamebiscuit.eventfragments;

import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.EventBundle;
import com.example.codenamebiscuit.helper.ImageLoader;
import com.example.codenamebiscuit.helper.MapViewPagerAdapter;

/**
 * Created by jaskarnjagpal on 4/28/17.
 */

public class MapFragment extends Fragment {
    private Toolbar toolbar;
    private int index;
    private ImageView pagerImage;
    private ProgressBar pagerProgress;
    private TextView name, distance;

    public MapFragment(){}

    public static MapFragment newInstance(int i){
        MapFragment f = new MapFragment();
        Bundle args = new Bundle();
        args.putInt("INDEX", i);
        f.setArguments(args);
        return f;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_pager, container, false);
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        pagerImage = (ImageView)view.findViewById(R.id.pager_event_image);
        pagerProgress = (ProgressBar)view.findViewById(R.id.pager_progress);
        name = (TextView)view.findViewById(R.id.pager_name);
        name.setSelected(true); name.setSingleLine(true);
        distance = (TextView)view.findViewById(R.id.pager_distance);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) index = args.getInt("INDEX", 0);
        ViewCompat.setElevation(getView(), 10f);
        if(MapViewPagerAdapter.eventImage.size()>0) {
            ImageLoader.loadImage(getContext(), MapViewPagerAdapter.eventImage.get(index), pagerImage, pagerProgress);
            name.setText(MapViewPagerAdapter.eventName.get(index));
            distance.setText(MapViewPagerAdapter.eventDistance.get(index) + " mi");
        }else{
            ImageLoader.loadBackgroundResource(getContext(), R.drawable.no_events, pagerImage);
        }
        toolbar.inflateMenu(R.menu.pager_menu);
        Drawable d = getActivity().getDrawable(R.drawable.ic_arrow_forward_black_48dp);
        d.setColorFilter(getActivity().getColor(R.color.livinWhite), PorterDuff.Mode.SRC_ATOP);
        toolbar.getMenu().findItem(R.id.view_event).setIcon(d);
        toolbar.getMenu().findItem(R.id.view_event).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), DisplayEvent.class);
                intent.putExtras(MapViewPagerAdapter.bundle.getBundle(index));
                startActivity(intent);
                return false;
            }
        });


    }




}
