package com.example.codenamebiscuit.eventfragments;

/**
 * Created by jaskarnjagpal on 3/1/17.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.MapActivity;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.FlipAnimation;
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

public class GridMainEventsFrag extends ProgressFragment implements ClickListener {

    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private ArrayList<JSONObject> eventData;
    private LinearLayoutManager mLinearLayoutManager;
    private SharedPreferences pref;
    private JSONObject currentUserId = new JSONObject();
    private SwipeRefreshLayout swipeContainer;
    private JSONObject saveEvent, deleteEvent;
    private ArrayList<JSONObject> data;
    private View mContentView;
    private Handler mHandler;
    private Runnable mShowContentRunnable = new Runnable() {

        @Override
        public void run() {
            setContentShown(true);
        }

    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


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
        setHasOptionsMenu(true);

        eventData = new ArrayList<JSONObject>();
        mAdapter = new EventAdapter(getContext().getApplicationContext(), 2, "", getFragmentManager(), getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        mContentView = inflater.inflate(R.layout.activity_main, container, false);

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    private void obtainData(){
        mHandler = new Handler();
        mHandler.postDelayed(mShowContentRunnable, 100);
        try {
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER)).execute(currentUserId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public static GridMainEventsFrag newInstance() {
        GridMainEventsFrag myFragment = new GridMainEventsFrag();
        return myFragment;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.map_action).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("latList", mAdapter.getLatsArrayList());
                bundle.putStringArrayList("lngList", mAdapter.getLngsArrayList());
                bundle.putStringArrayList("nameList", mAdapter.getEventNameList());
                bundle.putStringArrayList("imageList", mAdapter.getEventImageList());
                bundle.putStringArrayList("descList", mAdapter.getEventDescList());
                bundle.putStringArrayList("hosterList", mAdapter.getHosterArrayList());
                bundle.putStringArrayList("costList", mAdapter.getCostArrayList());
                bundle.putStringArrayList("startList", mAdapter.getEventStartList());
                bundle.putStringArrayList("timeList", mAdapter.getEventTimeList());
                bundle.putStringArrayList("prefList", mAdapter.getEventPrefList());
                bundle.putStringArrayList("locationList", mAdapter.getEventLocationList());
                bundle.putIntegerArrayList("distanceList", mAdapter.getEventDistanceList());

                Fragment fragment = new MapActivity();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.enter, R.anim.exit);
                ft.replace(R.id.fragment_container, fragment);
                ft.addToBackStack(null);
                ft.commit();
                getFragmentManager().executePendingTransactions();
                return false;
            }
        });
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
        //initialize event dataset
        obtainData();
        setContentShown(false);

        swipeContainer = (SwipeRefreshLayout) mContentView.findViewById(R.id.swipeContainer);
        setupSwipeDownRefresh();
        mRecyclerView = (RecyclerView)mContentView.findViewById(R.id.recyclerview_events);

        mAdapter.setEventData(data);
        mRecyclerView.setAdapter(mAdapter);


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemViewCacheSize(200);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        TextView textView = (TextView)getActivity().findViewById(R.id.toolbar_title);
        textView.setText("LIV IT");
        StyleableToast st = new StyleableToast(getContext(), "Network not detected!", Toast.LENGTH_LONG);
        //Custom stylable toast*
        st.setBackgroundColor(Color.RED);
        st.setTextColor(Color.WHITE);
        st.spinIcon();
        st.setMaxAlpha();
        RecyclerItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new RecyclerItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                        Toast.makeText(getActivity().getApplicationContext(), "CLick" + position, Toast.LENGTH_SHORT).show();
                        final CardView cv = (CardView) view.findViewById(R.id.cardview);
                        final CardView cvBack = (CardView) view.findViewById(R.id.card_view_back);
                        FlipAnimation flipAnimation = new FlipAnimation(cv, cvBack);
                        if (cv.getVisibility() == View.GONE)
                            flipAnimation.reverse();
                        view.startAnimation(flipAnimation); }
                });

        if(!isNetworkAvailable())
            st.show();

        enableCardSwiping();
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
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();

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
                    data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER)).execute(currentUserId).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                mAdapter.setEventData(data);
                swipeContainer.setRefreshing(false); }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light); }

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
            layout.setVisibility(View.GONE); }

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
            e.printStackTrace(); }
    }

    public void setEventData(ArrayList<JSONObject> dataGrid){
       data = dataGrid;
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

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mAdapter.notifyDataSetChanged();}
                                new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_DELETED_EVENTS)).execute(deleteEvent);}

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
                                        mAdapter.notifyItemRemoved(position);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mAdapter.notifyDataSetChanged();}
                                new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_SAVED_EVENTS)).execute(saveEvent);
                            }
                        });
        mRecyclerView.addOnItemTouchListener(swipeTouchListener);
    }
}
