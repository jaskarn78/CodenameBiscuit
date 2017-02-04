package com.example.codenamebiscuit.rv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder> {
    private ArrayList<JSONObject> mEventData;
    private Context context;

    public EventAdapter(Context context) {
        this.context=context;
    }


    public class EventAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    , View.OnLongClickListener{
        //public final TextView mEventNameTV;
        public final TextView mEventPreferenceTV;
        public final TextView mEventLocationTV;
        public final ImageView mEventImage;
        public final TextView mEventName;
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mEventImage.setImageBitmap(bitmap);

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                mEventImage.setImageResource(R.drawable.error);

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                mEventImage.setImageResource(R.drawable.placeholder);
                Toast.makeText(mEventImage.getContext(),
                        "Loading Event Images...", Toast.LENGTH_SHORT).show();

            }
        };

        public EventAdapterViewHolder(View view) {
            super(view);
           // mEventNameTV = (TextView) view.findViewById(R.id.tv_event_name);
            mEventPreferenceTV = (TextView) view.findViewById(R.id.tv_event_preference);
            mEventLocationTV = (TextView)view.findViewById(R.id.tv_event_location);
            mEventImage = (ImageView)view.findViewById(R.id.iv_event_image);
            mEventName = (TextView)view.findViewById(R.id.tv_event_name);

            view.setOnClickListener(this);
            mEventImage.setOnClickListener(this);
            mEventImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() ==mEventImage.getId()){
                Toast.makeText(v.getContext(), "ITEM PRESSED = "+
                        String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(v.getContext(), "ROW PRESSED = "+
                String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }


    @Override
    public EventAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.event_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new EventAdapterViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final EventAdapterViewHolder eventAdapterViewHolder, int position) {
        String eventLocation = null;
        String eventPref  = null;
        String eventPath = null;
        String event = null;
        loadImage(eventAdapterViewHolder);
        try {
            eventLocation = mEventData.get(position).getString("event_location");
            eventPref  = mEventData.get(position).getString("preference_name");
            eventPath  = mEventData.get(position).getString("img_path");
            event = mEventData.get(position).getString("event_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //eventAdapterViewHolder.mEventNameTV.setText(eventName);
        eventAdapterViewHolder.mEventPreferenceTV.setText(eventPref);
        eventAdapterViewHolder.mEventLocationTV.setText(eventLocation);
        eventAdapterViewHolder.mEventName.setText(event);
        Picasso.with(eventAdapterViewHolder.mEventImage.getContext().getApplicationContext())
                .load(getImageURL(eventPath)).into(loadImage(eventAdapterViewHolder));


    }

    private Target loadImage(final EventAdapterViewHolder eventAdapterViewHolder){

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
        if (null == mEventData) return 0;
        return mEventData.size();
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
    public String getImageURL(String path){
        return "http://athena.ecs.csus.edu/~teamone/AndroidUploadImage/uploads/"+path;
    }
    public void clear(){
        mEventData.clear();
        notifyDataSetChanged();
    }
    //add a list of items
    public void addAll(ArrayList<JSONObject> list){
        mEventData.addAll(list);
        notifyDataSetChanged();

    }
    public ArrayList<JSONObject> getObject(){
        return mEventData;
    }

}