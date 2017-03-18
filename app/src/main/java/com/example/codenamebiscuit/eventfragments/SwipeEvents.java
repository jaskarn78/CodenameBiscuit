package com.example.codenamebiscuit.eventfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.FlipAnimation;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.helper.UpdateDbOnSwipe;
import com.example.codenamebiscuit.swipedeck.SwipeDeck;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SwipeEvents extends android.support.v4.app.Fragment{

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
    private SharedPreferences pref;
    private JSONObject user;
    private ArrayList<JSONObject> data;
    private int lastPosition = -1;
    private FloatingActionButton floatingActionButton;
    MapView mapView;
    private GoogleMap googleMap;
    GetMainSwipeDataInterface sGetDataInterface;

    public interface GetMainSwipeDataInterface {
        ArrayList<JSONObject> getMainEventList();
        ArrayList<JSONObject> getUpdatedEventList();

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sGetDataInterface = (GetMainSwipeDataInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement GetDataInterface Interface");
        }
    }
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

        View rootView = inflater.inflate(R.layout.activity_swipe_events, container, false);
        cardStack = (SwipeDeck) rootView.findViewById(R.id.swipe_deck);

        //alter toolbar title
        TextView textView = (TextView)getActivity().findViewById(R.id.toolbar_title);
        textView.setText("LIV IT");

        return rootView;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupOnCreate(savedInstanceState);
    }

    /**
     * data is an arraylist passed through an interface from main activity
     * contains all event information for current user
     */
    @Override
    public void onResume() {
        super.onResume();
        try {
            data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER)).execute(user).get();
            adapter.setData(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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
        return getString(R.string.IMAGE_URL_PATH) + path;
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
                image = getImageURL(data.get(position).getString("img_path"));
                event_id = data.get(position).getString("event_id");
                String user_id = data.get(position).getString("user_id");
                event_location = data.get(position).getString("event_location");
                event_name = data.get(position).getString("event_name");
                event_preference = data.get(position).getString("preference_name");
                event_description = data.get(position).getString("event_description");
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


            ImageView frontCardImage = (ImageView) v.findViewById(R.id.offer_image);
            Picasso.with(context).load(image).placeholder(R.drawable.progress).into(frontCardImage);
            //Glide.with(SwipeEvents.this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL)
             //       .placeholder(R.drawable.progress).into(frontCardImage);

            final ImageView flippedCardImage = (ImageView) v.findViewById(R.id.back_image);
            Picasso.with(context).load(image).placeholder(R.drawable.progress).centerCrop().fit().into(flippedCardImage);
            //Glide.with(SwipeEvents.this).load(image).centerCrop()
            //        .placeholder(R.drawable.progress).into(flippedCardImage);

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
        private double getLat(){
            return Double.parseDouble(lat);
        }
        private double getLng(){
            return Double.parseDouble(lng);
        }
    }
}