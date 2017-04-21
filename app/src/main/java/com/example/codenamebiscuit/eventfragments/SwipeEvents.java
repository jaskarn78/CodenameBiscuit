package com.example.codenamebiscuit.eventfragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.Events;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.SwipePagerAdapter;
import com.example.codenamebiscuit.helper.EventBundle;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.example.codenamebiscuit.requests.UpdateDbOnSwipe;
import com.example.codenamebiscuit.swipedeck.SwipeDeck;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SwipeEvents extends ProgressFragment{

    private SwipeDeckAdapter adapter;
    private JSONObject saveEvent, deleteEvent;
    private String image; private String userId;
    private Bundle eventBundle;
    private EventBundle events;
    private TextView numOfEvents;
    private JSONObject user;
    private ArrayList<JSONObject> data;
    private View mContentView;
    private SwipeDeck cardStack;
    private Handler mHandler;
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
        setupOnCreate();}


    private void obtainData(){
        setContentShown(false);
        mHandler = new Handler();
        mHandler.postDelayed(mShowContentRunnable, 300);
        GridMainEventsFrag frag = (GridMainEventsFrag)getFragmentManager().findFragmentByTag("eventsFrag");
        if(frag!=null) data = frag.getData();
        else try {
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), userId).execute().get();
            Events.fromJson(data, getActivity());
        } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
        events = new EventBundle(data);
        adapter = new SwipeDeckAdapter(getContext()); adapter.setData(data);
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
            if(data.size()==0)
                numOfEvents.setText("Empty");
            else { numOfEvents.setText(currentPosition + "/" + data.size());
                loadBackgroundImage((int)cardStack.getTopCardItemId()+1); }

            cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
                @Override
                public void cardSwipedLeft(long stableId) {
                    try {
                        deleteEvent.put("event_id", adapter.getItem((int) stableId).get("event_id"));
                        deleteEvent.put("user_id", adapter.getItem((int) stableId).get("user_id"));
                        loadBackgroundImage((int)cardStack.getTopCardItemId());
                    } catch (JSONException e) { e.printStackTrace(); }
                    new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_DELETED_EVENTS)).execute(deleteEvent);
                    if(currentPosition!=data.size())
                        numOfEvents.setText((++currentPosition)+"/"+(data.size()));
                    else numOfEvents.setText("Empty"); }

                @Override
                public void cardSwipedRight(long stableId) {
                    try {
                        loadBackgroundImage((int)cardStack.getTopCardItemId());
                        saveEvent.put("event_id", adapter.getItem((int) stableId).get("event_id"));
                        saveEvent.put("user_id", adapter.getItem((int) stableId).get("user_id"));
                    } catch (JSONException e) { e.printStackTrace(); }
                    new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_SAVED_EVENTS)).execute(saveEvent);
                    if(currentPosition!=data.size())
                        numOfEvents.setText((++currentPosition)+"/"+(data.size()));
                    else numOfEvents.setText("Empty"); }

                @Override
                public boolean isDragEnabled(long itemId) {
                    return true;} });
        } }

        private void loadBackgroundImage(int imagePosition){
            try {
                if(imagePosition>=0) {
                    String background = getImageURL(data.get(imagePosition).getString("img_path"));
                    Glide.with(this).load(background).error(R.drawable.placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop().crossFade().into(swipeImage); }
                else Glide.with(this).load(R.drawable.livbg).crossFade().into(swipeImage);
            } catch (JSONException e) {e.printStackTrace(); } }


    /**
     * Assigns values to views within the cards
     * Handles flip animation to reveal additional event information
     * inflates layout cards.xml and assigns values to the views
     */
    public class SwipeDeckAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<JSONObject> data;

        public SwipeDeckAdapter(Context context) {
            this.context = context;
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
            try {
                image = getImageURL(data.get((int)getItemId(position)).getString("img_path"));
            } catch (JSONException e) {e.printStackTrace();}
            /**
             * initialize all views on the back side of the card
             * assign values to all views
             * event information retrieved from json array testData
             */
            eventBundle = events.getBundle((int)getItemId(position));
            ImageView frontCardImage = (ImageView) v.findViewById(R.id.offer_image);
            loadImage(frontCardImage, image);

            TextView eventName = (TextView)v.findViewById(R.id.slidename);
            eventName.setText(eventBundle.getString("eventName"));
            TextView eventHoster = (TextView)v.findViewById(R.id.slideHoster);
            eventHoster.setText(eventBundle.getString("eventHoster"));
            TextView eventLocation = (TextView)v.findViewById(R.id.slideLocation);
            eventLocation.setText(eventBundle.getString("eventLocation"));
            TextView eventPreference = (TextView)v.findViewById(R.id.slidePref);
            eventPreference.setText(eventBundle.getString("eventPreference"));
            TextView eventDate = (TextView)v.findViewById(R.id.slideDate);
            eventDate.setText(parseDate(eventBundle.getString("eventDate")));
            return v;
        }

    }
    private String parseDate(String dateString){
        dateString = dateString.replace('-', '/');
        return dateString.substring(5, 10);
    }

    private String getImageURL(String path) {
        return getActivity().getString(R.string.IMAGE_URL_PATH) + path;
    }

    private void loadImage(final ImageView imageView, String image) {
        Glide.with(SwipeEvents.this)
                .load(image).diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop().placeholder(R.drawable.progress).into(imageView);
    }

    public ArrayList<JSONObject> getData(){ return data; }
}