package com.example.codenamebiscuit.rv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.eventfragments.DisplayEvent;
import com.example.codenamebiscuit.eventfragments.GridMainEventsFrag;
import com.example.codenamebiscuit.requests.UpdateDbOnSwipe;
import com.mikepenz.iconics.view.IconicsImageView;
import com.wunderlist.slidinglayer.SlidingLayer;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder> {
    private ArrayList<JSONObject> mEventData;
    private Context context;
    private String message;
    private int type;
    private ImageView rowMenu;
    private Activity activity;
    private Bundle bundle;
    private View rootView;
    private Drawable eventImageDrawable;
    private SharedPreferences sharedPreferences;
    ArrayList<Bundle> bundleList = new ArrayList<>();


    public EventAdapter(Context context, int type, String message, Activity activity) {
        this.context = context;
        mEventData = new ArrayList<>();
        this.message=message;
        this.type=type;
        this.activity=activity;
        bundle=new Bundle();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }


    @Override
    public EventAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = 0;
        if(type==1)
            layoutIdForListItem = R.layout.event_list_item;
        else if(type==2)
            layoutIdForListItem=R.layout.grid_events;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new EventAdapterViewHolder(view);
    }




    public void setBundle(Bundle bundle){
        this.bundle=bundle;
        bundleList.add(this.bundle); }


    public Bundle getBundle(){
        return this.bundle;
    }


    public Bundle getBundleAtposition(int position){
        return bundleList.get(position); }

    public void removeBundleAtPosition(int position){
        bundleList.remove(position);}
    public ArrayList<Bundle> getBundleList(){
        return bundleList;
    }


    @Override
    public int getItemCount() {
        if(mEventData==null)
            return 0;
        else
            return mEventData.size();}


    public void setEventData(ArrayList<JSONObject> eventData) {
        mEventData = eventData;
        notifyDataSetChanged();
    }

    public void setupDeleteButton(final JSONObject restoreEvent, final int position, final Drawable image, final String text){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.restore_event, null);
        final CheckBox dialogCheckbox = (CheckBox)dialogView.findViewById(R.id.checkbox_event);
        final ImageView dialogImage = (ImageView)dialogView.findViewById(R.id.dialog_image);
        final TextView dialogEvent = (TextView)dialogView.findViewById(R.id.dialog_event_name);
        dialogBuilder.setView(dialogView);

        try {
            restoreEvent.put("user_id", mEventData.get(position).get("user_id"));
            restoreEvent.put("event_id", mEventData.get(position).get("event_id"));
        } catch (JSONException e) { e.printStackTrace();}

        if(!sharedPreferences.getBoolean("showDialog", false)) {
            dialogImage.setImageDrawable(image);
            dialogEvent.setText(text);
            dialogBuilder.setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mEventData.remove(position); notifyItemRemoved(position); notifyItemRangeChanged(position, mEventData.size());
                    if (message.equals("saved"))
                        new UpdateDbOnSwipe(context.getString(R.string.DATABASE_RESTORE_SAVED_EVENTS)).execute(restoreEvent);
                    else
                        new UpdateDbOnSwipe(context.getString(R.string.DATABASE_RESTORE_DELETED_EVENTS)).execute(restoreEvent);
                    dialog.dismiss(); }
            });

            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); }
            });

            dialogCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sharedPreferences.edit().putBoolean("showDialog", isChecked).apply(); } });
            AlertDialog alertDialog = dialogBuilder.create(); alertDialog.show();

        }else{
            mEventData.remove(position); notifyItemRemoved(position);
            notifyItemRangeChanged(position, mEventData.size());
            if (message.equals("saved"))
                new UpdateDbOnSwipe(context.getString(R.string.DATABASE_RESTORE_SAVED_EVENTS)).execute(restoreEvent);
            else
                new UpdateDbOnSwipe(context.getString(R.string.DATABASE_RESTORE_DELETED_EVENTS)).execute(restoreEvent); }
    }

    public String getImageURL(String path) {
        return context.getString(R.string.IMAGE_URL_PATH) + path; }

    public void loadImage(final ImageView imageView, final String URL, final ProgressBar progressBar) {
        Glide.with(imageView.getContext())
                .load(URL)
                .error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model,
                                               com.bumptech.glide.request.target.Target<GlideDrawable> target,
                                               boolean isFirstResource) { progressBar.setVisibility(View.GONE);
                        return false; }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   com.bumptech.glide.request.target.Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) { progressBar.setVisibility(View.GONE);
                        return false; } }).into(imageView);
        eventImageDrawable = imageView.getDrawable();
    }

    public void clear() {
        mEventData.clear();
        notifyDataSetChanged(); }

    //add a list of items
    public void addAll(ArrayList<JSONObject> list) {
        mEventData.addAll(list);
        notifyDataSetChanged(); }


    public ArrayList<JSONObject> getObject() { return mEventData;}

    public Drawable getEventImage(){ return eventImageDrawable;}

    public class EventAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        public final TextView mEventPreferenceTV, mEventLocationTV;
        public final TextView mEventName, mEventAge;
        public final TextView mEventStartDate, mEventStartTime;
        public final TextView mEventHoster, mEventDistance;
        public final TextView mEventCost;
        public final ImageView mEventImage;
        public final CardView cardView;
        public final WebView mWebView;

        SwipeLayout mSwipeLayout;
        Button buttonDelete;


        public EventAdapterViewHolder(View view) {
            super(view);
            rootView = view;
            rowMenu = (ImageView)view.findViewById(R.id.row_menu);

            mEventDistance = (TextView)view.findViewById(R.id.event_distance);
            mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);

            buttonDelete = (Button) itemView.findViewById(R.id.delete);
            mEventPreferenceTV = (TextView) view.findViewById(R.id.tv_event_preference);

            mEventLocationTV = (TextView) view.findViewById(R.id.tv_event_location);
            mEventName = (TextView) view.findViewById(R.id.tv_event_name);

            mEventAge = (TextView) view.findViewById(R.id.age);
            mEventCost = (TextView) view.findViewById(R.id.cost);

            mEventStartDate = (TextView)view.findViewById(R.id.start_date);
            mEventStartTime = (TextView)view.findViewById(R.id.start_time);

            mEventHoster = (TextView)view.findViewById(R.id.tv_event_hoster);
            mEventImage = (ImageView) view.findViewById(R.id.iv_event_image);

            cardView = (CardView) view.findViewById(R.id.cardview);
            mWebView = (WebView)view.findViewById(R.id.webView);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(final View v) {
        }


        @Override
        public boolean onLongClick(View v) {
            return true;
        }}


    @Override
    public void onBindViewHolder(final EventAdapterViewHolder eventAdapterViewHolder, final int position) {
        String eventLocation = null;
        String eventPref = null;
        String eventPath = null;
        String event = null;
        String eventInfo = "";
        String startDate = null;
        String startTime = null;
        String eventid = null;
        String userId = null;
        String eventHoster = null;
        String cost = null;
        Double lat = 0.0;
        String eventWebsite = null;
        int eventDistance = 0;
        Double lng = 0.0;

        final JSONObject restoreEvent = new JSONObject();
        JSONObject eventObject = mEventData.get(position);

        try {
            eventLocation = eventObject.getString("event_location");
            eventPref = eventObject.getString("preference_name");
            eventPath = eventObject.getString("img_path");
            event = eventObject.getString("event_name");
            eventHoster = eventObject.getString("event_sponsor");
            cost = eventObject.getString("event_cost");
            startDate = eventObject.getString("start_date");
            startTime = eventObject.getString("start_time");
            eventid = eventObject.getString("event_id");
            userId = eventObject.getString("event_id");
            lat = eventObject.getDouble("lat");
            lng = eventObject.getDouble("lng");
            eventInfo = eventObject.getString("event_description");
            eventWebsite = eventObject.getString("event_website");
            eventDistance = eventObject.getInt("event_distance");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final Bundle bundle = new Bundle();
        bundle.putString("eventName", event);
        bundle.putString("eventImage", eventPath);
        bundle.putString("eventDate", startDate);
        bundle.putString("eventTime", startTime);
        bundle.putString("eventLocation", eventLocation);
        bundle.putString("eventPreference", eventPref);
        bundle.putString("eventDescription", eventInfo);
        bundle.putString("eventHoster", eventHoster);
        bundle.putString("eventDistance", eventDistance + "");
        bundle.putString("eventCost", cost);
        bundle.putString("eventId", eventid);
        bundle.putDouble("eventLat", lat);
        bundle.putDouble("eventLng", lng);
        bundle.putString("eventWebsite", eventWebsite);
        setBundle(bundle);

        if (type == 1) {
            eventAdapterViewHolder.mEventHoster.setText("Presented By: " + eventHoster);
            eventAdapterViewHolder.mEventCost.setText("Entry Fee: $" + cost);
            eventAdapterViewHolder.mEventPreferenceTV.setText(eventPref);
            eventAdapterViewHolder.mEventLocationTV.setText(eventLocation);
            eventAdapterViewHolder.mEventName.setText(event);


            /***************************************************************************************
             * Handles the swipe to restore operation in saved events and deleted events
             ***************************************************************************************/
            eventAdapterViewHolder.mSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            eventAdapterViewHolder.mSwipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                    YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                }
            });

            /********************************************************************************
             * Loads event image from url obtained from database and assigns
             * the loaded image to the event imageview in the layout
             ******************************************************************************/
            final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
            loadImage(eventAdapterViewHolder.mEventImage, getImageURL(eventPath), progressBar);


            /*****************************************************************************************
             * Swiping on the cardview will display a remove button, when pressed
             * swiped event will be removed from saved/deleted events and placed into
             * the main events list
             ****************************************************************************************/
            eventAdapterViewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setupDeleteButton(restoreEvent, position, eventAdapterViewHolder.mEventImage.getDrawable(),
                            eventAdapterViewHolder.mEventName.getText().toString());
                }
            });
        }

        /*****************************************************************************************
         * Type 2 represents the grid layout and its corresponding views are set
         * ****************************************************************************************/
        if (type == 2) {
            final ProgressBar progressBar2 = (ProgressBar) rootView.findViewById(R.id.grid_progress);
            loadImage(eventAdapterViewHolder.mEventImage, getImageURL(eventPath), progressBar2);
        } }

}