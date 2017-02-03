package com.example.codenamebiscuit.rv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder> {
    private ArrayList<JSONObject> mEventData;

    public EventAdapter() {
    }


    public class EventAdapterViewHolder extends RecyclerView.ViewHolder {
        //public final TextView mEventNameTV;
        public final TextView mEventPreferenceTV;
        public final TextView mEventLocationTV;
        public final ImageView mEventImage;
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
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public EventAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.event_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new EventAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param eventAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(final EventAdapterViewHolder eventAdapterViewHolder, int position) {
        String eventLocation = null;
        String eventPref  = null;
        String eventPath = null;
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
        try {
            eventLocation = mEventData.get(position).getString("event_location");
            eventPref  = mEventData.get(position).getString("preference_name");
            eventPath  = mEventData.get(position).getString("img_path");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //eventAdapterViewHolder.mEventNameTV.setText(eventName);
        eventAdapterViewHolder.mEventPreferenceTV.setText(eventPref);
        eventAdapterViewHolder.mEventLocationTV.setText(eventLocation);
        Picasso.with(eventAdapterViewHolder.mEventImage.getContext()).
                load(getImageURL(eventPath)).into(target);


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
    public JSONObject getObject(int position){
        return mEventData.get(position);
    }
}