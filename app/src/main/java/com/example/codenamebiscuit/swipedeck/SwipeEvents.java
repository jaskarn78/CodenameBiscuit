package com.example.codenamebiscuit.swipedeck;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.example.codenamebiscuit.helper.SaveEventsOnSwipe;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SwipeEvents extends AppCompatActivity {
    private final String IMAGE_URL_PATH = "http://athena.ecs.csus.edu/~teamone/AndroidUploadImage/uploads/";

    private SwipeDeck cardStack;
    private SwipeDeckAdapter adapter;
    private ArrayList<JSONObject> testData;
    private JSONObject saveEvent;
    private String image;
    private String event_id;
    private String event_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_swipe_events);
        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
        TextView eventid = (TextView) findViewById(R.id.event_id_num);
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
            public void cardSwipedRight(long stableId) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + stableId);
                Toast.makeText(getApplicationContext(), "Deleting Event...: ", Toast.LENGTH_SHORT).show();

            }

            @Override
            public boolean isDragEnabled(long itemId) {
                return true;
            }
        });
    }

    public String getImageURL(String path) {
        return IMAGE_URL_PATH + path;
    }


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
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;


            if (v == null) {
                LayoutInflater inflater = getLayoutInflater();
                // normally use a viewholder
                v = inflater.inflate(R.layout.cards, parent, false);
            }
            try {
                image = getImageURL(testData.get(position).getString("img_path"));
                event_id = testData.get(position).getString("event_id");
                String user_id = testData.get(position).getString("user_id");
                event_location = testData.get(position).getString("event_location");

                TextView event_location_tv = (TextView)v.findViewById(R.id.event_location_swipe);
                event_location_tv.setText("Location: "+ event_location);
                TextView textView = (TextView)v.findViewById(R.id.event_id_num);
                textView.setText("Event id: "+event_id);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            //((TextView) v.findViewById(R.id.textView2)).setText(data.get(position));
            ImageView imageView = (ImageView) v.findViewById(R.id.offer_image);
            Picasso.with(context).load(image).fit().centerCrop().into(imageView);


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Layer type: ", Integer.toString(v.getLayerType()));
                    Log.i("Hardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));
                    /*Intent i = new Intent(v.getContext(), BlankActivity.class);
                    v.getContext().startActivity(i);*/
                }
            });
            return v;
        }
    }
}

