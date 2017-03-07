package com.example.codenamebiscuit.rv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.FlipAnimation;
import com.example.codenamebiscuit.swipedeck.layouts.SwipeFrameLayout;
import com.google.android.gms.maps.model.LatLng;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder> {
    private ArrayList<JSONObject> mEventData;
    private Context context;
    private Typeface typeface;
    private ClickListener clickListener = null;
    private int type;


    public EventAdapter(Context context, int type) {
        this.context = context;
        mEventData = new ArrayList<>();
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Black.ttf");
        this.type=type;
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
        public final TextView mEventInfoBack;
        public final ImageButton mSaveImageButton, mLinkImageButton;
        public final TextView mEventHoster;
        // public final ImageView mFeaturedImage;
        public final TextView mEventDistance;
        public final CardView cardView;
        public final RelativeLayout layout;
        public final WebView mWebView;
        SwipeFrameLayout swipeLayout;



        public EventAdapterViewHolder(final View view) {
            super(view);

            mEventPreferenceTV = (TextView) view.findViewById(R.id.tv_event_preference);
            mEventPreferenceTVBack = (TextView) view.findViewById(R.id.event_preference_back);

            //mEventPreferenceTV.setTypeface(typeface);

            mEventLocationTV = (TextView) view.findViewById(R.id.tv_event_location);
            mEventLocationTVBack = (TextView) view.findViewById(R.id.event_location_back);

            //mEventLocationTV.setTypeface(typeface);

            mEventName = (TextView) view.findViewById(R.id.tv_event_name);
            mEventNameBack = (TextView) view.findViewById(R.id.event_name_back);

            //mEventName.setTypeface(typeface);

            mEventAge = (TextView) view.findViewById(R.id.age);
            mEventAgeBack = (TextView)view.findViewById(R.id.event_age_back);
            //mEventAge.setTypeface(typeface);

            mEventCost = (TextView) view.findViewById(R.id.cost);
            mEventCostBack = (TextView)view.findViewById(R.id.event_cost_back);
            // mEventCost.setTypeface(typeface);

            mEventStartDate = (TextView)view.findViewById(R.id.start_date);
            mEventStartTime = (TextView)view.findViewById(R.id.start_time);

            mSaveImageButton = (ImageButton)view.findViewById(R.id.saveButton);

            mEventDistance = (TextView) view.findViewById(R.id.event_distance);
            mEventInfoBack = (TextView)view.findViewById(R.id.event_info_back);
            mEventImageback = (ImageView)view.findViewById(R.id.iv_event_image_back);

            mEventHoster = (TextView)view.findViewById(R.id.tv_event_hoster);
            layout = (RelativeLayout) view.findViewById(R.id.extend);

            mEventImage = (ImageView) view.findViewById(R.id.iv_event_image);
            cardView = (CardView) view.findViewById(R.id.cardview);

            mWebView = (WebView)view.findViewById(R.id.webView);
            mLinkImageButton = (ImageButton)view.findViewById(R.id.linkButton);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            if(type==2) {
                mEventNameBack.setTypeface(typeface);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Log.i("Layer type: ", Integer.toString(v.getLayerType()));
                        Log.i("Hardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));

                        //Picasso.with(context).load(R.drawable.liv1).fit().centerCrop().into(imageView);
                        final CardView cv = (CardView) v.findViewById(R.id.cardview);
                        final CardView cvBack = (CardView) v.findViewById(R.id.card_view_back);
                        FlipAnimation flipAnimation = new FlipAnimation(cv, cvBack);

                        if (cv.getVisibility() == View.GONE)
                            flipAnimation.reverse();
                        v.startAnimation(flipAnimation);}
                });
            }
        }

        @Override
        public void onClick(final View v) {
            if (clickListener != null) {
                clickListener.itemClicked(v, getAdapterPosition());
            }
            StyleableToast st = new StyleableToast(context, "EVENT LONG CLICKED", Toast.LENGTH_SHORT);
            st.setBackgroundColor(Color.parseColor("#ff9dfc"));
            st.setTextColor(Color.WHITE);
            st.setIcon(R.drawable.ic_check_circle_white_24dp);
            st.setMaxAlpha();
            st.show();
        }


        @Override
        public boolean onLongClick(View v) {
            return true;
        }
    }


    @Override
    public void onBindViewHolder(final EventAdapterViewHolder eventAdapterViewHolder, int position) {

        String eventLocation = null;
        String eventPref = null;
        String eventPath = null;
        String event = null;
        String eventInfo = "";
        String startDate=null;
        String startTime=null;
        String eventid = null;
        LatLng latLng = null;
        String eventDistance = null;
        String eventHoster=null;
        String cost=null;


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
            //if(type==2)
            eventInfo = mEventData.get(position).getString("event_description");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        eventAdapterViewHolder.mEventPreferenceTV.setText(eventPref);
        eventAdapterViewHolder.mEventLocationTV.setText(eventLocation);
        eventAdapterViewHolder.mEventName.setText(event);

        final String finalEvent = event;
        final String finalEventInfo = eventInfo;
        final String finalEventLocation = eventLocation;
        final String finalEventInfo1 = eventInfo;


        if(type==1){
            Picasso.with(context.getApplicationContext())
                    .load(getImageURL(eventPath))
                    .resize(90, 90)
                    .into(eventAdapterViewHolder.mEventImage);
            eventAdapterViewHolder.mSaveImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance();
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra("beginTime", cal.getTimeInMillis());
                    intent.putExtra("allDay", false);
                    intent.putExtra("rrule", "FREQ=DAILY");
                    intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                    intent.putExtra("title", finalEvent);
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, finalEventLocation);
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, finalEventInfo1);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            eventAdapterViewHolder.mEventHoster.setText("Presented By: "+eventHoster);
            eventAdapterViewHolder.mEventCost.setText("Entry Fee: $"+cost);
            eventAdapterViewHolder.mWebView.getSettings().setJavaScriptEnabled(true);
            eventAdapterViewHolder.mWebView.getSettings().setLoadWithOverviewMode(true);
            eventAdapterViewHolder.mWebView.getSettings().setUseWideViewPort(true);
            eventAdapterViewHolder.mWebView.setVerticalScrollBarEnabled(true);
            eventAdapterViewHolder.mWebView.setHorizontalScrollBarEnabled(true);
            eventAdapterViewHolder.mWebView.setWebViewClient(new WebViewClient(){

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
                @Override
                public void onPageFinished(WebView view, final String url) {
                    //progDailog.dismiss();

                }
            });
            eventAdapterViewHolder.mLinkImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventAdapterViewHolder.mWebView.loadUrl("https://www.google.com");
                    if(eventAdapterViewHolder.mWebView.getVisibility()==View.GONE){
                        eventAdapterViewHolder.mWebView.setVisibility(View.VISIBLE);
                        StyleableToast st = new StyleableToast(context.getApplicationContext(), "Loading Website...", Toast.LENGTH_SHORT);
                        st.setBackgroundColor(context.getColor(R.color.livinPink));
                        st.setTextColor(context.getColor(R.color.livinWhite));
                        st.show();
                    }else{
                        eventAdapterViewHolder.mWebView.setVisibility(View.GONE);
                    }

                }
            });
            eventAdapterViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (eventAdapterViewHolder.layout.getVisibility() == View.GONE) {
                        eventAdapterViewHolder.layout.setVisibility(View.VISIBLE);
                    } else {
                        eventAdapterViewHolder.layout.setVisibility(View.GONE);}
                }
            });

        }

            //if(startDate!=null)
                //eventAdapterViewHolder.mEventStartDate.setText(startDate);
            //if(startTime!=null)
                //eventAdapterViewHolder.mEventStartTime.setText("Time: "+startTime);
        if(type==2) {
            eventAdapterViewHolder.mEventPreferenceTVBack.setText(eventPref);
            eventAdapterViewHolder.mEventNameBack.setText(event);
            eventAdapterViewHolder.mEventLocationTVBack.setText("1234 Example St. "+eventLocation);
            eventAdapterViewHolder.mEventCostBack.setText("Event Price: $20.00");
            eventAdapterViewHolder.mEventAgeBack.setText("Age Restriction: 21+");
            eventAdapterViewHolder.mEventInfoBack.setText("Description: "+eventInfo);

            Picasso.with(context.getApplicationContext())
                    .load(getImageURL(eventPath))
                    .into(eventAdapterViewHolder.mEventImage);

            Picasso.with(context.getApplicationContext())
                    .load(getImageURL(eventPath))
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(eventAdapterViewHolder.mEventImageback); }

    }

        /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if(mEventData==null)
            return 0;
        else
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

    public String getImageURL(String path) {
        return "http://athena.ecs.csus.edu/~teamone/AndroidUploadImage/uploads/" + path; }

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