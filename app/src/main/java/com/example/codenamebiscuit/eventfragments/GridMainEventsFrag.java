package com.example.codenamebiscuit.eventfragments;

/**
 * Created by jaskarnjagpal on 3/1/17.
 */


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.Events;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.EventBundle;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.example.codenamebiscuit.requests.UpdateDbOnSwipe;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;
import com.wunderlist.slidinglayer.SlidingLayer;

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
    private EventBundle events;
    private Bundle eventBundle;
    private Handler mHandler;
    private TextView eventName, eventLoc, eventHoster;
    private TextView eventPref;
    private ImageView eventImage;
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
        data = new ArrayList<>();
        setHasOptionsMenu(true);}


    public static GridMainEventsFrag newInstance(Bundle bundle) {
        GridMainEventsFrag gridMainEventsFrag = new GridMainEventsFrag();
        gridMainEventsFrag.setArguments(bundle);
        return gridMainEventsFrag;
    }
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
            mHandler.postDelayed(mShowContentRunnable, 1000);
            SwipeEvents frag = (SwipeEvents)getFragmentManager().findFragmentByTag("swipeFrag");
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), userId).execute().get();
            Events.fromJson(data, getActivity());
            events = new EventBundle(data);
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
        toolbarSpinner = (MaterialSpinner)getActivity().findViewById(R.id.spinner);


        setContentView(mContentView);
        if (isAdded()) {
            obtainData();
            mRecyclerView.setAdapter(mAdapter);
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
                                eventBundle = events.getBundle(position); }
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
                            } catch (JSONException e) { e.printStackTrace();} } }

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
                        mAdapter.setEventData(data);mAdapter.notifyDataSetChanged();break;
                    case 1:
                        Events.toFurthest(data);
                        mAdapter.setEventData(data);mAdapter.notifyDataSetChanged();break;
                    case 2:
                        Events.toEarliest(data);
                        mAdapter.setEventData(data);mAdapter.notifyDataSetChanged();break;
                    case 3:
                        Events.toLatest(data);
                        mAdapter.setEventData(data); mAdapter.notifyDataSetChanged(); break;
                    default: materialSpinner.setSelectedIndex(0);break;

                }}});

    }
}
