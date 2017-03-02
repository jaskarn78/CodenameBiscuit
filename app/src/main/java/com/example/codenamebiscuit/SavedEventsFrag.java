package com.example.codenamebiscuit;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.helper.UpdateDbOnSwipe;
import com.example.codenamebiscuit.rv.ClickListener;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by jaskarnjagpal on 2/23/17.
 */

public class SavedEventsFrag extends Fragment implements ClickListener {
    private static final String TAG = "Saved Events Fragment";

    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private ArrayList<JSONObject> eventData;
    private SharedPreferences pref;
    private JSONObject currentUserId = new JSONObject();
    private SwipeRefreshLayout swipeContainer;
    private JSONObject restoreEvent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        restoreEvent = new JSONObject();
        String user_id = pref.getString("user_id", null);
        try {
            currentUserId.put("user_id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //initialize event dataset

        eventData = new ArrayList<JSONObject>();
        /*Custom stylable toast*
        StyleableToast st = new StyleableToast(getContext(), "Loading Saved Events...Please Wait", Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#ff9dfc"));
        st.setTextColor(Color.WHITE);
        st.setIcon(R.drawable.ic_autorenew_white_24dp);
        st.spinIcon();
        st.setMaxAlpha();
        st.show();*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        setupSwipeDownRefresh();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_events);

        return rootView;

    }

    /**********************************************************************************************
     * sets up recycler view and assigns layout
     * assigns mEventAdapter which contains all event information retrieved from MySQL request
     * recycler view is assigned a linear layout
     **********************************************************************************************/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            TextView tv = (TextView) getActivity().findViewById(R.id.toolbar_title);
            tv.setText("Saved Events");

            mAdapter = new EventAdapter(getActivity().getApplicationContext(), 1);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            mRecyclerView.setAdapter(mAdapter);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);


            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemViewCacheSize(40);
            mRecyclerView.setDrawingCacheEnabled(true);
            mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            enableCardSwiping();


        //mAdapter.setClickListener(getContext());
    }

    /**********************************************************************************************
     * When activity resumes after a pause, check to see if any new events have been added
     * set swipeContainer.setRefreshing to true
     * load the event data
     * set swipecontainer.setRefreshing to false
     **********************************************************************************************/
    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        loadEventData();

    }

    @Override
    public void onStart() {
        super.onStart();
        loadEventData();
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

    private void enableCardSwiping() {
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
                                StyleableToast st = new StyleableToast(getActivity().getApplicationContext(), "Restoring...", Toast.LENGTH_SHORT);
                                for (int position : ints) {
                                    //eventData.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                    try {
                                        restoreEvent.put("user_id", mAdapter.getObject().get(position).get("user_id"));
                                        restoreEvent.put("event_id", mAdapter.getObject().get(position).getString("event_id"));
                                        Log.i("postion", position+"");
                                        //Custom stylable toast*

                                       // mAdapter.setEventData(eventData);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    st.setBackgroundColor(Color.GREEN);
                                    st.setTextColor(Color.WHITE);
                                    st.spinIcon();
                                    st.setMaxAlpha();
                                    st.show();
                                    eventData.remove(position);
                                    mAdapter.setEventData(eventData);


                                }
                                new UpdateDbOnSwipe(getString(R.string.DATABASE_RESTORE_SAVED_EVENTS)).execute(restoreEvent);

                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] ints) {

                            }
                        });
        mRecyclerView.addOnItemTouchListener(swipeTouchListener);
    }




    /**********************************************************************************************
     * HTTP request to run python script which contains sql command
     * to retrieve all event data filtered by user id
     **********************************************************************************************/
    private void loadEventData() {
        QueryEventList list = (QueryEventList)
                new QueryEventList(getString(R.string.DATABASE_SAVED_EVENTS_PULLER),
                        getContext()).execute(currentUserId);
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
    public void setEventData(ArrayList<JSONObject> data){
        eventData = data;
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
            layout.setVisibility(View.GONE);}

}
