package com.example.codenamebiscuit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder> {
    private ArrayList<JSONObject> mEventData;

    public EventAdapter() {

    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public class EventAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mEventNameTV;
        public final TextView mEventLocationTV;

        public EventAdapterViewHolder(View view) {
            super(view);
            mEventNameTV = (TextView) view.findViewById(R.id.tv_event_name);
            mEventLocationTV = (TextView) view.findViewById(R.id.tv_event_location);
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
    public void onBindViewHolder(EventAdapterViewHolder eventAdapterViewHolder, int position) {
        String eventName = null;
        String eventLoc  = null;
        try {
            eventName = mEventData.get(position).getString("event_Name");
            eventLoc  = mEventData.get(position).getString("event_Location");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        eventAdapterViewHolder.mEventNameTV.setText(eventName);
        eventAdapterViewHolder.mEventLocationTV.setText(eventLoc);
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
}