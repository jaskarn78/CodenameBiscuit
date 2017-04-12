package com.example.codenamebiscuit.eventfragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.helper.UpdateDbOnSwipe;
import com.example.codenamebiscuit.rv.ClickListener;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by jaskarnjagpal on 2/23/17.
 */

public class DeletedEventsFrag extends ProgressFragment{
    private static final String TAG = "Saved Events Fragment";

    private EventAdapter mAdapter;
    protected SharedPreferences pref;
    private JSONObject currentUserId = new JSONObject();
    private RecyclerView mRecyclerView;
    private ArrayList<JSONObject> data;
    private View mContentView;
    Bundle bundle;
    private Handler mHandler;
    private Runnable mShowContentRunnable = new Runnable() {

        @Override
        public void run() {
            if(isAdded())
                setContentShown(true);
        }

    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        bundle = new Bundle();
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String user_id = pref.getString("user_id", null);
        try {
            currentUserId.put("user_id", user_id); }
        catch (JSONException e) {
            e.printStackTrace(); }


    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        mContentView = inflater.inflate(R.layout.activity_main, container, false);

        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerview_events);

        mAdapter = new EventAdapter(getActivity().getApplicationContext(), 1, "deleted",getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**********************************************************************************************
     * sets up recycler view and assigns layout
     * assigns mEventAdapter which contains all event information retrieved from MySQL request
     * recycler view is assigned a linear layout
     **********************************************************************************************/

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setContentView(mContentView);
        setAllowEnterTransitionOverlap(true);
        setAllowReturnTransitionOverlap(true);
        obtainData();
        mAdapter.setEventData(data);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new RecyclerItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                Intent intent = new Intent(getActivity(), DisplayEvent.class);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        });
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemViewCacheSize(100);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

    }
    private void obtainData(){
        setContentShown(false);

        mHandler = new Handler();
        mHandler.postDelayed(mShowContentRunnable, 2000);
        try {
            data = new QueryEventList(getString(R.string.DATABASE_DELETED_EVENTS_PULLER)).execute(currentUserId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static DeletedEventsFrag newInstance() {
        return new DeletedEventsFrag();
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
    public void onStart() {
        super.onStart();
    }
}
