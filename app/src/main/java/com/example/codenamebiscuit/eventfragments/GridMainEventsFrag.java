package com.example.codenamebiscuit.eventfragments;

/**
 * Created by jaskarnjagpal on 3/1/17.
 */

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.helper.Events;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.example.codenamebiscuit.requests.RunQuery;
import com.example.codenamebiscuit.requests.UpdateDbOnSwipe;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


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
    private MaterialSpinner toolbarSpinner;
    private Runnable mShowContentRunnable = new Runnable() {
        @Override
        public void run() {if (isAdded()) { setContentShown(true);}} };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveEvent = new JSONObject();
        deleteEvent = new JSONObject();
        userId = getArguments().getString("currentUserId");
        data = new ArrayList<>();
        setHasOptionsMenu(true);}


    public static GridMainEventsFrag newInstance(Bundle bundle) {
        GridMainEventsFrag gridMainEventsFrag = new GridMainEventsFrag();
        gridMainEventsFrag.setArguments(bundle);
        return gridMainEventsFrag; }


    public ArrayList<JSONObject> getData(){ return data; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.activity_main, container, false);
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerview_events);
        return super.onCreateView(inflater, container, savedInstanceState); }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.refresh).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                eneableRefreshing();
                return false;}});
        super.onCreateOptionsMenu(menu, inflater); }


    private void obtainData() {
        try {
            setContentShown(false);
            Handler mHandler = new Handler();
            mHandler.postDelayed(mShowContentRunnable, 800);
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), userId).execute().get();
            Events.fromJson(data, getActivity());
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
            mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            enableCardSwiping();
            setupSpinner();
            } }


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
                                Snackbar.make(getContentView(), "Event Removed: "+mAdapter.getObject().get(position)
                                        .get("event_id"), Snackbar.LENGTH_SHORT).show();
                                mAdapter.notifyItemRemoved(position);
                                deleteEvent.put("user_id", mAdapter.getObject().get(position).getString("user_id"));
                                deleteEvent.put("event_id", mAdapter.getObject().get(position).getString("event_id"));
                                data.remove(position);
                                new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_DELETED_EVENTS)).execute(deleteEvent);
                            } catch (JSONException e) { e.printStackTrace();} } }

                    @Override
                    public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] ints) {
                        for (int position : ints) {
                            try {
                                Snackbar.make(getContentView(), "Event Saved: "+mAdapter.getObject().get(position)
                                    .get("event_id"), Snackbar.LENGTH_SHORT).show();
                                mAdapter.notifyItemRemoved(position);
                                saveEvent.put("user_id", mAdapter.getObject().get(position).getString("user_id"));
                                saveEvent.put("event_id", mAdapter.getObject().get(position).getString("event_id"));
                                data.remove(position);
                                //mAdapter.removeBundleAtPosition(position);
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


    private void eneableRefreshing(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(GridMainEventsFrag.this); ft.attach(GridMainEventsFrag.this);
        ft.commit();
    }

}
