package com.example.codenamebiscuit.eventfragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.R;
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
    private ArrayList<JSONObject> data;
    GetSavedDataInterface sGetDataInterface;

    public interface GetSavedDataInterface {
        ArrayList<JSONObject> getSavedEventList();
        ArrayList<JSONObject> getUpdatedSavedEventList();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sGetDataInterface = (GetSavedDataInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement GetDataInterface Interface");
        }
    }


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
            mRecyclerView.setItemViewCacheSize(80);
            mRecyclerView.setDrawingCacheEnabled(true);
            mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
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
        try {
            data = new QueryEventList(getString(R.string.DATABASE_SAVED_EVENTS_PULLER)).execute(currentUserId).get();
            mAdapter.setEventData(data);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //loadEventData();

    }


    @Override
    public void onStart() {
        super.onStart();
       // loadEventData();
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
                                return false;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] ints) {
                                for (int position : ints) {
                                    AlertDialog alert = AskOption(position);
                                    alert.show();
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
     * Alert dialog is prompted on each swipe to restore deleted or saved event
     * if 'restore' is clicked, the event will be removed from the saved/deleted event list
     * back to the main event list
     **********************************************************************************************/

    private AlertDialog AskOption(final int position)
    {
        final StyleableToast st = new StyleableToast(getActivity().getApplicationContext(), "Restoring...", Toast.LENGTH_SHORT);
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle("Remove")
                .setMessage("Do you want to remove this event from saved events?")
                .setIcon(R.drawable.ic_restore_black_24dp)

                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
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
                        data.remove(position);
                        mAdapter.setEventData(data);


                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }




    /**********************************************************************************************
     * HTTP request to run php script which contains sql command
     * to retrieve all event data filtered by user id
     **********************************************************************************************/
    private void loadEventData() {
        try {
            data =new QueryEventList(getString(R.string.DATABASE_SAVED_EVENTS_PULLER),
                    getContext()).execute(currentUserId).get();
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
