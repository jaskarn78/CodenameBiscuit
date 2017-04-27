package com.example.codenamebiscuit.eventfragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.Events;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.EventBundle;
import com.example.codenamebiscuit.helper.ImageLoader;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.example.codenamebiscuit.requests.UpdateDbOnSwipe;
import com.example.codenamebiscuit.swipedeck.SwipeDeck;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;



public class SwipeEvents extends ProgressFragment{

    private SwipeDeckAdapter adapter;
    private JSONObject saveEvent, deleteEvent;
    private String image; private String userId;
    private EventBundle events;
    private TextView numOfEvents;
    private ArrayList<JSONObject> data;
    private View mContentView;
    private SwipeDeck cardStack;
    private int currentPosition;
    private ImageView swipeImage;
    private Runnable mShowContentRunnable = new Runnable() {
        @Override
        public void run() { if(isAdded()) { setContentShown(true); } } };

    public static SwipeEvents newInstance(Bundle bundle)  {
        SwipeEvents swipeEvents = new SwipeEvents();
        swipeEvents.setArguments(bundle);
        return swipeEvents;}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId= getArguments().getString("currentUserId");
        data = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        setHasOptionsMenu(false);
        mContentView = inflater.inflate(R.layout.activity_swipe_events, container, false);
        return super.onCreateView(inflater, container, savedInstanceState); }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupOnCreate();}


    private void obtainData(){
        setContentShown(false);
        Handler mHandler = new Handler();
        mHandler.postDelayed(mShowContentRunnable, 300);
        GridMainEventsFrag frag = (GridMainEventsFrag)getFragmentManager().findFragmentByTag("eventsFrag");
        if(frag!=null) data = frag.getData();
        else try {
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), userId).execute().get();
            Events.fromJson(data, getActivity());
        } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
        events = new EventBundle(data);
        adapter = new SwipeDeckAdapter(); adapter.setData(data);
        cardStack = (SwipeDeck) mContentView.findViewById(R.id.swipe_deck);
        swipeImage = (ImageView)mContentView.findViewById(R.id.swipeBackground);
        numOfEvents = (TextView)getActivity().findViewById(R.id.toolbar_title);
        cardStack.setAdapter(adapter);
    }


    /**
     * Moved initializations to sepearate method to clear up onCreate method
     * method initalizes the card stack view and loads event information from intent
     * event information is added into custom swipe deck adapter
     */
    private void setupOnCreate() {
        saveEvent = new JSONObject(); deleteEvent=new JSONObject();
        if(isAdded()) {
            currentPosition=1;
            setContentView(mContentView);
            obtainData();
            numOfEvents = (TextView)getActivity().findViewById(R.id.toolbar_title);
            if(data.size()==0)  numOfEvents.setText("Empty");
            else { numOfEvents.setText(currentPosition + "/" + data.size());
                loadBackgroundImage((int)cardStack.getTopCardItemId()+1); }

            cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
                @Override
                public void cardSwipedLeft(long stableId) {
                    try {
                        Snackbar.make(getContentView(), "Event Removed: "+adapter.getItem((int) stableId).get("event_id"), Snackbar.LENGTH_SHORT).show();
                        deleteEvent.put("event_id", adapter.getItem((int) stableId).get("event_id"));
                        deleteEvent.put("user_id", adapter.getItem((int) stableId).get("user_id"));
                        loadBackgroundImage((int)cardStack.getTopCardItemId());
                    } catch (JSONException e) { e.printStackTrace(); }
                    new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_DELETED_EVENTS)).execute(deleteEvent);
                    if(currentPosition!=data.size())
                        numOfEvents.setText((++currentPosition)+"/"+(data.size()));
                    else numOfEvents.setText("Empty");}

                @Override
                public void cardSwipedRight(long stableId) {
                    try {
                        Snackbar.make(getContentView(), "Event Saved: "+adapter.getItem((int) stableId).get("event_id"), Snackbar.LENGTH_SHORT).show();
                        loadBackgroundImage((int)cardStack.getTopCardItemId());
                        saveEvent.put("event_id", adapter.getItem((int) stableId).get("event_id"));
                        saveEvent.put("user_id", adapter.getItem((int) stableId).get("user_id"));
                    } catch (JSONException e) { e.printStackTrace(); }
                    new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_SAVED_EVENTS)).execute(saveEvent);
                    if(currentPosition!=data.size())
                        numOfEvents.setText((++currentPosition)+"/"+(data.size()));
                    else numOfEvents.setText("Empty");}

                @Override
                public boolean isDragEnabled(long itemId) { return true;} });
        } }

        private void loadBackgroundImage(int imagePosition){
            try {
                if(imagePosition>=0) {
                    String background = data.get(imagePosition).getString("img_path");
                    ImageLoader.loadBackgroundImage(getContext(), background, swipeImage);
                }
                else ImageLoader.loadBackgroundResource(getContext(), R.drawable.livbg, swipeImage);
            } catch (JSONException e) {e.printStackTrace(); } }


    /**********************************************************************************************************
     * Assigns values to views within the cards
     * Handles flip animation to reveal additional event information
     * inflates layout cards.xml and assigns values to the views
     ***********************************************************************************************************/
    public class SwipeDeckAdapter extends BaseAdapter {
        private ArrayList<JSONObject> data;

        public SwipeDeckAdapter() {
            data = new ArrayList<>(); }

        public void setData(ArrayList<JSONObject> data) {
            clear();
            this.data = data;
            notifyDataSetChanged(); }

        @Override
        public int getCount() {
            return data.size(); }

        public void clear() {
            data.clear(); }

        @Override
        public JSONObject getItem(int position) {
            return data.get(position); }

        @Override
        public long getItemId(int position) {
            return position; }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View v;
            LayoutInflater inflater = getActivity().getLayoutInflater();
            v = inflater.inflate(R.layout.cards, parent, false);
            try { image = data.get((int)getItemId(position)).getString("img_path");
            } catch (JSONException e) {e.printStackTrace();}
            /**
             * initialize all views on the back side of the card
             * assign values to all views
             * event information retrieved from json array testData
             */
            Bundle eventBundle = events.getBundle((int) getItemId(position));
            ImageView frontCardImage = (ImageView) v.findViewById(R.id.offer_image);
            FloatingActionButton swipeButton = (FloatingActionButton)v.findViewById(R.id.swipeButton);
            final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.swipeProgress);
            //loadImage(frontCardImage, image, progressBar);
            ImageLoader.loadImageFitCenter(getActivity(), image, frontCardImage, progressBar);


            TextView eventName = (TextView)v.findViewById(R.id.slidename);
            eventName.setText(eventBundle.getString("eventName"));
            TextView eventHoster = (TextView)v.findViewById(R.id.slideHoster);
            eventHoster.setText("Presented By: "+eventBundle.getString("eventHoster"));
            TextView eventLocation = (TextView)v.findViewById(R.id.slideLocation);
            eventLocation.setText(eventBundle.getString("eventLocation"));
            TextView eventPreference = (TextView)v.findViewById(R.id.slidePref);
            eventPreference.setText(eventBundle.getString("eventPreference"));
            TextView eventDate = (TextView)v.findViewById(R.id.slideDate);
            eventDate.setText(parseDate(eventBundle.getString("eventDate")));
            swipeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DisplayEvent.class);
                    intent.putExtras(events.getBundle((int)getItemId(position))); getContext().startActivity(intent);
                }
            }); return v;
        }

    }
    private String parseDate(String dateString){
        dateString = dateString.replace('-', '/');
        return dateString.substring(5, 10);
    }


    public ArrayList<JSONObject> getData(){ return data; }
}