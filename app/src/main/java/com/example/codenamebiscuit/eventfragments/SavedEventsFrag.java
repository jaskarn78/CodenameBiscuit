package com.example.codenamebiscuit.eventfragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.requests.QueryEventList;
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
    private SharedPreferences pref;
    private ArrayList<JSONObject> data;
    private View mContentView;
    private Handler mHandler;
    private LinearLayout bgLayout;
    private String userId;
    private Runnable mShowContentRunnable = new Runnable() {
        @Override
        public void run() {
            if(isAdded())
                setContentShown(true); } };

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
        userId = pref.getString("user_id", null);
        setHasOptionsMenu(true);

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
        bgLayout = (LinearLayout)mContentView.findViewById(R.id.bgImage);
        TextView textView = (TextView)mContentView.findViewById(R.id.events_text);
        textView.setText("0 Saved Events Found");
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerview_events);
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
        if(data.isEmpty()){
            bgLayout.setVisibility(View.VISIBLE);
        }else bgLayout.setVisibility(View.GONE);
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


    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.refresh_saved).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    mAdapter.clear();
                    data = new QueryEventList(getString(R.string.DATABASE_SAVED_EVENTS_PULLER), userId).execute().get();
                    mAdapter.addAll(data);
                    mAdapter.notifyDataSetChanged();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }
    private void obtainData(){
        setContentShown(false);
        mHandler = new Handler();
        mHandler.postDelayed(mShowContentRunnable, 00);
        try {
            data = new QueryEventList(getString(R.string.DATABASE_SAVED_EVENTS_PULLER), userId).execute().get();
            mAdapter = new EventAdapter(getActivity().getApplicationContext(), 1, "saved", getActivity());
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

}
