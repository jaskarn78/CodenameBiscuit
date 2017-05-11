package com.example.codenamebiscuit.eventfragments;

/**
 * Created by jaskarnjagpal on 3/1/17.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.helper.EventBundle;
import com.example.codenamebiscuit.helper.Events;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.example.codenamebiscuit.requests.RunQuery;
import com.example.codenamebiscuit.requests.UpdateDbOnSwipe;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikepenz.iconics.view.IconicsImageView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import br.com.mauker.materialsearchview.MaterialSearchView;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * Created by jaskarnjagpal on 2/23/17.
 */

public class GridMainEventsFrag extends ProgressFragment {
    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private JSONObject saveEvent, deleteEvent;
    private ArrayList<JSONObject> data;
    private String userId;
    private View fabMenuView;
    private MaterialSearchView searchView;
    private View mContentView;
    private MaterialSpinner toolbarSpinner;
    private LinearLayout bgImage;
    private EventBundle eventsBundle;
    private JSONObject preferences;
    private boolean showing;
    FABRevealMenu fabMenu;
    private boolean touched;
    private Runnable mShowContentRunnable = new Runnable() {
        @Override
        public void run() {if (isAdded()) {
            setContentShown(true);
            mRecyclerView.scrollToPosition(0);
            toolbarSpinner.setSelectedIndex(0);
        } } };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveEvent = new JSONObject();
        deleteEvent = new JSONObject();
        userId = getArguments().getString("currentUserId");
        data = new ArrayList<>();
        setHasOptionsMenu(true);}

    public ArrayList<JSONObject> getData(){ return data; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.activity_main, container, false);
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerview_events);
        bgImage = (LinearLayout) mContentView.findViewById(R.id.bgImage);
        TextView textView = (TextView)mContentView.findViewById(R.id.events_text);
        textView.setText("0 Upcoming Events Found");
        return super.onCreateView(inflater, container, savedInstanceState); }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater); }


    private void obtainData() {
        setContentShown(false);
        Handler mHandler = new Handler();
        mHandler.postDelayed(mShowContentRunnable, 600);
        try {
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), userId).execute().get();
            setupBgImage();
            Events.fromJson(data, getActivity());
            mAdapter = new EventAdapter(getContext().getApplicationContext(), 2, "", getActivity());
            mAdapter.setEventData(data); eventsBundle = new EventBundle(data);

        } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); } }


    private void setupBgImage(){
        if(data.isEmpty()) {
            bgImage.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "No Events Available...", Toast.LENGTH_SHORT).show();
        }else bgImage.setVisibility(View.GONE); }



    /**********************************************************************************************
     * sets up recycler view and assigns layout
     * assigns mEventAdapter which contains all event information retrieved from MySQL request
     * recycler view is assigned a linear layout
     **********************************************************************************************/
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbarSpinner = (MaterialSpinner)getActivity().findViewById(R.id.spinner);
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        bindViews();
        setContentView(mContentView);
        if (isAdded()) {
            obtainData();
            mRecyclerView.setAdapter(mAdapter);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setItemViewCacheSize(data.size());
            mRecyclerView.setDrawingCacheEnabled(true);
            mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            enableCardSwiping(); setupSpinner();
        } setupSearchView();
    }

    private void setupSearchView(){
        searchView.setShouldKeepHistory(false);
        searchView.adjustTintAlpha(0.8f);
        if(data.size()>0) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    searchView.clearSuggestions();
                    searchView.addSuggestions(eventsBundle.getEventStringList());} });
        }
        searchView.setCloseOnTintClick(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) { return false; }

            @Override
            public boolean onQueryTextChange(String s) { return false; } });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int dataPosition =  eventsBundle.getEventStringList().indexOf(searchView.getSuggestionAtPosition(position));
                searchView.setQuery(searchView.getSuggestionAtPosition(position), true);
                Bundle bundle = eventsBundle.getBundle(dataPosition);
                Intent intent = new Intent(getActivity(), DisplayEvent.class);
                intent.putExtras(bundle); getActivity().startActivity(intent); }});
    }


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
                                String suggestion = eventsBundle.getEventStringList().get(position);
                                searchView.removeSuggestion(suggestion);
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
                                String suggestion = eventsBundle.getEventStringList().get(position);
                                searchView.removeSuggestion(suggestion);
                                data.remove(position);
                                new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_SAVED_EVENTS)).execute(saveEvent);
                            } catch (JSONException e) {e.printStackTrace();} } }

                }); mRecyclerView.addOnItemTouchListener(swipeTouchListener);
    }

    private void bindViews() {
        ArrayList<FancyButton> btnList = new ArrayList<>();
        final FloatingActionButton fabReveal = (FloatingActionButton)getActivity().findViewById(R.id.fab_reveal);
        fabMenu = (FABRevealMenu)getActivity().findViewById(R.id.reveal);
        if(fabReveal!=null && fabMenu!=null){
            View customView = View.inflate(getActivity(), R.layout.preferences_layout,null);
            fabMenuView = customView;
            fabMenu.setCustomView(customView);
            fabMenu.bindAncherView(fabReveal); }
        IconicsImageView closeButton = (IconicsImageView)getActivity().findViewById(R.id.exit_icon);

        FancyButton musicFancyButton = (FancyButton) fabMenuView.findViewById(R.id.btn_music);
        FancyButton sportsButton = (FancyButton) fabMenuView.findViewById(R.id.btn_sports);
        FancyButton foodButton = (FancyButton) fabMenuView.findViewById(R.id.btn_food);
        FancyButton outdoorButton = (FancyButton) fabMenuView.findViewById(R.id.btn_outdoors);
        FancyButton healthButton = (FancyButton) fabMenuView.findViewById(R.id.btn_health);
        FancyButton entertainmentButton = (FancyButton) fabMenuView.findViewById(R.id.btn_entertainment);
        FancyButton charityButton = (FancyButton) fabMenuView.findViewById(R.id.btn_charity);
        FancyButton retailButton = (FancyButton) fabMenuView.findViewById(R.id.btn_retail);
        FancyButton familyButton = (FancyButton) fabMenuView.findViewById(R.id.btn_family);

        btnList = new ArrayList();
        btnList.add(musicFancyButton);btnList.add(foodButton); btnList.add(sportsButton);
        btnList.add(outdoorButton); btnList.add(healthButton); btnList.add(familyButton);
        btnList.add(retailButton); btnList.add(charityButton); btnList.add(entertainmentButton);
        setupPreferences(btnList);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                fabMenu.closeMenu();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        refresh();} }, 800);} });

    }

    public void onBackPressed(){
        mAdapter.getFancyShowCaseView().hide();
    }
    public boolean fancyViewShowing(){
        if(mAdapter.getFancyShowCaseView()!=null){
            if(mAdapter.getFancyShowCaseView().isShowing())
                showing=true;
            else showing=false;}
        return showing;
    }

    private void refresh(){
        if(touched) {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(fragment); ft.attach(fragment); ft.commitNow();
        }touched=false; }


    public void setupPreferences(final List<FancyButton> btnList) {
        try {
            preferences = new JSONObject();
            JSONObject removed = new JSONObject();
            preferences.put("user_id", userId);
            removed.put("user_id", userId);
            ArrayList<JSONObject> prefList = new QueryEventList(getString(R.string.PULL_USER_PREFERENCES), userId).execute().get();

            for (int i = 0; i < prefList.size(); i++) {
                if (Integer.parseInt(prefList.get(i).getString("preference_id")) > 0) {
                    btnList.get(Integer.parseInt(prefList.get(i).getString("preference_id")) - 1).setBackgroundColor(getActivity().getColor(R.color.livinPink));
                    btnList.get(Integer.parseInt(prefList.get(i).getString("preference_id")) - 1).setSelected(true);
                    preferences.put("pref_id" + (Integer.parseInt(prefList.get(i).getString("preference_id"))), 1);} }

        } catch (JSONException | InterruptedException | ExecutionException e) {e.printStackTrace(); }
        for (int i = 0; i < btnList.size(); i++) {
            final int finalI = i;
            btnList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touched = true;
                    if (!btnList.get(finalI).isSelected()) {
                        btnList.get(finalI).setBackgroundColor(getActivity().getColor(R.color.livinPink));
                        btnList.get(finalI).setSelected(true);
                        try { preferences.put("pref_id" + (finalI + 1), 1); }
                        catch (JSONException e) { e.printStackTrace(); }
                        new RunQuery(getString(R.string.PUSH_USER_PREFERENCES)).execute(preferences);}
                    else {
                        btnList.get(finalI).setBackgroundColor(getActivity().getColor(R.color.transparentPink));
                        btnList.get(finalI).setSelected(false);
                        try { preferences.put("pref_id" + (finalI + 1), 0);
                        } catch (JSONException e) { e.printStackTrace(); }
                        new RunQuery(getString(R.string.PUSH_USER_PREFERENCES)).execute(preferences);} } }); }

    }

    public  boolean getTouchedValue(){
        return touched;
    }


    private void setupSpinner(){
        toolbarSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, int i, long l, Object o) {
                switch (i) {
                    case 0:
                        Events.fromJson(data, getActivity());
                        mAdapter.setEventData(data);mAdapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(0);
                        break;
                    case 1:
                        Events.toFurthest(data);
                        mAdapter.setEventData(data);mAdapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(0);
                        break;
                    case 2:
                        Events.toEarliest(data);
                        mAdapter.setEventData(data);mAdapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(0);
                        break;
                    case 3:
                        Events.toLatest(data);
                        mAdapter.setEventData(data); mAdapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(0);
                        break;
                    default: materialSpinner.setSelectedIndex(0);
                        mRecyclerView.smoothScrollToPosition(0);
                        break;}}});

    }

}
