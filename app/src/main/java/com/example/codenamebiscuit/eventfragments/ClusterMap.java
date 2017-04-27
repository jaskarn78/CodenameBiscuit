package com.example.codenamebiscuit.eventfragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.Event;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.EventItem;
import com.example.codenamebiscuit.helper.GPSTracker;
import com.example.codenamebiscuit.helper.ImageLoader;
import com.example.codenamebiscuit.helper.MultiDrawable;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by jaskarnjagpal on 4/26/17.
 */

public class ClusterMap extends AppCompatActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<EventItem> {
    private GoogleMap mMap;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private Drawable drawable;
    private View customMarkerView;
    private ImageView customImageView;
    private ClusterManager<EventItem> mClusterManager;


    @Override
    protected void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.cluster_map);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp);
        upArrow.setColorFilter(getResources().getColor(R.color.livinWhite), PorterDuff.Mode.SRC_ATOP);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        customMarkerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_marker_view, null);
        customImageView = (ImageView)customMarkerView.findViewById(R.id.profile_image);
        setUpMap();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (mMap != null) {
            return;
        }
        mMap = map;
        startDemo();
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public boolean onClusterClick(Cluster<EventItem> cluster) {
        String firstName = cluster.getItems().iterator().next().getTitle();
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    protected GoogleMap getMap() {
        return mMap;
    }

    private class EventRenderer extends DefaultClusterRenderer<EventItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private ImageView customImageView;
        private final int mDimension;

        public EventRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.imageItem);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
            mImageView.setDrawingCacheEnabled(true);


        }

        @Override
        protected void onBeforeClusterItemRendered(final EventItem event, final MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageResource(R.drawable.livbg);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(event.getTitle());

        }

        @Override
        protected void onBeforeClusterRendered(final Cluster<EventItem> cluster, final MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            final List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            final int width = mDimension;
            final int height = mDimension;

            for (EventItem item : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;

            }

        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
    }

    private void startDemo() {
        GPSTracker gps = new GPSTracker(getApplicationContext());
        LatLng latLng = new LatLng(gps.getLatitude(), gps.getLongitude());
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        getMap().animateCamera(update);
        mClusterManager = new ClusterManager<EventItem>(this, getMap());
        mClusterManager.setRenderer(new EventRenderer());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        getMap().setOnCameraIdleListener(mClusterManager);

        try {
            addItems();
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        mClusterManager.cluster();
    }

    private void addItems() throws ExecutionException, InterruptedException, JSONException {
        String userId = getIntent().getStringExtra("userId");
        ArrayList<JSONObject> data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER), userId).execute().get();
        for(JSONObject obj : data) {
            mClusterManager.addItem(new EventItem(obj.getDouble("lat"), obj.getDouble("lng"), obj.getString("img_path"), obj.getString("event_name")));

        }
    }


}
