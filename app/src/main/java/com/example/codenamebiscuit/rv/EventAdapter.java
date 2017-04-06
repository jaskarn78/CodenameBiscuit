package com.example.codenamebiscuit.rv;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Typeface;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.CalendarContract;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.eventfragments.DisplayEvent;
import com.example.codenamebiscuit.helper.FlipAnimation;
import com.example.codenamebiscuit.helper.GPSTracker;
import com.example.codenamebiscuit.helper.UpdateDbOnSwipe;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.mikepenz.iconics.view.IconicsImageView;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thebrownarrow.model.MyLocation;

import net.colindodd.toggleimagebutton.ToggleImageButton;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder> {
    private ArrayList<JSONObject> mEventData;
    private Context context;
    private Typeface typeface;
    private String message;
    private int type;
    private FragmentManager fragmentManager;
    private FragmentManager childFragmentManager;
    private int lastPosition = -1;
    private ImageView rowMenu;
    private Activity activity;
    private Bundle eventBundle;
    private Bundle bundle;
    private View rootView;

    private static ArrayList<String> latsArrayList = new ArrayList<>();
    private static ArrayList<String> lngsArrayList = new ArrayList<>();
    private static ArrayList<String> eventNameList = new ArrayList<>();
    private static ArrayList<String> eventImageList = new ArrayList<>();
    private static ArrayList<String> eventDescList = new ArrayList<>();

    private static ArrayList<String> hosterArrayList = new ArrayList<>();
    private static ArrayList<String> costArrayList = new ArrayList<>();
    private static ArrayList<String> eventStartList = new ArrayList<>();
    private static ArrayList<String> eventTimeList = new ArrayList<>();
    private static ArrayList<String> eventPrefList = new ArrayList<>();
    private static ArrayList<String> eventLocationList = new ArrayList<>();

    private static ArrayList<Integer> eventDistanceList = new ArrayList<>();



    public EventAdapter(Context context, int type, String message,
                        FragmentManager fragmentManager, Activity activity) {
        this.context = context;
        mEventData = new ArrayList<>();
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Black.ttf");
        this.message=message;
        this.type=type;
        this.fragmentManager=fragmentManager;
        this.activity=activity;
        this.childFragmentManager = childFragmentManager;
        eventBundle = new Bundle();
        bundle=new Bundle();
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
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new EventAdapterViewHolder(view);
    }


    public class EventAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        public final TextView mEventPreferenceTV, mEventPreferenceTVBack;
        public final TextView mEventLocationTV, mEventLocationTVBack;
        public final TextView mEventName, mEventNameBack;
        public final TextView mEventAge, mEventAgeBack;
        public final TextView mEventCost, mEventCostBack;
        public final ImageView mEventImage, mEventImageback;
        public final TextView mEventStartDate, mEventStartTime;
        public final TextView mEventInfoBack, mEventInfo;
        public final ImageButton mSaveImageButton, mLinkImageButton;
        public final ImageButton mShareButton;
        public final TextView mEventHoster, mEventDistanceBack;
        public final TextView mEventDistance;
        public final CardView cardView;
        public IconicsImageView infoIcon;
        public TextView moreInfo;
        public final RelativeLayout layout;
        public final WebView mWebView;
        SwipeLayout mSwipeLayout;
        Button buttonDelete;


        public EventAdapterViewHolder(View view) {
            super(view);

            rootView = view;
            rowMenu = (ImageView)view.findViewById(R.id.row_menu);

            moreInfo = (TextView) view.findViewById(R.id.more_info);

            infoIcon = (IconicsImageView)view.findViewById(R.id.info_icon);

            mEventDistance = (TextView)view.findViewById(R.id.event_distance);
            mEventDistanceBack = (TextView)view.findViewById(R.id.event_distance_back);
            mEventInfo = (TextView)view.findViewById(R.id.extra_info);

            mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            buttonDelete = (Button) itemView.findViewById(R.id.delete);

            mEventPreferenceTV = (TextView) view.findViewById(R.id.tv_event_preference);
            mEventPreferenceTVBack = (TextView) view.findViewById(R.id.event_preference_back);

            mEventLocationTV = (TextView) view.findViewById(R.id.tv_event_location);
            mEventLocationTVBack = (TextView) view.findViewById(R.id.event_location_back);

            mEventName = (TextView) view.findViewById(R.id.tv_event_name);
            mEventNameBack = (TextView) view.findViewById(R.id.event_name_back);

            mEventAge = (TextView) view.findViewById(R.id.age);
            mEventAgeBack = (TextView)view.findViewById(R.id.event_age_back);

            mEventCost = (TextView) view.findViewById(R.id.cost);
            mEventCostBack = (TextView)view.findViewById(R.id.event_cost_back);

            mEventStartDate = (TextView)view.findViewById(R.id.start_date);
            mEventStartTime = (TextView)view.findViewById(R.id.start_time);

            mSaveImageButton = (ImageButton)view.findViewById(R.id.saveButton);
            mShareButton = (ImageButton)view.findViewById(R.id.shareButton);

            mEventInfoBack = (TextView)view.findViewById(R.id.event_info_back);
            mEventImageback = (ImageView)view.findViewById(R.id.iv_event_image_back);

            mEventHoster = (TextView)view.findViewById(R.id.tv_event_hoster);
            layout = (RelativeLayout) view.findViewById(R.id.extend);

            mEventImage = (ImageView) view.findViewById(R.id.iv_event_image);
            cardView = (CardView) view.findViewById(R.id.cardview);

            mWebView = (WebView)view.findViewById(R.id.webView);
            mLinkImageButton = (ImageButton)view.findViewById(R.id.linkButton);

            //itemView.setOnClickListener(this);
            //itemView.setOnLongClickListener(this);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            //this.layout.setOnClickListener(this);
            //.cardView.setOnLongClickListener(this);

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
        String eventLocation = null;    String eventPref = null;    String eventPath = null;
        String event = null;            String eventInfo = "";      String startDate=null;
        String startTime=null;          String eventid = null;      String userId=null;
        String eventHoster=null;        String cost=null;           Double lat=0.0;
        Double lng=0.0;                 final JSONObject restoreEvent = new JSONObject();


        try {
            eventLocation = mEventData.get(position).getString("event_location");
            eventPref = mEventData.get(position).getString("preference_name");
            eventPath = mEventData.get(position).getString("img_path");
            event = mEventData.get(position).getString("event_name");
            eventHoster=mEventData.get(position).getString("event_sponsor");
            cost=mEventData.get(position).getString("event_cost");
            startDate=mEventData.get(position).getString("start_date");
            startTime=mEventData.get(position).getString("start_time");
            eventid = mEventData.get(position).getString("event_id");
            userId=mEventData.get(position).getString("event_id");
            lat = mEventData.get(position).getDouble("lat");
            lng=mEventData.get(position).getDouble("lng");
            eventInfo = mEventData.get(position).getString("event_description");

            eventNameList.add(event);
            eventImageList.add(getImageURL(eventPath));
            eventDescList.add(eventInfo);
            latsArrayList.add(lat+"");
            lngsArrayList.add(lng+"");
            eventDistanceList.add(getLocation(lat,lng));

            hosterArrayList.add(eventHoster);
            costArrayList.add(cost);
            eventStartList.add(startDate);
            eventTimeList.add(startTime);
            eventPrefList.add(eventPref);
            eventLocationList.add(eventLocation);

        } catch (JSONException e) {
            e.printStackTrace(); }


        eventAdapterViewHolder.mEventPreferenceTV.setText(eventPref);
        eventAdapterViewHolder.mEventLocationTV.setText(eventLocation);
        eventAdapterViewHolder.mEventName.setText(event);

        final String finalEvent = event;
        final String finalEventInfo1 = eventInfo;
        final String finalEventLocation = eventLocation;

        final Bundle bundle = new Bundle();
        bundle.putString("eventName", event);
        bundle.putString("eventImage", eventPath);
        bundle.putString("eventDate", startDate);
        bundle.putString("eventTime", startTime);
        bundle.putString("eventLocation", eventLocation);
        bundle.putString("eventPreference", eventPref);
        bundle.putString("eventDescription", eventInfo);
        bundle.putString("eventHoster", eventHoster);
        bundle.putString("eventDistance", getLocation(lat, lng)+" mi");
        bundle.putString("eventCost", cost);
        bundle.putString("eventId", eventid);
        bundle.putDouble("eventLat", lat);
        bundle.putDouble("eventLng", lng);
        setBundle(bundle);

        if(type==1) {
            setAnimation(eventAdapterViewHolder.itemView, position);
            rowMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(activity, v);
                    popupMenu.getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.info:
                                    Intent intent = new Intent(activity, DisplayEvent.class);
                                    intent.putExtras(bundle);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                                    context.startActivity(intent);
                            }
                            return true; }
                    });
                }
            });

            /********************************************************************************
             * assigns distance from current location to event location
             *******************************************************************************/
            //eventAdapterViewHolder.mEventDistance.setText("Distance: " + getLocation(lat, lng) + " miles");

            /********************************************************************************
             * Loads event image from url obtained from database and assigns
             * the loaded image to the event imageview in the layout
             ******************************************************************************/

            loadImage(eventAdapterViewHolder.mEventImage, getImageURL(eventPath));
            eventAdapterViewHolder.mEventInfo.setText("Additional Event Information");

            /********************************************************************************
             * Implements add to calendar funcitonality, will add event to
             * internal mobile calendar with the event name, date, time, and
             * event description
             *******************************************************************************/
            eventAdapterViewHolder.mSaveImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance();
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra("beginTime", cal.getTimeInMillis());
                    intent.putExtra("allDay", false);
                    intent.putExtra("rrule", "FREQ=DAILY");
                    intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                    intent.putExtra("title", finalEvent);
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, finalEventLocation);
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, finalEventInfo1);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

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

            /*****************************************************************************************
             * Swiping on the cardview will display a remove button, when pressed
             * swiped event will be removed from saved/deleted events and placed into
             * the main events list
             ****************************************************************************************/
            eventAdapterViewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        restoreEvent.put("user_id", mEventData.get(position).get("user_id"));
                        restoreEvent.put("event_id", mEventData.get(position).get("event_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(activity)
                            .setTextTitle(eventAdapterViewHolder.mEventName.getText()+"")
                            .setImageDrawable(eventAdapterViewHolder.mEventImage.getDrawable().getCurrent())
                            .setTextSubTitle("Restore Event")
                            .setBody("Restore this event back to main events list")
                            .setPositiveButtonText("Continue")
                            .setPositiveColor(R.color.livinPink)
                            .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                                @Override
                                public void OnClick(View view, Dialog dialog) {
                                    mEventData.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, mEventData.size());
                                    if (message.equals("saved"))
                                        new UpdateDbOnSwipe(context.getString(R.string.DATABASE_RESTORE_SAVED_EVENTS)).execute(restoreEvent);
                                    else
                                        new UpdateDbOnSwipe(context.getString(R.string.DATABASE_RESTORE_DELETED_EVENTS)).execute(restoreEvent);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButtonText("Cancel")
                            .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                                @Override
                                public void OnClick(View view, Dialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .build();
                    alert.show();


                }
            });

            eventAdapterViewHolder.mEventHoster.setText("Presented By: " + eventHoster);
            eventAdapterViewHolder.mEventCost.setText("Entry Fee: $" + cost);
            eventAdapterViewHolder.mWebView.getSettings().setJavaScriptEnabled(true);
            eventAdapterViewHolder.mWebView.getSettings().setLoadWithOverviewMode(true);
            eventAdapterViewHolder.mWebView.getSettings().setUseWideViewPort(true);
            eventAdapterViewHolder.mWebView.setVerticalScrollBarEnabled(true);
            eventAdapterViewHolder.mWebView.setHorizontalScrollBarEnabled(true);
            eventAdapterViewHolder.mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, final String url) {
                }
            });




            /*****************************************************************************************
             * Clicking on the link icon in the expanded cardview will
             * open a webpage with the specified url. Event poster can post
             * ticket sale link along with other event information
             ****************************************************************************************/
            eventAdapterViewHolder.mLinkImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventAdapterViewHolder.mWebView.loadUrl("https://www.google.com");
                    if (eventAdapterViewHolder.mWebView.getVisibility() == View.GONE) {
                        eventAdapterViewHolder.mWebView.setVisibility(View.VISIBLE);
                        StyleableToast st = new StyleableToast(context.getApplicationContext(), "Loading Website...", Toast.LENGTH_SHORT);
                        st.setBackgroundColor(context.getColor(R.color.livinPink));
                        st.setTextColor(context.getColor(R.color.livinWhite));
                        st.show();
                    } else {
                        eventAdapterViewHolder.mWebView.setVisibility(View.GONE);
                    }
                }
            });




            /*****************************************************************************************
             * Clicking on the cardview item will expand the card to
             * revieal additional information as well as additional buttons
             * which will perform the share, webview, and add to calendar functions
             * ****************************************************************************************/
            eventAdapterViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, DisplayEvent.class);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    context.startActivity(intent);
                }
            });
        }




        /*****************************************************************************************
         * Type 2 represents the grid layout and its corresponding views are set
         * ****************************************************************************************/
        if(type==2) {
            //setGridAnimation(eventAdapterViewHolder.itemView, position);
            eventAdapterViewHolder.mEventPreferenceTVBack.setText(eventPref);
            eventAdapterViewHolder.mEventNameBack.setText(event);
            eventAdapterViewHolder.mEventLocationTVBack.setText("1234 Example St. "+eventLocation);
            eventAdapterViewHolder.mEventCostBack.setText("Event Price: $20.00");
            eventAdapterViewHolder.mEventAgeBack.setText("Age Restriction: 21+");
            eventAdapterViewHolder.mEventInfoBack.setText("Description: "+eventInfo);
            eventAdapterViewHolder.mEventDistanceBack.setText("Distance: "+getLocation(lat, lng)+" miles");


            /****************************************************************************
             *Sets up Bundle object to pass to DisplayEvent fragment
             * DisplayEvent fragment displays all event information
             ***************************************************************************/
            final Double finalLat2 = lat;
            final Double finalLng2 = lng;
            eventAdapterViewHolder.moreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, DisplayEvent.class);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    context.startActivity(intent);
                }
            });

            final ProgressBar progressBar = (ProgressBar)rootView.findViewById(R.id.progress);

            Log.i("Image", getImageURL(eventPath));
            Glide.with(context.getApplicationContext())
                    .load(getImageURL(eventPath))
                    .placeholder(R.drawable.progress)
                    .error(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(eventAdapterViewHolder.mEventImage);

            Glide.with(context.getApplicationContext())
                    .load(getImageURL(eventPath))
                    .placeholder(R.drawable.progress)
                    .error(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(eventAdapterViewHolder.mEventImageback);

        }


    }
    public void setBundle(Bundle bundle){
        this.bundle=bundle;
    }
    public Bundle getBundle(){
        return this.bundle;
    }

    /*****************************************************************************************
     * Retrieve lat and lng from database and creates location object with event location
     * location2 created with users current lat and lng location
     * distance between location1 and location2 is calculated and returned
     ****************************************************************************************/
    private int getLocation(double lat, double lng){
        Location location2 = new Location("location2");
        double distance=0.0;
        GPSTracker gps = new GPSTracker(context.getApplicationContext());
        if(gps.canGetLocation()){
            location2.setLatitude(gps.getLatitude());
            location2.setLongitude(gps.getLongitude());
            Location location1 = new Location("");
            location1.setLongitude(lng);
            location1.setLatitude(lat);
            distance=location1.distanceTo(location2);
        }
        return (int)Math.round(distance*0.000621371192); }



    /*****************************************************************************************
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     ****************************************************************************************/
    @Override
    public int getItemCount() {
        if(mEventData==null)
            return 0;
        else
            return mEventData.size();}


    /************************************************************************************
     * This method is used to set the event data if we haven't set it yet. This is handy when we
     * get new data from the web but don't want to create a new EventAdapter to display it.
     *
     * @param eventData The new weather data to be displayed.
     ***********************************************************************************/
    public void setEventData(ArrayList<JSONObject> eventData) {
        mEventData = eventData;
        notifyDataSetChanged();
    }

    public String getImageURL(String path) {
        return context.getString(R.string.IMAGE_URL_PATH) + path; }

    public void loadImage(final ImageView imageView, final String URL) {
        Log.i("Image", URL);
        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        Glide.with(imageView.getContext())
                .load(URL)
                .placeholder(R.drawable.progress)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.placeholder)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
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

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.enter);
            viewToAnimate.startAnimation(animation);
            lastPosition = position; }
    }
    private void setGridAnimation(View viewToAnimate, int position){
        if(position>lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.swing_up_left);
            viewToAnimate.startAnimation(animation);
            lastPosition=position;
        }
    }
    public ArrayList<String> getLatsArrayList(){
        return latsArrayList;
    }
    public ArrayList<String> getLngsArrayList(){
        return lngsArrayList;
    }

    public ArrayList<JSONObject> getObject() {
        return mEventData;
    }

    public ArrayList<String> getEventNameList(){
        return eventNameList;
    }
    public ArrayList<String> getEventDescList(){
        return eventDescList;
    }
    public ArrayList<String> getEventImageList(){
        return eventImageList;
    }
    public ArrayList<String> getCostArrayList(){
        return costArrayList;
    }
    public ArrayList<String> getEventLocationList(){
        return eventLocationList;
    }
    public ArrayList<String> getHosterArrayList(){
        return hosterArrayList;
    }
    public ArrayList<String> getEventStartList(){
        return eventStartList;
    }
    public ArrayList<String> getEventPrefList(){
        return eventPrefList;
    }
    public ArrayList<String> getEventTimeList(){
        return eventTimeList;
    }
    public ArrayList<Integer> getEventDistanceList(){
        return eventDistanceList;
    }

}