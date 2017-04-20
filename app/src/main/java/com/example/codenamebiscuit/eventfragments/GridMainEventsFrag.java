package com.example.codenamebiscuit.eventfragments;

/**
 * Created by jaskarnjagpal on 3/1/17.
 */

import android.animation.ValueAnimator;
import android.content.Context;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.Events;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.FlipAnimation;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.example.codenamebiscuit.requests.UpdateDbOnSwipe;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.maps.MapView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;
import com.wunderlist.slidinglayer.SlidingLayer;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.support.v7.widget.RecyclerView.*;


/**
 * Created by jaskarnjagpal on 2/23/17.
 */

public class GridMainEventsFrag extends ProgressFragment {
    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private JSONObject saveEvent, deleteEvent;
    private ArrayList<JSONObject> data;
    private String userId;
    private View mContentView;
    private SlidingLayer slidingLayer;
    private MaterialSpinner toolbarSpinner;
    private Bundle eventBundle;
    private Handler mHandler;
    private TextView eventName, eventLoc, eventHoster;
    private TextView eventPref;
    private ImageView eventImage;
    private ExpandableLayout expandableLayout;
    private ArrayList<Bundle> bundleList;
    private Runnable mShowContentRunnable = new Runnable() {
        @Override
        public void run() {if (isAdded()) { setContentShown(true);}} };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveEvent = new JSONObject();
        deleteEvent = new JSONObject();
        userId = getArguments().getString("currentUserId");
        slidingLayer = (SlidingLayer)getActivity().findViewById(R.id.slidingLayer1);
        setHasOptionsMenu(true);}


    public static GridMainEventsFrag newInstance() { return new GridMainEventsFrag(); }
    public ArrayList<JSONObject> getData(){ return data; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.activity_main, container, false);
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerview_events);
        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                slidingLayer.closeLayer(true);} });
        return super.onCreateView(inflater, container, savedInstanceState); }


    private void obtainData() {
        try {
            setContentShown(false);
            mHandler = new Handler();
            mHandler.postDelayed(mShowContentRunnable, 800);
            SwipeEvents frag = (SwipeEvents)getFragmentManager().findFragmentByTag("swipeFrag");
            if(frag!=null && frag.getData().size()!=data.size()) data = frag.getData();
            else {data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), userId).execute().get();
            Events.fromJson(data, getActivity());}
            mAdapter = new EventAdapter(getContext().getApplicationContext(), 2, "", getActivity());
            mAdapter.setEventData(data);
        } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); } }



    /**********************************************************************************************
     * sets up recycler view and assigns layout
     * assigns mEventAdapter which contains all event information retrieved from MySQL request
     * recycler view is assigned a linear layout
     **********************************************************************************************/
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        expandableLayout = (ExpandableLayout)getActivity().findViewById(R.id.expandable_layout);
        toolbarSpinner = (MaterialSpinner)getActivity().findViewById(R.id.spinner);
        toolbarSpinner.setSelectedIndex(toolbarSpinner.getSelectedIndex());


        setContentView(mContentView);
        if (isAdded()) {
            obtainData();
            mRecyclerView.setAdapter(mAdapter);
            bundleList = mAdapter.getBundleList();
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setItemViewCacheSize(200);
            mRecyclerView.setDrawingCacheEnabled(true);
            mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

            handleRecyclerItemClick();
            fabClick();
            enableCardSwiping();
            setupSpinner();} }


    public String getImageURL(String path) {
        return getString(R.string.IMAGE_URL_PATH) + path; }


    private void handleRecyclerItemClick(){
        RecyclerItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new RecyclerItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, final View view) {
                        eventName = (TextView)getActivity().findViewById(R.id.slidename);
                        eventHoster = (TextView)getActivity().findViewById(R.id.slideHoster);
                        eventPref = (TextView)getActivity().findViewById(R.id.slidePref);
                        eventLoc = (TextView)getActivity().findViewById(R.id.slideLocation);
                        eventImage = (ImageView)getActivity().findViewById(R.id.slideImage);

                            try {eventName.setText(data.get(position).getString("event_name"));
                                eventHoster.setText("Presented By:"+data.get(position).getString("event_sponsor"));
                                eventPref.setText(data.get(position).getString("preference_name"));
                                eventLoc.setText(data.get(position).getString("event_location"));
                                Glide.with(GridMainEventsFrag.this).load(getImageURL(data.get(position)
                                        .getString("img_path"))).into(eventImage);
                                eventBundle = getBundle(position); }
                            catch (JSONException e) { e.printStackTrace();}
                            slidingLayer.openLayer(true); } }); }


    private void fabClick(){
        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setBackgroundColor(getContext().getColor(R.color.accent));
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), DisplayEvent.class);
                if(eventBundle!=null) {
                    intent.putExtras(eventBundle);
                    getActivity().getApplicationContext().startActivity(intent); } } });
        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                slidingLayer.closeLayer(true); } }); }


    private void enableCardSwiping() {
        SwipeableRecyclerViewTouchListener swipeTouchListener = new SwipeableRecyclerViewTouchListener(mRecyclerView,
                new SwipeableRecyclerViewTouchListener.SwipeListener() {
                    @Override
                    public boolean canSwipeLeft(int i) { return true; }

                    @Override
                    public boolean canSwipeRight(int i) { return true; }

                    @Override
                    public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] ints) {
                        for (int position : ints) {
                            try {
                                mAdapter.notifyItemRemoved(position);
                                deleteEvent.put("user_id", mAdapter.getObject().get(position).getString("user_id"));
                                deleteEvent.put("event_id", mAdapter.getObject().get(position).getString("event_id"));
                                data.remove(position);
                                new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_DELETED_EVENTS)).execute(deleteEvent);
                            } catch (JSONException e) {
                                e.printStackTrace();} } }

                    @Override
                    public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] ints) {
                        for (int position : ints) {
                            try {mAdapter.notifyItemRemoved(position);
                                saveEvent.put("user_id", mAdapter.getObject().get(position).getString("user_id"));
                                saveEvent.put("event_id", mAdapter.getObject().get(position).getString("event_id"));
                                data.remove(position);
                                mAdapter.removeBundleAtPosition(position);
                                new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_SAVED_EVENTS)).execute(saveEvent);
                            } catch (JSONException e) {e.printStackTrace();} } }
                }); mRecyclerView.addOnItemTouchListener(swipeTouchListener); }


    private void setupSpinner(){
        toolbarSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, int i, long l, Object o) {
                switch (i) {
                    case 0:
                        Events.fromJson(data, getActivity());
                        materialSpinner.setSelectedIndex(i);
                        mAdapter.setEventData(data);mAdapter.notifyDataSetChanged();break;
                    case 1:
                        Events.toFurthest(data);
                        materialSpinner.setSelectedIndex(i);
                        mAdapter.setEventData(data);mAdapter.notifyDataSetChanged();break;
                    case 2:
                        Events.toEarliest(data);
                        materialSpinner.setSelectedIndex(i);
                        mAdapter.setEventData(data);mAdapter.notifyDataSetChanged();break;
                    case 3:
                        Events.toLatest(data);
                        materialSpinner.setSelectedIndex(i);
                        mAdapter.setEventData(data); mAdapter.notifyDataSetChanged(); break;
                    case 4:
                        expandableLayout.expand();materialSpinner.setSelectedIndex(0); break;
                }}});
        toolbarSpinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner materialSpinner) {
                materialSpinner.setSelectedIndex(materialSpinner.getSelectedIndex());
            }
        });
    }


    private Bundle getBundle(int position) throws JSONException {
        Bundle bundle = new Bundle();
        bundle.putString("eventName", data.get(position).getString("event_name"));
        bundle.putString("eventImage", data.get(position).getString("img_path"));
        bundle.putString("eventDate", data.get(position).getString("start_date"));
        bundle.putString("eventHoster", data.get(position).getString("event_sponsor"));
        bundle.putString("eventDistance", data.get(position).getString("event_distance"));
        bundle.putString("eventPreference", data.get(position).getString("preference_name"));
        bundle.putString("eventDescription", data.get(position).getString("event_description"));
        bundle.putString("eventLocation", data.get(position).getString("event_location"));
        bundle.putString("eventCost", data.get(position).getString("event_cost"));
        bundle.putString("eventTime", data.get(position).getString("start_time"));
        bundle.putString("eventId", data.get(position).getString("event_id"));
        bundle.putDouble("eventLat", data.get(position).getDouble("lat"));
        bundle.putDouble("eventLng", data.get(position).getDouble("lng"));
        bundle.putString("eventWebsite", data.get(position).getString("event_website"));
        return bundle;
    }


}
