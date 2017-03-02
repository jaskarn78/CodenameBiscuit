package com.example.codenamebiscuit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.helper.UpdateDbOnSwipe;
import com.example.codenamebiscuit.rv.ClickListener;
import com.example.codenamebiscuit.rv.EventAdapter;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.maps.model.LatLng;
import com.muddzdev.styleabletoastlibrary.StyleableToast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by jaskarnjagpal on 2/23/17.
 */

public class MainEventsFrag extends Fragment implements ClickListener {

    private RecyclerView mRecyclerView, mRecyclerViewFeatured;
    private EventAdapter mAdapter;
    private ArrayList<JSONObject> eventData;
    private LinearLayoutManager mLinearLayoutManager;
    private SharedPreferences pref;
    private JSONObject currentUserId = new JSONObject();
    private SwipeRefreshLayout swipeContainer;
    private JSONObject saveEvent, deleteEvent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        saveEvent = new JSONObject();
        deleteEvent=new JSONObject();


        String user_id = pref.getString("user_id", null);
        try {
            currentUserId.put("user_id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //initialize event dataset
        eventData = new ArrayList<JSONObject>();

        //Custom stylable toast*
        if (savedInstanceState == null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        setupSwipeDownRefresh();

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview_events);

        //alter toolbar title
        TextView textView = (TextView)getActivity().findViewById(R.id.toolbar_title);
        textView.setText("UpComing Events");


        return rootView;

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**********************************************************************************************
     * sets up recycler view and assigns layout
     * assigns mEventAdapter which contains all event information retrieved from MySQL request
     * recycler view is assigned a linear layout
     **********************************************************************************************/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StyleableToast st = new StyleableToast(getContext(), "Network not detected!", Toast.LENGTH_LONG);
        //Custom stylable toast*
        st.setBackgroundColor(Color.RED);
        st.setTextColor(Color.WHITE);
        st.spinIcon();
        st.setMaxAlpha();

        if(!isNetworkAvailable())
            st.show();


        mAdapter = new EventAdapter(getContext(), 1);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setAdapter(mAdapter);
       //mRecyclerViewFeatured.setAdapter(mAdapter);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManagerFeatured =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(40);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

       // mRecyclerViewFeatured.setLayoutManager(layoutManagerFeatured);
        //mRecyclerViewFeatured.setHasFixedSize(true);
        //mRecyclerViewFeatured.setItemViewCacheSize(20);
        //mRecyclerViewFeatured.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //mRecyclerViewFeatured.setItemAnimator(new DefaultItemAnimator());

        enableCardSwiping();}


    /**********************************************************************************************
     * When activity resumes after a pause, check to see if any new events have been added
     * set swipeContainer.setRefreshing to true
     * load the event data
     * set swipecontainer.setRefreshing to false
     **********************************************************************************************/
    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        loadEventData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
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
                loadEventData();
                swipeContainer.setRefreshing(false);
            }

        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }
    /**********************************************************************************************
     * Handles the drop down functionality in the list view of the event data
     * When image button is clicked, additional event information is revealed
     * @param view
     * @param position
     **********************************************************************************************/

    @Override
    public void itemClicked(View view, int position) {
        RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.extend);
        if (layout.getVisibility() == View.GONE)
            layout.setVisibility(View.VISIBLE);
        else
            layout.setVisibility(View.GONE);
    }


    /**********************************************************************************************
     * HTTP request to run python script which contains sql command
     * to retrieve all event data filtered by user id
     **********************************************************************************************/
    private void loadEventData(){
            QueryEventList list = (QueryEventList)
                    new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), getContext()).execute(currentUserId);
        try {
            Log.i("list size: ",list.get()+"");
            setEventData(list.get());
            eventData = list.get();
            mAdapter.setEventData(eventData);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<JSONObject> getEventList(){
        return eventData;
    }
    public void setEventData(ArrayList<JSONObject> data){
        eventData = data;
    }


    private void enableCardSwiping(){
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(mRecyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int i) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int i) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] ints) {
                                StyleableToast st = new StyleableToast(getContext(), "Removing...", Toast.LENGTH_SHORT);

                                for (int position : ints) {
                                    //eventData.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                    try {
                                        deleteEvent.put("user_id", mAdapter.getObject().get(position).getString("user_id"));
                                        deleteEvent.put("event_id", mAdapter.getObject().get(position).getString("event_id"));
                                        //Custom stylable toast*
                                        st.setBackgroundColor(Color.RED);
                                        st.setTextColor(Color.WHITE);
                                        st.setIcon(R.drawable.ic_delete_black_24dp);
                                        st.spinIcon();
                                        st.setMaxAlpha();
                                        st.show();
                                        eventData.remove(position);
                                        if(eventData!=null)
                                            mAdapter.setEventData(eventData);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mAdapter.notifyDataSetChanged();


                                }
                                new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_DELETED_EVENTS)).execute(deleteEvent);

                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] ints) {
                                StyleableToast st = new StyleableToast(getContext(), "Saving...", Toast.LENGTH_SHORT);

                                for (int position : ints) {
                                    //eventData.remove(position);
                                    mAdapter.notifyItemRemoved(position);

                                    try {
                                        saveEvent.put("user_id", mAdapter.getObject().get(position).getString("user_id"));
                                        saveEvent.put("event_id", mAdapter.getObject().get(position).getString("event_id"));
                                        //Custom stylable toast*
                                        st.setBackgroundColor(Color.parseColor("#ff9dfc"));
                                        st.setTextColor(Color.WHITE);
                                        st.setIcon(R.drawable.ic_save_black_24dp);
                                        st.spinIcon();
                                        st.setMaxAlpha();
                                        st.show();
                                        eventData.remove(position);
                                        if(eventData!=null)
                                            mAdapter.setEventData(eventData);
                                        //Toast.makeText(getContext(), mAdapter.getObject().get(position).getString("event_id"), Toast.LENGTH_SHORT).show();


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mAdapter.notifyDataSetChanged();

                                }

                                new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_SAVED_EVENTS)).execute(saveEvent);

                            }
                        });
        mRecyclerView.addOnItemTouchListener(swipeTouchListener);
    }
}
