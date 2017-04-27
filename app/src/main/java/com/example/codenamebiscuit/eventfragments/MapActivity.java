package com.example.codenamebiscuit.eventfragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.GPSTracker;
import com.example.codenamebiscuit.helper.ImageLoader;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.thebrownarrow.customstyledmap.CustomMap;
import com.thebrownarrow.model.MyLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapActivity extends AppCompatActivity {
    private ArrayList<Double> latsArrayList;
    private ArrayList <Double> lngsArrayList;
    private ArrayList <String> nameList;
    private ArrayList<String> imageList;
    private ArrayList<String> descList;
    private ArrayList<String> hosterList;
    private ArrayList<String> costList;
    private ArrayList<String> startList;
    private ArrayList<String> timeList;
    private ArrayList<String> prefList;
    private ArrayList<String> locationList;
    private ArrayList<Integer> distanceList;
    private JSONObject currentUser;


    private ArrayList<MyLocation> latLngsArrayList;
    private String userId;
    private Animation slide_out_down, slide_in_up;
    public static GoogleMap map;
    private SupportMapFragment supportMapFragment;
    Marker hamburg, previousSelectedMarker;
    private ViewPager event_pager;
    CustomMap customMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map);

        userId=sharedPreferences.getString("user_id", null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp);
        upArrow.setColorFilter(getResources().getColor(R.color.livinWhite), PorterDuff.Mode.SRC_ATOP);
        this.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContents();
    }


    public void onDestroy()
    {
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    private void loadData(){
        latLngsArrayList = new ArrayList<>();
        latsArrayList = new ArrayList<>();
        lngsArrayList = new ArrayList<>();
        nameList = new ArrayList<>();
        imageList = new ArrayList<>();
        descList = new ArrayList<>();
        hosterList = new ArrayList<>();
        prefList = new ArrayList<>();
        startList = new ArrayList<>();
        timeList = new ArrayList<>();
        costList = new ArrayList<>();
        locationList = new ArrayList<>();
        distanceList = new ArrayList<>();
        try{
            ArrayList<JSONObject> data = new QueryEventList(getString(R.string.DATABASE_MAIN_EVENTS_PULLER),userId).execute().get();
            for(int i = 0; i< data.size(); i++){
                latsArrayList.add(data.get(i).getDouble("lat"));
                lngsArrayList.add(data.get(i).getDouble("lng"));
                nameList.add(data.get(i).getString("event_name"));
                imageList.add(data.get(i).getString("img_path"));
                descList.add(data.get(i).getString("event_description"));
                hosterList.add(data.get(i).getString("event_sponsor"));
                prefList.add(data.get(i).getString("preference_name"));
                startList.add(data.get(i).getString("start_date"));
                timeList.add(data.get(i).getString("start_time"));
                costList.add(data.get(i).getString("event_cost"));
                locationList.add(data.get(i).getString("event_location"));
                distanceList.add(getLocation(data.get(i).getDouble("lat"), data.get(i).getDouble("lng")));
            }

        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void setContents(){
        loadData();

        for(int i=0; i<lngsArrayList.size(); i++){
            latLngsArrayList.add(new MyLocation(latsArrayList.get(i), lngsArrayList.get(i)));
            Log.i("latlngs", latLngsArrayList.get(i).getLatitude()+", "+latLngsArrayList.get(i).getLongitude());
        }


        slide_out_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_down);
        slide_in_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_up);

        event_pager = (ViewPager)findViewById(R.id.event_pager);

        supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                MyLocation location = latLngsArrayList.get(0);
                Point mappoint = map.getProjection().toScreenLocation(
                        new LatLng(latLngsArrayList.get(1).getLatitude(), latLngsArrayList.get(1).getLongitude()));
                mappoint.set(mappoint.x, mappoint.y - 30);
                map.animateCamera(CameraUpdateFactory.newLatLng(map.getProjection().fromScreenLocation(mappoint)));
                customMap = new CustomMap(map, latLngsArrayList, getApplicationContext());

                try {
                    //customMap.setCustomMapStyle(R.raw.mapstyle);
                    // Customise the styling of the base map using a JSON object defined in a raw resource file.
                } catch (Resources.NotFoundException e) {
                    Log.e("Explore detail activity", "Can't find style. Error: " + e);
                }
                handleMap();
                event_pager.setAdapter(new MapViewPagerAdapter(getApplicationContext(), latLngsArrayList));;
                for(int i=0; i<latLngsArrayList.size(); i++){
                    customMap.addPin(latLngsArrayList.get(i), i);
                }
            }
        });


        event_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                MyLocation location = latLngsArrayList.get(position);
                Point mappoint = map.getProjection().toScreenLocation(
                        new LatLng(location.getLatitude(), location.getLongitude()));
                mappoint.set(mappoint.x, mappoint.y - 30);
                map.animateCamera(CameraUpdateFactory.newLatLng(map.getProjection().fromScreenLocation(mappoint)));

                customMap.addSelectedCustomPin(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private int getLocation(double lat, double lng){
        Location location2 = new Location("location2");
        double distance=0.0;
        GPSTracker gps = new GPSTracker(getApplicationContext());
        if(gps.canGetLocation()){
            location2.setLatitude(gps.getLatitude());
            location2.setLongitude(gps.getLongitude());
            Location location1 = new Location("");
            location1.setLongitude(lng);
            location1.setLatitude(lat);
            distance=location1.distanceTo(location2);
        }
        return (int)Math.round(distance*0.000621371192); }

    private void handleMap() {;
        if (map != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            map.setMyLocationEnabled(true);

            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {

                    final int mPosition = (int) marker.getTag();
                    try {
                        if (previousSelectedMarker != null) {

                            MyLocation location = latLngsArrayList.get(mPosition);

                            if (map.getCameraPosition().zoom >= 13) {
                                previousSelectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                                        BitmapFactory.decodeResource(getResources(),
                                                R.drawable.ic_near_normal_pin)));
                            } else if (map.getCameraPosition().zoom < 13) {
                                previousSelectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                                        BitmapFactory.decodeResource(getResources(),
                                                R.drawable.ic_normal_pin)));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    marker.setIcon(null);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(getResources(),
                                    R.drawable.ic_selected_pin)));


                    previousSelectedMarker = marker;

                    if (event_pager.getVisibility() != View.VISIBLE) {

                        event_pager.startAnimation(slide_in_up);
                        slide_in_up.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation arg0) {

                                event_pager.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation arg0) {

                            }

                            @Override
                            public void onAnimationEnd(Animation arg0) {

                            }
                        });

                        event_pager.setCurrentItem(0, true);

                    } else {
                        event_pager.setCurrentItem(0, true);
                    }

                    return false;
                }
            });

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    if (event_pager.getVisibility() == View.VISIBLE) {
                        event_pager.startAnimation(slide_out_down);

                        slide_out_down.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation arg0) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation arg0) {

                            }

                            @Override
                            public void onAnimationEnd(Animation arg0) {
                                event_pager.setVisibility(View.GONE);
                                event_pager.clearAnimation();
                            }
                        });
                    }
                }
            });


        } else {
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    map = googleMap;
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    customMap = new CustomMap(map, latLngsArrayList,getApplicationContext());

                    try {
                        customMap.setCustomMapStyle(R.raw.mapstyle);
                        // Customise the styling of the base map using a JSON object defined in a raw resource file.
                    } catch (Resources.NotFoundException e) {
                        Log.e("Explore detail activity", "Can't find style. Error: " + e);
                    }

                    handleMap();
                }
            });

        }
    }
    class MapViewPagerAdapter extends PagerAdapter {
        ArrayList<MyLocation> arr_LocationList;
        Context context;

        public MapViewPagerAdapter(Context context, ArrayList<MyLocation> arr_ExploreList) {
            this.context = context;
            this.arr_LocationList = arr_ExploreList;
        }

        @Override
        public int getCount() {
            return arr_LocationList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.row_map_pager, null);

            LinearLayout lnr_main = (LinearLayout) itemView.findViewById(R.id.lnr_main);
            TextView tv_name = (TextView) itemView.findViewById(R.id.tv_event_name);
            tv_name.setText(nameList.get(position));

            TextView tv_preference = (TextView)itemView.findViewById(R.id.tv_event_preference);
            tv_preference.setText(prefList.get(position));

            TextView tv_location = (TextView)itemView.findViewById(R.id.tv_event_location);
            tv_location.setText(locationList.get(position));

            TextView tv_cost = (TextView)itemView.findViewById(R.id.cost);
            tv_cost.setText("Entry Fee: $"+costList.get(position));


            final ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.progress_bar);
            ImageView tv_image = (ImageView)itemView.findViewById(R.id.iv_event_image);
            ImageLoader.loadFullImage(getApplicationContext(), imageList.get(position), tv_image, progressBar);

            tv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("eventName", nameList.get(position));
                    bundle.putString("eventImage", imageList.get(position));
                    bundle.putString("eventDate", startList.get(position));
                    bundle.putString("eventHoster", hosterList.get(position));
                    bundle.putString("eventDistance", "0");
                    bundle.putString("eventPreference", prefList.get(position));
                    bundle.putString("eventDescription", descList.get(position));
                    bundle.putString("eventLocation", locationList.get(position));
                    bundle.putString("eventCost", costList.get(position));
                    bundle.putString("eventTime", timeList.get(position));
                    bundle.putDouble("eventLat", latsArrayList.get(position));
                    bundle.putDouble("eventLng", lngsArrayList.get(position));
                    bundle.putString("eventDistance", distanceList.get(position)+"");
                    bundle.putInt("mapImage", 1);
                    Intent intent = new Intent(MapActivity.this, DisplayEvent.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            final MyLocation myLocation = arr_LocationList.get(position);

            //tv_name.setText("Location:" + (position + 1));

            container.addView(itemView);

            return itemView;
        }
    }

}
