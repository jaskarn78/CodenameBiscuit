package com.example.codenamebiscuit.eventfragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.Events;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.SwipePagerAdapter;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.example.codenamebiscuit.requests.UpdateDbOnSwipe;
import com.example.codenamebiscuit.swipedeck.SwipeDeck;
import com.google.android.gms.maps.MapView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.wunderlist.slidinglayer.SlidingLayer;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SwipeEvents extends ProgressFragment{

    private SwipeDeckAdapter adapter;
    private JSONObject saveEvent, deleteEvent;
    private String image; private String userId;
    private JSONObject user;
    private ArrayList<JSONObject> data;
    private FloatingActionButton fab;
    private View mContentView;
    private ImageView eventImageBottom;
    private SwipeDeck cardStack;
    private MaterialSpinner toolbarSpinner;
    private ExpandableLayout expandableLayout;
    private Handler mHandler;
    private ViewPager viewPager;
    private SwipePagerAdapter swipePagerAdapter;
    private Runnable mShowContentRunnable = new Runnable() {
        @Override
        public void run() { if(isAdded()) { setContentShown(true); } } };

    public static SwipeEvents newInstance()  {return new SwipeEvents();}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId= getArguments().getString("currentUserId");
        user=new JSONObject();
        data = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        setHasOptionsMenu(true);
        mContentView = inflater.inflate(R.layout.activity_swipe_events, container, false);
        return super.onCreateView(inflater, container, savedInstanceState); }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager = (ViewPager)mContentView.findViewById(R.id.event_pager);
        swipePagerAdapter = new SwipePagerAdapter(getContext());
        eventImageBottom = (ImageView)getActivity().findViewById(R.id.slideImage);
        toolbarSpinner = (MaterialSpinner)getActivity().findViewById(R.id.spinner);
        expandableLayout = (ExpandableLayout)getActivity().findViewById(R.id.expandable_layout);
        setupOnCreate();
        setupSpinner();}


    private void obtainData(){
        setContentShown(false);
        mHandler = new Handler();
        mHandler.postDelayed(mShowContentRunnable, 100);
        GridMainEventsFrag frag = (GridMainEventsFrag)getFragmentManager().findFragmentByTag("eventsFrag");
        if(frag!=null) data = frag.getData();
        else try {
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), userId).execute().get();
            Events.fromJson(data, getActivity());
        } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }


        adapter = new SwipeDeckAdapter(getContext());
        cardStack = (SwipeDeck) mContentView.findViewById(R.id.swipe_deck);
        cardStack.setAdapter(adapter); swipePagerAdapter.setPagerData(data);
        viewPager.setAdapter(swipePagerAdapter);
    }


    /**
     * Moved initializations to sepearate method to clear up onCreate method
     * method initalizes the card stack view and loads event information from intent
     * event information is added into custom swipe deck adapter
     */
    private void setupOnCreate() {
        saveEvent = new JSONObject();
        deleteEvent=new JSONObject();
        if(isAdded()) {
            setContentView(mContentView);
            obtainData();
            cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
                @Override
                public void cardSwipedLeft(long stableId) {
                    try {
                        deleteEvent.put("event_id", adapter.getItem((int) stableId).get("event_id"));
                        deleteEvent.put("user_id", adapter.getItem((int) stableId).get("user_id"));
                    } catch (JSONException e) { e.printStackTrace(); }
                    new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_DELETED_EVENTS)).execute(deleteEvent); }

                @Override
                public void cardSwipedRight(long stableId) {
                    try {
                        saveEvent.put("event_id", adapter.getItem((int) stableId).get("event_id"));
                        saveEvent.put("user_id", adapter.getItem((int) stableId).get("user_id"));
                    } catch (JSONException e) { e.printStackTrace(); }
                    new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_SAVED_EVENTS)).execute(saveEvent); }

                @Override
                public boolean isDragEnabled(long itemId) { return true;}
            });
            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true; } });
        } }


    /**
     * Assigns values to views within the cards
     * Handles flip animation to reveal additional event information
     * inflates layout cards.xml and assigns values to the views
     */
    public class SwipeDeckAdapter extends BaseAdapter {
        private Context context;

        public SwipeDeckAdapter(Context context)
        { this.context = context; }

        @Override
        public int getCount() { return data.size(); }

        public void clear(){ data.clear(); }

        @Override
        public JSONObject getItem(int position) { return data.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View v;
            LayoutInflater inflater = getActivity().getLayoutInflater();
            v = inflater.inflate(R.layout.cards, parent, false);
            viewPager.setCurrentItem(position-1);
            try {image = getImageURL(data.get(position).getString("img_path")); } catch (JSONException e) { e.printStackTrace();}
            /**
             * initialize all views on the back side of the card
             * assign values to all views
             * event information retrieved from json array testData
             */
            final ProgressBar progress = (ProgressBar) v.findViewById(R.id.card_progress);
            ImageView frontCardImage = (ImageView) v.findViewById(R.id.offer_image);
            loadImage(frontCardImage, progress);
            return v; }

        private String getImageURL(String path) {
            return context.getString(R.string.IMAGE_URL_PATH) + path; }

        private void loadImage(ImageView imageView, final ProgressBar progress){
            Glide.with(SwipeEvents.this)
                    .load(image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.progress)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progress.setVisibility(View.GONE);
                            return false; }
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progress.setVisibility(View.GONE);
                            return false;} }) .into(imageView); }
    }

    private Bundle getBundle(int position) {
        Bundle bundle = new Bundle();
        try {
            bundle.putString("eventName", data.get(position).getString("event_name"));
            bundle.putString("eventImage", data.get(position).getString("img_path"));
            bundle.putString("eventDate", data.get(position).getString("start_date"));
            bundle.putString("eventHoster", data.get(position).getString("event_sponsor"));
            bundle.putString("eventDistance", data.get(position).getString("event_distance"));
            bundle.putString("eventPreference", data.get(position).getString("preference_name"));
            bundle.putString("eventDescription", data.get(position).getString("event_description"));
            bundle.putString("eventLocation", data.get(position).getString("event_location"));
            bundle.putString("eventCost", data.get(position).getString("event_cost"));
            bundle.putString("eventTime", data.get(position).getString("start_time"));
            bundle.putString("eventId", data.get(position).getString("event_id"));
            bundle.putDouble("eventLat", data.get(position).getDouble("lat"));
            bundle.putDouble("eventLng", data.get(position).getDouble("lng"));
            bundle.putString("eventWebsite", data.get(position).getString("event_website"));
        } catch (JSONException e) { e.printStackTrace(); }
        return bundle; }
    public ArrayList<JSONObject> getData(){
        return data;
    }
    private void setupSpinner(){
        toolbarSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, int i, long l, Object o) {
                switch (i) {
                    case 0:
                        Events.fromJson(data, getActivity());
                        materialSpinner.setSelectedIndex(i);break;
                    case 1:
                        Events.toFurthest(data);
                        materialSpinner.setSelectedIndex(i);break;
                    case 2:
                        Events.toEarliest(data);
                        materialSpinner.setSelectedIndex(i);break;
                    case 3:
                        Events.toLatest(data);
                        materialSpinner.setSelectedIndex(i);break;
                    case 4: expandableLayout.expand();
                        materialSpinner.setSelectedIndex(0);break;
                }}});
        toolbarSpinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner materialSpinner) {
                materialSpinner.setSelectedIndex(materialSpinner.getSelectedIndex());
            }
        });
    }

}