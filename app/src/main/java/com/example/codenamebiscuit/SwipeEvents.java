package com.example.codenamebiscuit;


import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.FlipAnimation;
import com.example.codenamebiscuit.helper.SaveEventsOnSwipe;
import com.example.codenamebiscuit.swipedeck.SwipeDeck;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SwipeEvents extends AppCompatActivity{

    private SwipeDeck cardStack;
    private SwipeDeckAdapter adapter;
    private ArrayList<JSONObject> testData;
    private JSONObject saveEvent;
    private String image;
    private String event_id;
    private String event_location;
    private String event_preference;
    private String event_name;

    /**
     * A handler object, used for deferring UI operations.
     */
    private Handler mHandler = new Handler();

    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private boolean mShowingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_swipe_events);
        setupOnCreate();

    }


    /*Moved initializations to sepearate method
       method initalizes the card stack view and loads event information from intent
       event information is added into custom swipe deck adapter
    */
    private void setupOnCreate(){
        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
        //TextView eventid = (TextView) findViewById(R.id.event_id_num);
        saveEvent = new JSONObject();

        testData = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        String array = extras.getString("jArray");
        if (extras != null) {

            try {
                JSONArray jsonAr = new JSONArray(array);
                for (int i = 0; i < jsonAr.length(); i++) {
                    JSONObject jsonObj = jsonAr.getJSONObject(i);
                    testData.add(jsonObj);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter = new SwipeDeckAdapter(testData, this);

        if (cardStack != null) {
            cardStack.setAdapter(adapter);
        }

        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long stableId) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + stableId);

            }

            @Override
            public void cardSwipedRight(long stableId) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + stableId);
                try {
                    Toast.makeText(getApplicationContext(), "Saving Event ID...: "+adapter.getItem((int)stableId).getString("event_id"), Toast.LENGTH_SHORT).show();
                    saveEvent.put("event_id", adapter.getItem((int)stableId).get("event_id"));
                    saveEvent.put("user_id", adapter.getItem((int)stableId).get("user_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SaveEventsOnSwipe().execute(saveEvent);

            }

            @Override
            public boolean isDragEnabled(long itemId) {
                return true;
            }
        });
    }

    public String getImageURL(String path) {
        return getString(R.string.IMAGE_URL_PATH)+ path;
    }









    //Assigns values to views within the cards
    //Handles flip animation to reveal additional event information


    public class SwipeDeckAdapter extends BaseAdapter {

        private List<JSONObject> data;
        private Context context;


        public SwipeDeckAdapter(List<JSONObject> data, Context context) {
            this.data = data;
            this.context = context;
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


            if (v == null) {
                LayoutInflater inflater = getLayoutInflater();
                v = inflater.inflate(R.layout.cards, parent, false);
            }

            try {
                image = getImageURL(testData.get(position).getString("img_path"));
                event_id = testData.get(position).getString("event_id");
                String user_id = testData.get(position).getString("user_id");
                event_location = testData.get(position).getString("event_location");
                event_name = testData.get(position).getString("event_name");
                event_preference = testData.get(position).getString("event_preference");



            } catch (JSONException e) {
                e.printStackTrace();
            }
            //((TextView) v.findViewById(R.id.textView2)).setText(data.get(position));
            ImageView imageView = (ImageView) v.findViewById(R.id.offer_image);
            Picasso.with(context).load(image).fit().centerCrop().into(imageView);

            ImageView flippedCardImage = (ImageView)v.findViewById(R.id.back_image);
            Picasso.with(context).load(image).fit().centerCrop().into(flippedCardImage);

            TextView event_location_tv = (TextView)v.findViewById(R.id.event_location_back);
            event_location_tv.setText(event_location);

            TextView event_name_tv = (TextView)v.findViewById(R.id.event_name_back);
            event_name_tv.setText(event_name);

            TextView event_preference_tv = (TextView)v.findViewById(R.id.event_preference_back);
            event_preference_tv.setText(event_preference);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Log.i("Layer type: ", Integer.toString(v.getLayerType()));
                    Log.i("Hardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));

                    //Picasso.with(context).load(R.drawable.liv1).fit().centerCrop().into(imageView);
                    final CardView cv =(CardView) v.findViewById(R.id.card_view);
                    final CardView cvBack = (CardView)v.findViewById(R.id.card_view_back);


                    FlipAnimation flipAnimation = new FlipAnimation(cv, cvBack);

                    if (cv.getVisibility() == View.GONE)
                    {
                        flipAnimation.reverse();

                    }

                    v.startAnimation(flipAnimation);



                }
            });
            return v;
        }
    }
}

