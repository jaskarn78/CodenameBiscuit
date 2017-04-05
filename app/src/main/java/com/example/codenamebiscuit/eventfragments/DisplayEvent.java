package com.example.codenamebiscuit.eventfragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.R;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.like.LikeButton;
import com.mikepenz.iconics.view.IconicsImageView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayEvent.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayEvent extends ProgressFragment{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private ImageView displayEventImage;
    private TextView displayEventName, displayEventStartDate, displayEventStartTIme;
    private TextView displayEventDesc, displayEventLocation, displayEventPref;
    private TextView displayEventDistance, displayEventCost, likeText;
    private TextView displayEventHoster;
    private String eventName, eventImage, eventDate, eventCost;
    private String eventTime, eventDescription, eventLocation;
    private String eventPreferences, eventHoster, eventDistance;
    private int mapImage;
    private LikeButton likeButton;
    private String eventId;
    private RatingBar ratingBar;
    private double eventLat, eventLng;
    private IconicsImageView webSite, navigate;
    private IconicsImageView phone;
    private WebView webView;
    MapView mapView;
    private GoogleMap googleMap;
    Typeface typeface;
    private View rootView;
    private android.os.Handler mHandler;
    private Runnable mShowContentRunnable = new Runnable() {
        @Override
        public void run() {
            setContentShown(true);
        }

    };



    private OnFragmentInteractionListener mListener;

    public DisplayEvent() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DisplayEvent newInstance() {
        DisplayEvent fragment = new DisplayEvent();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeface= Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Black.ttf");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_display_event, container, false);

        likeText = (TextView)rootView.findViewById(R.id.currentRating);
        likeButton = (LikeButton)rootView.findViewById(R.id.like);

        phone = (IconicsImageView)rootView.findViewById(R.id.event_call);
        webSite = (IconicsImageView)rootView.findViewById(R.id.event_website);
        navigate = (IconicsImageView)rootView.findViewById(R.id.event_directions);
        displayEventImage = (ImageView)rootView.findViewById(R.id.display_event_image);

        ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBar);
        webView = (WebView)rootView.findViewById(R.id.webView);


        displayEventName = (TextView)rootView.findViewById(R.id.display_event_name);
        displayEventHoster = (TextView)rootView.findViewById(R.id.event_hoster);
        displayEventDistance = (TextView)rootView.findViewById(R.id.display_event_distance);
        displayEventPref = (TextView)rootView.findViewById(R.id.display_event_preference);
        displayEventDesc = (TextView)rootView.findViewById(R.id.display_event_description);
        displayEventLocation = (TextView)rootView.findViewById(R.id.display_event_location);
        displayEventStartDate = (TextView)rootView.findViewById(R.id.display_event_date);
        displayEventCost = (TextView)rootView.findViewById(R.id.display_event_cost);
        displayEventStartTIme = (TextView)rootView.findViewById(R.id.display_event_time);
        mapView = (MapView)rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();


        Bundle bundle = this.getArguments();
        eventName=bundle.getString("eventName");
        eventImage = bundle.getString("eventImage");
        eventDate = bundle.getString("eventDate");
        eventHoster = bundle.getString("eventHoster");
        eventDistance = bundle.getString("eventDistance");
        eventPreferences = bundle.getString("eventPreference");
        eventDescription = bundle.getString("eventDescription");
        eventLocation = bundle.getString("eventLocation");
        eventDate = bundle.getString("eventDate");
        eventCost = bundle.getString("eventCost");
        eventTime = bundle.getString("eventTime");
        eventId = bundle.getString("eventId");
        eventLat = bundle.getDouble("eventLat");
        eventLng = bundle.getDouble("eventLng");
        mapImage = bundle.getInt("mapImage");
        Log.i("coords", eventLat+", "+eventLng);


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setContentView(rootView);
        setContentShown(false);
        mHandler = new android.os.Handler();
        mHandler.postDelayed(mShowContentRunnable, 500);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                googleMap=gMap;
                // Add a marker in Sydney and move the camera
                LatLng eventCoords = new LatLng(eventLat, eventLng);
                googleMap.addMarker(new MarkerOptions().position(eventCoords).title(eventName));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(eventCoords, 10);
                googleMap.animateCamera(update);
            }
        });


        loadImage();

        displayEventName.setText(eventName);
        displayEventHoster.setText("By: "+eventHoster);
        displayEventDistance.setText(eventDistance);
        displayEventPref.setText(eventPreferences);
        displayEventLocation.setText("1234 Example Way "+eventLocation+" 95826");
        displayEventStartDate.setText("Start Date: "+parseDate(eventDate));
        displayEventStartTIme.setText("Start Time: "+parseTime(eventTime));
        displayEventCost.setText("Entry Fee $"+ eventCost);
        displayEventDesc.setText(eventDescription+ " Lorem ipsum dolor sit amet, consectetur adipisicing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad " +
                "minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea " +
                "commodo consequat.");

        setupWebView();
        setupWebsiteBtn();
        setupNavigateBtn();
        setupPhone();



    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void setupWebsiteBtn(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
            }
        });
        webSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webView.getVisibility()==View.GONE) {
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl("https://www.google.com");
                }
                else
                    webView.setVisibility(View.GONE); }
        });
    }

    private void setupNavigateBtn(){
        final String map = "http://maps.google.co.in/maps?q=" + eventLocation;

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(i);
            }
        });
    }

    private void setupWebView(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
    }

    private void setupPhone(){
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:9167777777"));
                startActivity(intent);
            }
        });
    }

    private String parseDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj = null;
        try {
            dateObj = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("MM-dd-yyyy").format(dateObj);
    }

    private String parseTime(String time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date dateObj = null;
        try {
            dateObj = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("HH:mm").format(dateObj);
    }

    private void loadImage(){
        Picasso.with(getContext().getApplicationContext()).load(getImageURL(eventImage))
                .centerCrop().fit().into(displayEventImage);
    }

    public String getImageURL(String path) {
        if(mapImage!=1)
            return "http://athena.ecs.csus.edu/~teamone/events/" + path;
        else
            return path;
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
