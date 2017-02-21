package com.example.codenamebiscuit.rv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder>{
    private ArrayList<JSONObject> mEventData;
    private Context context;
    private Typeface typeface;
    private ClickListener clickListener = null;


    public EventAdapter(Context context) {
        this.context = context;
        mEventData = new ArrayList<>();
        typeface=Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeueBd.ttf");

    }


    @Override
    public EventAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = 0;
        layoutIdForListItem = R.layout.event_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new EventAdapterViewHolder(view);
    }


    public class EventAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, OnMapReadyCallback {

        public final TextView mEventPreferenceTV;
        public final TextView mEventLocationTV;
        public final ImageView mEventImage;
        public final ImageView mExpandedImage;
        public final TextView mEventName;
        public final TextView mEventAge;
        public final TextView mEventCost;
        public final CardView cardView;
        public final ImageButton imageButton;
        public final RelativeLayout layout;
        //public final MapView map;
        public GoogleMap googleMap;
        public int originalHeight=0;
        public boolean isViewExpanded=false;


        public EventAdapterViewHolder(final View view) {
            super(view);
            //map = (MapView) view.findViewById(R.id.map);
            //map.getMapAsync(this);

            mEventPreferenceTV = (TextView) view.findViewById(R.id.tv_event_preference);
            mEventPreferenceTV.setTypeface(typeface);

            mEventLocationTV = (TextView) view.findViewById(R.id.tv_event_location);
            mEventLocationTV.setTypeface(typeface);

            mEventName = (TextView) view.findViewById(R.id.tv_event_name);
            mEventName.setTypeface(typeface);

            mEventAge = (TextView)view.findViewById(R.id.age);
            mEventAge.setTypeface(typeface);

            mEventCost = (TextView)view.findViewById(R.id.cost);
            mEventCost.setTypeface(typeface);

            mExpandedImage = (ImageView)view.findViewById(R.id.collapseImage);

            layout = (RelativeLayout)view.findViewById(R.id.extend);


            mEventImage = (ImageView) view.findViewById(R.id.iv_event_image);
            cardView = (CardView) view.findViewById(R.id.cardview);
            imageButton = (ImageButton) view.findViewById(R.id.parent_list_item_expand_arrow);
            imageButton.setOnClickListener(this);
            //map.onCreate(null);

        }


        @Override
        public void onClick(final View v) {
            if(clickListener!=null){
                clickListener.itemClicked(v, getAdapterPosition());
            }
        }


        @Override
        public void onMapReady(GoogleMap googleMap) {
            this.googleMap=googleMap;

        }


    }


    @Override
    public void onBindViewHolder(final EventAdapterViewHolder eventAdapterViewHolder, int position) {

        String eventLocation = null;
        String eventPref = null;
        String eventPath = null;
        String event = null;
        String eventid = null;


        loadImage(eventAdapterViewHolder);
        try {
            eventLocation = mEventData.get(position).getString("event_location");
            eventPref = mEventData.get(position).getString("preference_name");
            eventPath = mEventData.get(position).getString("img_path");
            event = mEventData.get(position).getString("event_name");
            eventid = mEventData.get(position).getString("event_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        eventAdapterViewHolder.mEventPreferenceTV.setText(eventPref);
        eventAdapterViewHolder.mEventLocationTV.setText(eventLocation);
        eventAdapterViewHolder.mEventName.setText(event);
        Picasso.with(eventAdapterViewHolder.mEventImage.getContext().getApplicationContext())
                .load(getImageURL(eventPath))
                .resize(100, 100)
                .centerCrop()
                .into(loadImage(eventAdapterViewHolder));

        eventAdapterViewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "button clicked", Toast.LENGTH_SHORT).show();

                if (eventAdapterViewHolder.layout.getVisibility() == View.GONE) {
                    eventAdapterViewHolder.layout.setVisibility(View.VISIBLE);

                }

                else {
                    eventAdapterViewHolder.layout.setVisibility(View.GONE);

                }
                Log.i("click", "button clicked");
            }
        });
        //eventAdapterViewHolder.itemView.setOnClickListener(this);

    }
    public void setClickListener(ClickListener clickListener){
        this.clickListener=clickListener;
    }



    private Target loadImage(final EventAdapterViewHolder eventAdapterViewHolder) {

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                eventAdapterViewHolder.mEventImage.setImageBitmap(bitmap);

                Log.v("Success", "image created from url");
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                eventAdapterViewHolder.mEventImage.setImageResource(R.drawable.error);

                Log.e("Error", "Bitmap not created from URL");

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                eventAdapterViewHolder.mEventImage.setImageResource(R.drawable.placeholder);


            }
        };
        return target;
    }


    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if(mEventData.size()!=0)
            return mEventData.size();
        return 0;
    }


    /**
     * This method is used to set the event data if we haven't set it yet. This is handy when we
     * get new data from the web but don't want to create a new EventAdapter to display it.
     *
     * @param eventData The new weather data to be displayed.
     */
    public void setEventData(ArrayList<JSONObject> eventData) {
        mEventData = eventData;
        notifyDataSetChanged();
    }

    public String getImageURL(String path) {
        return "http://athena.ecs.csus.edu/~teamone/AndroidUploadImage/uploads/" + path;
    }

    public void clear() {
        mEventData.clear();
        notifyDataSetChanged();
    }

    //add a list of items
    public void addAll(ArrayList<JSONObject> list) {
        mEventData.addAll(list);
        notifyDataSetChanged();

    }

    public ArrayList<JSONObject> getObject() {
        return mEventData;
    }


}