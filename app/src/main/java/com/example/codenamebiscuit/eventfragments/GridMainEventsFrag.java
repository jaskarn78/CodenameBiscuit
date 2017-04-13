package com.example.codenamebiscuit.eventfragments;

/**
 * Created by jaskarnjagpal on 3/1/17.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.Events;
import com.example.codenamebiscuit.MainActivity;
import com.example.codenamebiscuit.MapActivity;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.FlipAnimation;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.helper.UpdateDbOnSwipe;
import com.example.codenamebiscuit.rv.ClickListener;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.mikepenz.iconics.view.IconicsImageView;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;
import com.wunderlist.slidinglayer.SlidingLayer;
import com.wunderlist.slidinglayer.transformer.AlphaTransformer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * Created by jaskarnjagpal on 2/23/17.
 */

public class GridMainEventsFrag extends Fragment  {
    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private JSONObject saveEvent, deleteEvent;
    private ArrayList<JSONObject> data;
    private String userId;
    private View mContentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveEvent = new JSONObject();
        deleteEvent = new JSONObject();
        userId = getArguments().getString("currentUserId");
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.activity_main, container, false);
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerview_events);
        return mContentView;
    }

    private void obtainData() {
        try {
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), userId).execute().get();
            Events.fromJson(data,getContext());
            mAdapter = new EventAdapter(getContext().getApplicationContext(), 2, "", getActivity());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); }
    }
    public ArrayList<JSONObject> getData(){
        return data;
    }

    public static GridMainEventsFrag newInstance() {
        return new GridMainEventsFrag();
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
        obtainData();

        mAdapter.setEventData(data);
        mRecyclerView.setAdapter(mAdapter);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemViewCacheSize(100);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        TextView textView = (TextView) getActivity().findViewById(R.id.toolbar_title);
        textView.setText("LIV IT");
        StyleableToast st = new StyleableToast(getContext(), "Network not detected!", Toast.LENGTH_LONG);

        st.setBackgroundColor(Color.RED);
        st.setTextColor(Color.WHITE);
        st.spinIcon();
        st.setMaxAlpha();
        RecyclerItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new RecyclerItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, final View view) {
                        final CardView cv = (CardView) view.findViewById(R.id.cardview);
                        final CardView cvBack = (CardView) view.findViewById(R.id.card_view_back);
                        final FlipAnimation flipAnimation = new FlipAnimation(cv, cvBack);
                        if (cv.getVisibility() == View.GONE)
                            flipAnimation.reverse();
                        view.startAnimation(flipAnimation); } });
        if (!isNetworkAvailable())
            st.show();
        enableCardSwiping(); }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
    }

    @Override
    public void onStart()
    {super.onStart(); }


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
                                    try {
                                        deleteEvent.put("user_id", mAdapter.getObject().get(position).getString("user_id"));
                                        deleteEvent.put("event_id", mAdapter.getObject().get(position).getString("event_id"));

                                        st.setBackgroundColor(Color.RED);
                                        st.setTextColor(Color.WHITE);
                                        st.setIcon(R.drawable.ic_delete_black_24dp);
                                        st.spinIcon();
                                        st.setMaxAlpha();
                                        st.show();
                                        data.remove(position);
                                        mAdapter.notifyItemRemoved(position);
                                        new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_DELETED_EVENTS)).execute(deleteEvent);
                                    } catch (JSONException e) {
                                        e.printStackTrace(); }
                                } }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] ints) {
                                StyleableToast st = new StyleableToast(getContext(), "Saving...", Toast.LENGTH_SHORT);

                                for (int position : ints) {
                                    mAdapter.notifyItemRemoved(position);
                                    try {
                                        saveEvent.put("user_id", mAdapter.getObject().get(position).getString("user_id"));
                                        saveEvent.put("event_id", mAdapter.getObject().get(position).getString("event_id"));
                                        st.setBackgroundColor(Color.parseColor("#ff9dfc"));
                                        st.setTextColor(Color.WHITE);
                                        st.setIcon(R.drawable.ic_save_black_24dp);
                                        st.spinIcon();
                                        st.setMaxAlpha();
                                        st.show();
                                        data.remove(position);
                                        new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_SAVED_EVENTS)).execute(saveEvent);
                                        mAdapter.notifyItemRemoved(position);

                                    } catch (JSONException e) {
                                        e.printStackTrace(); }
                                }
                            }
                        }); mRecyclerView.addOnItemTouchListener(swipeTouchListener); }

}
