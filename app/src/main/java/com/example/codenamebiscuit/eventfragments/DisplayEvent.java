package com.example.codenamebiscuit.eventfragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.codenamebiscuit.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.like.LikeButton;
import com.mikepenz.iconics.view.IconicsImageView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayEvent.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayEvent extends AppCompatActivity{

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
    private Toolbar toolbar;
    private TextView toolbarTitle;
    MapView mapView;
    private GoogleMap googleMap;
    Typeface typeface;



    public DisplayEvent() {
    }


    // TODO: Rename and change types and number of parameters
    public static DisplayEvent newInstance() {
        DisplayEvent fragment = new DisplayEvent();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_display_event);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp);
        upArrow.setColorFilter(getResources().getColor(R.color.livinPink), PorterDuff.Mode.SRC_ATOP);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(upArrow);

        typeface= Typeface.createFromAsset(this.getAssets(), "fonts/Raleway-Black.ttf");
        bindViews(savedInstanceState);

        setupMap(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    public void bindViews(Bundle savedInstanceState) {

        likeText = (TextView)findViewById(R.id.currentRating);
        likeButton = (LikeButton)findViewById(R.id.like);

        phone = (IconicsImageView)findViewById(R.id.event_call);
        webSite = (IconicsImageView)findViewById(R.id.event_website);
        navigate = (IconicsImageView)findViewById(R.id.event_directions);
        displayEventImage = (ImageView)findViewById(R.id.display_event_image);

        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        webView = (WebView)findViewById(R.id.webView);


        displayEventName = (TextView)findViewById(R.id.display_event_name);
        displayEventHoster = (TextView)findViewById(R.id.event_hoster);
        displayEventDistance = (TextView)findViewById(R.id.display_event_distance);
        displayEventPref = (TextView)findViewById(R.id.display_event_preference);
        displayEventDesc = (TextView)findViewById(R.id.display_event_description);
        displayEventLocation = (TextView)findViewById(R.id.display_event_location);
        displayEventStartDate = (TextView)findViewById(R.id.display_event_date);
        displayEventCost = (TextView)findViewById(R.id.display_event_cost);
        displayEventStartTIme = (TextView)findViewById(R.id.display_event_time);
        mapView = (MapView)findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        Bundle bundle = getIntent().getExtras();
        eventName=bundle.getString("eventName");
        toolbarTitle.setText(eventName);

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

    }

    public void setupMap(Bundle savedInstanceState){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                googleMap=gMap;
                LatLng eventCoords = new LatLng(eventLat, eventLng);
                googleMap.addMarker(new MarkerOptions().position(eventCoords).title(eventName));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(eventCoords, 10);
                googleMap.animateCamera(update);
            }
        });

        loadImage();

        displayEventName.setText(eventName);
        displayEventHoster.setText("Presented By: "+eventHoster);
        displayEventDistance.setText(eventDistance);
        displayEventPref.setText(eventPreferences+" | Sports | Music");
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
    }
    @Override
    public void onStop() {
        super.onStop();
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
        Picasso.with(this).load(getImageURL(eventImage))
                .centerCrop().fit().into(displayEventImage);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    public String getImageURL(String path) {
        if(mapImage!=1)
            return getResources().getString(R.string.IMAGE_URL_PATH) + path;
        else
            return path;
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
