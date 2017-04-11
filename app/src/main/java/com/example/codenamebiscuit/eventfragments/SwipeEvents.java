package com.example.codenamebiscuit.eventfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.FlipAnimation;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.helper.UpdateDbOnSwipe;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.example.codenamebiscuit.swipedeck.SwipeDeck;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.iconics.view.IconicsImageView;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wunderlist.slidinglayer.SlidingLayer;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SwipeEvents extends Fragment{

    private SwipeDeck cardStack;
    private SwipeDeckAdapter adapter;
    private JSONObject saveEvent, deleteEvent;
    private String image;
    private String event_id;
    private String event_location;
    private String event_preference;
    private String event_name;
    private String event_description;
    private String lat, lng;
    private String eventWebsite;
    private Bundle savedState;
    private SharedPreferences pref;
    private android.support.v7.widget.Toolbar toolbar;
    private LinearLayout linearLayoutContainer;
    private JSONObject user;
    private MenuItem item;
    private ArrayList<JSONObject> data;
    private int lastPosition = -1;
    MapView mapView;
    private GoogleMap googleMap;
    private View mContentView;
    private Handler mHandler;
    private SlidingLayer mSlidingLayer;
    private IconicsImageView downArrow;


    public static SwipeEvents newInstance() {
        SwipeEvents myFragment = new SwipeEvents();
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String user_id = pref.getString("user_id", null);
        user=new JSONObject();
        data = new ArrayList<>();
        try {
            user.put("user_id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        mContentView = inflater.inflate(R.layout.activity_swipe_events, container, false);
        cardStack = (SwipeDeck) mContentView.findViewById(R.id.swipe_deck);
        setHasOptionsMenu(true);
        mSlidingLayer = (SlidingLayer) getActivity().findViewById(R.id.slidingLayer1);
        downArrow = (IconicsImageView)getActivity().findViewById(R.id.down_arrow);

        //alter toolbar title
        TextView textView = (TextView)getActivity().findViewById(R.id.toolbar_title);
        textView.setText("LIV IT");
        return mContentView;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        obtainData();
        savedState = savedInstanceState;
        setupOnCreate(savedInstanceState);
    }
    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
    }

    private void obtainData(){
        try {
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER)).execute(user).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * data is an arraylist passed through an interface from main activity
     * contains all event information for current user
     */
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStart(){
        super.onStart();
    }

    /**
     * Moved initializations to sepearate method to clear up onCreate method
     * method initalizes the card stack view and loads event information from intent
     * event information is added into custom swipe deck adapter
     */
    private void setupOnCreate(Bundle savedInstanceState) {
        saveEvent = new JSONObject();
        deleteEvent=new JSONObject();

        adapter = new SwipeDeckAdapter(data, getActivity().getApplicationContext(), savedInstanceState);
        cardStack.setAdapter(adapter);
        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);


        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long stableId) {
                try {
                    StyleableToast st = new StyleableToast(getActivity().getApplicationContext(), "EVENT DELETED", Toast.LENGTH_SHORT);
                    st.setBackgroundColor(Color.parseColor("#ff9dfc"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_delete_white_24dp);
                    st.setMaxAlpha();
                    st.show();
                    deleteEvent.put("event_id", adapter.getItem((int)stableId).get("event_id"));
                    deleteEvent.put("user_id", adapter.getItem((int)stableId).get("user_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_DELETED_EVENTS)).execute(deleteEvent);

            }

            @Override
            public void cardSwipedRight(long stableId) {
                try {
                    StyleableToast st = new StyleableToast(getActivity().getApplicationContext(), "EVENT SAVED", Toast.LENGTH_SHORT);
                    st.setBackgroundColor(Color.parseColor("#ff9dfc"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_check_circle_white_24dp);
                    st.setMaxAlpha();
                    st.show();
                    saveEvent.put("event_id", adapter.getItem((int) stableId).get("event_id"));
                    saveEvent.put("user_id", adapter.getItem((int) stableId).get("user_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new UpdateDbOnSwipe(getString(R.string.DATABASE_STORE_SAVED_EVENTS)).execute(saveEvent);

            }

            @Override
            public boolean isDragEnabled(long itemId) {
                return true;
            }
        });
    }

    /**
     *
     * @param path
     * @return URL of image located on server
     */
    public String getImageURL(String path) {
        return getResources().getString(R.string.IMAGE_URL_PATH)+path;
    }


    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }





    /**
     * Assigns values to views within the cards
     * Handles flip animation to reveal additional event information
     * inflates layout cards.xml and assigns values to the views
     */
    public class SwipeDeckAdapter extends BaseAdapter {

        private List<JSONObject> data;
        private Context context;
        private Bundle savedInstanceState;

        public SwipeDeckAdapter(List<JSONObject> data, Context context, Bundle savedInstanceState) {
            this.data = data;
            this.context = context;
            this.savedInstanceState=savedInstanceState;
        }
        public void setData(List<JSONObject> eventData){
            data=eventData;
        }
        public List<JSONObject> getData(){
            return data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        public void clear(){
            data.clear();
        }

        @Override
        public JSONObject getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View v = convertView;

                LayoutInflater inflater = getActivity().getLayoutInflater();
                v = inflater.inflate(R.layout.cards, parent, false);
            try {
                image = data.get(position).getString("img_path");
                event_id = data.get(position).getString("event_id");
                String user_id = data.get(position).getString("user_id");
                event_location = data.get(position).getString("event_location");
                event_name = data.get(position).getString("event_name");
                event_preference = data.get(position).getString("preference_name");
                event_description = data.get(position).getString("event_description");
                eventWebsite = data.get(position).getString("event_website");
                lat = data.get(position).getString("lat");
                lng = data.get(position).getString("lng");
            } catch (JSONException e) {
                e.printStackTrace();}

            /**
             * initialize all views on the back side of the card
             * assign values to all views
             * event information retrieved from json array testData
             */
            mapView = (MapView)v.findViewById(R.id.mapView);

            final ProgressBar progressBarFront = (ProgressBar)v.findViewById(R.id.front_progress);
            final ProgressBar progressBarBack = (ProgressBar)v.findViewById(R.id.back_progress);
            ImageView frontCardImage = (ImageView) v.findViewById(R.id.offer_image);
            //Picasso.with(context).load(image).into(frontCardImage);
            Glide.with(SwipeEvents.this)
                    .load(getImageURL(image))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.progress)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBarFront.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBarFront.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(frontCardImage);

            Log.i("Image path", getImageURL(image));

            final ImageView flippedCardImage = (ImageView) v.findViewById(R.id.back_image);
            //Picasso.with(context).load(image).centerCrop().fit().into(flippedCardImage);
            Glide.with(SwipeEvents.this)
                    .load(getImageURL(image))
                    .centerCrop()
                    .placeholder(R.drawable.progress)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBarBack.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBarBack.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(flippedCardImage);

            TextView event_location_tv = (TextView) v.findViewById(R.id.display_event_location);
            event_location_tv.setText(event_location);

            TextView event_name_tv = (TextView) v.findViewById(R.id.event_name_back);
            event_name_tv.setText(event_name);

            TextView event_preference_tv = (TextView) v.findViewById(R.id.event_preference_back);
            event_preference_tv.setText(event_preference);

            TextView event_info = (TextView)v.findViewById(R.id.display_event_description);
            event_info.setText(event_description);

            final CardView cv = (CardView) v.findViewById(R.id.card_view);
            final CardView cvBack = (CardView) v.findViewById(R.id.card_view_back);
            final View finalV = v;
            flippedCardImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FlipAnimation flipAnimation = new FlipAnimation(cv, cvBack);
                    flipAnimation.reverse();
                    finalV.startAnimation(flipAnimation);
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Log.i("Layer type: ", Integer.toString(v.getLayerType()));
                    Log.i("Hardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));

                    //Picasso.with(context).load(R.drawable.liv1).fit().centerCrop().into(imageView);

                    FlipAnimation flipAnimation = new FlipAnimation(cv, cvBack);

                    if (cv.getVisibility() == View.GONE) {
                        flipAnimation.reverse();
                    }
                    v.startAnimation(flipAnimation);

                }
            });
            return v;
        }
        public String getImageURL(String path) {
            return context.getString(R.string.IMAGE_URL_PATH) + path; }
        private double getLat(){
            return Double.parseDouble(lat);
        }
        private double getLng(){
            return Double.parseDouble(lng);
        }
    }
}