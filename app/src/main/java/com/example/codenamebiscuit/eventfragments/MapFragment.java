package com.example.codenamebiscuit.eventfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.codenamebiscuit.R;
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
        ImageLoader.loadPagerImage(getContext(), MapViewPagerAdapter.eventImage.get(index), pagerImage, pagerProgress);
        name.setText(MapViewPagerAdapter.eventName.get(index));
        distance.setText(MapViewPagerAdapter.eventDistance.get(index)+" mi");
        toolbar.inflateMenu(R.menu.archived);

    }




}
