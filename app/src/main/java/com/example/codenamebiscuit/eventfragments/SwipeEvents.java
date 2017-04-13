package com.example.codenamebiscuit.eventfragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.example.codenamebiscuit.Events;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.FlipAnimation;
import com.example.codenamebiscuit.helper.ItemClickSupport;
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

public class SwipeEvents extends ProgressFragment{

    private SwipeDeckAdapter adapter;
    private JSONObject saveEvent, deleteEvent;
    private String image;
    private String userId;
    private JSONObject user;
    private ArrayList<JSONObject> data;
    private ViewPager event_pager;
    private View mContentView;
    private Bundle bundle;
    MapView mapView;
    private Handler mHandler;
    private Runnable mShowContentRunnable = new Runnable() {
        @Override
        public void run() {
            setContentShown(true);
        }
    };


    public static SwipeEvents newInstance() {
        return new SwipeEvents();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId= getArguments().getString("currentUserId");
        user=new JSONObject();
        data = new ArrayList<>();
        bundle=new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        setHasOptionsMenu(true);

        mContentView = inflater.inflate(R.layout.activity_swipe_events, container, false);
        TextView textView = (TextView)getActivity().findViewById(R.id.toolbar_title);
        textView.setText("LIV IT");
        return super.onCreateView(inflater, container, savedInstanceState);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupOnCreate();
    }
    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
    }

    private void obtainData(){
        setContentShown(false);
        mHandler = new Handler();
        mHandler.postDelayed(mShowContentRunnable, 900);
        try {
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), userId).execute().get();
            Events.fromJson(data,getContext());
            adapter = new SwipeDeckAdapter(getContext());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); }
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
    private void setupOnCreate() {
        saveEvent = new JSONObject();
        deleteEvent=new JSONObject();

        setContentView(mContentView);
        obtainData();
        SwipeDeck cardStack = (SwipeDeck) mContentView.findViewById(R.id.swipe_deck);
        cardStack.setAdapter(adapter);

        event_pager = (ViewPager)mContentView.findViewById(R.id.event_pager);
        event_pager.setAdapter(new CardPagerAdapter(data, getContext().getApplicationContext()));

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
        event_pager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {return true;} });

    }


    @Override
    public void onStop() {
        super.onStop();
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
        private Context context;

        public SwipeDeckAdapter(Context context) {
            this.context = context;
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
            View v;
                LayoutInflater inflater = getActivity().getLayoutInflater();
                v = inflater.inflate(R.layout.cards, parent, false);
            try {
                image = getImageURL(data.get(position).getString("img_path"));
                event_pager.setCurrentItem(position-1);
            } catch (JSONException e) {
                e.printStackTrace();}

            /**
             * initialize all views on the back side of the card
             * assign values to all views
             * event information retrieved from json array testData
             */
            mapView = (MapView)v.findViewById(R.id.mapView);
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
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progress.setVisibility(View.GONE);
                            return false;}
                    })
                    .into(imageView); } }

    private class CardPagerAdapter extends PagerAdapter{
        Context context;
        ArrayList<JSONObject> data;

        public CardPagerAdapter(ArrayList<JSONObject> data, Context context){
            this.context=context;
            this.data=data;
        }
        @Override
        public View instantiateItem(ViewGroup container, final int position){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.cards_pager, null);
            TextView eventName = (TextView)itemView.findViewById(R.id.tv_event_name);
            TextView eventLocation = (TextView)itemView.findViewById(R.id.tv_event_location);
            TextView eventPreference = (TextView)itemView.findViewById(R.id.tv_event_preference);
            TextView eventHoster = (TextView)itemView.findViewById(R.id.tv_event_hoster);
            ImageView eventImage = (ImageView)itemView.findViewById(R.id.pager_event_image);
            ImageView menuImage = (ImageView)itemView.findViewById(R.id.pager_menu);
            JSONObject object = data.get(event_pager.getCurrentItem());

            try {
                String imagePager= getImageURL(data.get(position).getString("img_path"));
                eventName.setText(data.get(position).getString("event_name"));
                eventLocation.setText(data.get(position).getString("event_location"));
                eventPreference.setText(data.get(position).getString("preference_name"));
                eventHoster.setText("Presented By: "+data.get(event_pager.getCurrentItem()).getString("event_sponsor"));

                setBundle(bundle, object);

                Glide.with(SwipeEvents.this).load(imagePager).placeholder(R.drawable.progress).into(eventImage); }
            catch (JSONException e) { e.printStackTrace();}

            menuImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DisplayEvent.class);
                    intent.putExtras(getBundle());
                    context.startActivity(intent); }
            });
            container.addView(itemView);
            return itemView; }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private String getImageURL(String path) {
            return context.getString(R.string.IMAGE_URL_PATH) + path; }
    }

    private void setBundle(Bundle extra, JSONObject object) throws JSONException {
        bundle=extra;
        bundle.putString("eventDescription", object.getString("event_description"));
        bundle.putString("eventPreference", object.getString("preference_name"));
        bundle.putString("eventHoster", object.getString("event_sponsor"));
        bundle.putString("eventLocation", object.getString("event_location"));
        bundle.putString("eventId", object.getString("event_id"));
        bundle.putString("eventName", object.getString("event_name"));
        bundle.putString("eventImage", object.getString("img_path"));
        bundle.putString("eventTime", object.getString("start_time"));
        bundle.putString("eventDate", object.getString("start_date"));
        bundle.putString("eventCost", object.getString("event_cost"));
        bundle.putDouble("eventLat", object.getDouble("lat"));
        bundle.putDouble("eventLng", object.getDouble("lng"));
        bundle.putString("eventWebsite", object.getString("event_website"));
        bundle.putString("eventDistance", object.getString("event_distance"));
    }

    private Bundle getBundle(){
        return bundle;
    }
}