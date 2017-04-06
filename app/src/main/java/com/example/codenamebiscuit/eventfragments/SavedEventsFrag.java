package com.example.codenamebiscuit.eventfragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;

import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.rv.ClickListener;
import com.example.codenamebiscuit.rv.EventAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by jaskarnjagpal on 2/23/17.
 */

public class SavedEventsFrag extends ProgressFragment implements ClickListener{
    private static final String TAG = "Saved Events Fragment";

    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private ArrayList<JSONObject> eventData;
    private SharedPreferences pref;
    private JSONObject currentUserId = new JSONObject();
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<JSONObject> data;
    private View mContentView;
    private Handler mHandler;
    private boolean saved;
    private Runnable mShowContentRunnable = new Runnable() {

        @Override
        public void run() {
            setContentShown(true);
        }

    };


    @Override
    public void itemClicked(View view, int position) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        String user_id = pref.getString("user_id", null);
        try {
            currentUserId.put("user_id", user_id);
        } catch (JSONException e) {
            e.printStackTrace(); }
        mAdapter = new EventAdapter(getActivity().getApplicationContext(), 1, "saved", getChildFragmentManager(), getActivity());
    }

    public static SavedEventsFrag newInstance() {
        SavedEventsFrag frag = new SavedEventsFrag();
        return frag;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.activity_main, container, false);
        swipeContainer = (SwipeRefreshLayout) mContentView.findViewById(R.id.swipeContainer);
        setupSwipeDownRefresh();
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerview_events);
        eventData = new ArrayList<JSONObject>();

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    /**********************************************************************************************
     * sets up recycler view and assigns layout
     * assigns mEventAdapter which contains all event information retrieved from MySQL request
     * recycler view is assigned a linear layout
     **********************************************************************************************/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setContentView(mContentView);
        obtainData();

        mAdapter.setEventData(data);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemViewCacheSize(100);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);


    }
    private void obtainData(){
        setContentShown(false);
        mHandler = new Handler();
        mHandler.postDelayed(mShowContentRunnable, 1500);
        try {
            data = new QueryEventList(getString(R.string.DATABASE_SAVED_EVENTS_PULLER)).execute(currentUserId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**********************************************************************************************
     * When activity resumes after a pause, check to see if any new events have been added
     * set swipeContainer.setRefreshing to true
     * load the event data
     * set swipecontainer.setRefreshing to false
     **********************************************************************************************/
    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause(){
        super.onPause();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack("saved").commit();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }
    @Override
    public void onDetach(){
        super.onDetach();
    }



    @Override
    public void onStart() {
        super.onStart();
    }
    public void setData(ArrayList<JSONObject> sData){
        data=sData;
    }
    public ArrayList<JSONObject> getData(){
        return data;
    }

    /**********************************************************************************************
     * setup up swipe container to refresh event list when performing down swipe gesture
     * when swipeContainer.setRefresh(true) load the event data
     * after data has been loaded, set swipeContainer.setRefresh(false)
     **********************************************************************************************/
    private void setupSwipeDownRefresh() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                try {
                    data = new QueryEventList(getString(R.string.DATABASE_SAVED_EVENTS_PULLER)).execute(currentUserId).get();
                    mAdapter.setEventData(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light); }

}
