package com.example.codenamebiscuit;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;
import com.example.codenamebiscuit.eventfragments.DisplayEvent;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thebrownarrow.customstyledmap.CustomMap;
import com.thebrownarrow.model.MyLocation;

import java.util.ArrayList;

public class MapActivity extends ProgressFragment {
    private ArrayList<String> latsArrayList;
    private ArrayList<String> lngsArrayList;
    private ArrayList<String> nameList;
    private ArrayList<String> imageList;
    private ArrayList<String> descList;
    private ArrayList<String> hosterList;
    private ArrayList<String> costList;
    private ArrayList<String> startList;
    private ArrayList<String> timeList;
    private ArrayList<String> prefList;
    private ArrayList<String> locationList;
    private ArrayList<Integer> distanceList;


    private ArrayList<MyLocation> latLngsArrayList;
    private Animation slide_out_down, slide_in_up;
    public static GoogleMap map;
    private SupportMapFragment supportMapFragment;
    Marker hamburg, previousSelectedMarker;
    private View mContentView;
    private static ViewPager event_pager;
    CustomMap customMap;
    private Handler mHandler;
    private Runnable mShowContentRunnable = new Runnable() {
        @Override
        public void run() {
            setContentShown(true);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        mContentView = inflater.inflate(R.layout.activity_map, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(mContentView);
        setContents(mContentView);


    }
    public void onDestroyView()
    {
        super.onDestroyView();
        Fragment fragment = this.getChildFragmentManager().findFragmentById(R.id.map);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }
    @Override
    public void onResume() {
        super.onResume();
        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        //((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    private void setContents(View rootView){
        setContentShown(false);
        mHandler = new Handler();
        mHandler.postDelayed(mShowContentRunnable, 2000);
        Bundle bundle = getArguments();
        latsArrayList = new ArrayList<>();
        latsArrayList.clear();
        latsArrayList = bundle.getStringArrayList("latList");

        lngsArrayList = new ArrayList<>();
        lngsArrayList.clear();
        lngsArrayList = bundle.getStringArrayList("lngList");

        latLngsArrayList = new ArrayList<>();
        latLngsArrayList.clear();

        nameList=new ArrayList<>();
        nameList.clear();
        nameList = bundle.getStringArrayList("nameList");

        imageList = new ArrayList<>();
        imageList.clear();
        imageList = bundle.getStringArrayList("imageList");

        descList = new ArrayList<>();
        descList.clear();
        descList = bundle.getStringArrayList("descList");

        hosterList = new ArrayList<>();
        hosterList.clear();
        hosterList = bundle.getStringArrayList("hosterList");

        prefList = new ArrayList<>();
        prefList.clear();
        prefList = bundle.getStringArrayList("prefList");

        startList = new ArrayList<>();
        startList.clear();
        startList = bundle.getStringArrayList("startList");

        timeList = new ArrayList<>();
        timeList.clear();
        timeList = bundle.getStringArrayList("timeList");

        costList = new ArrayList<>();
        costList.clear();
        costList = bundle.getStringArrayList("costList");

        locationList = new ArrayList<>();
        locationList.clear();
        locationList = bundle.getStringArrayList("locationList");

        distanceList = new ArrayList<>();
        distanceList.clear();
        distanceList = bundle.getIntegerArrayList("distanceList");


        for(int i=0; i<lngsArrayList.size(); i++){
            latLngsArrayList.add(new MyLocation(Double.parseDouble(latsArrayList.get(i)), Double.parseDouble(lngsArrayList.get(i))));
            Log.i("latlngs", latLngsArrayList.get(i).getLatitude()+", "+latLngsArrayList.get(i).getLongitude());
        }


        slide_out_down = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_out_down);
        slide_in_up = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in_up);

        event_pager = (ViewPager)rootView.findViewById(R.id.event_pager);

        //supportMapFragment = (SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                customMap = new CustomMap(map, latLngsArrayList, getContext().getApplicationContext());

                try {
                    //customMap.setCustomMapStyle(R.raw.mapstyle);
                    // Customise the styling of the base map using a JSON object defined in a raw resource file.
                } catch (Resources.NotFoundException e) {
                    Log.e("Explore detail activity", "Can't find style. Error: " + e);
                }

                handleMap();
                event_pager.setAdapter(new MapViewPagerAdapter(getContext().getApplicationContext(), latLngsArrayList));
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
    private void handleMap() {

        if (map != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                        event_pager.setCurrentItem(mPosition, true);

                    } else {
                        event_pager.setCurrentItem(mPosition, true);
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
                    customMap = new CustomMap(map, latLngsArrayList, getContext().getApplicationContext());

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
            TextView tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_name.setText(nameList.get(position));

            TextView tv_desc = (TextView)itemView.findViewById(R.id.tv_desc);
            tv_desc.setText(descList.get(position)+" "+getString(R.string.lorem_ipsum));

            ImageView tv_image = (ImageView)itemView.findViewById(R.id.tv_image);
            Picasso.with(getContext().getApplicationContext()).load(imageList.get(position)).fit().into(tv_image);

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
                    bundle.putDouble("eventLat", Double.parseDouble(latsArrayList.get(position)));
                    bundle.putDouble("eventLng", Double.parseDouble(lngsArrayList.get(position)));
                    bundle.putString("eventDistance", distanceList.get(position)+"");
                    bundle.putInt("mapImage", 1);
                    Intent intent = new Intent(getActivity(), DisplayEvent.class);
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);
                }
            });
            final MyLocation myLocation = arr_LocationList.get(position);

            //tv_name.setText("Location:" + (position + 1));

            container.addView(itemView);

            return itemView;
        }
    }

}
