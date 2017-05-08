package com.example.codenamebiscuit.helper;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.eventfragments.MapFragment;
import com.example.codenamebiscuit.requests.QueryEventList;
import com.github.nitrico.mapviewpager.MapViewPager;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by jaskarnjagpal on 4/28/17.
 */

public class MapViewPagerAdapter extends MapViewPager.MultiAdapter {
    private LinkedList<CameraPosition> eventPositions;
    private ArrayList<JSONObject> data;
    private CameraPosition cameraPosition;
    public static ArrayList<String> eventName;
    public static ArrayList<String> eventImage;
    public static ArrayList<String> eventDistance;
    public static EventBundle bundle;

    public MapViewPagerAdapter(FragmentManager fm, Context context, String user_id, Activity activity){
        super(fm);
        eventPositions = new LinkedList<>();
        eventName = new ArrayList<>();
        eventImage = new ArrayList<>();
        eventDistance = new ArrayList<>();
        try {
            data = new QueryEventList(context.getString(R.string.DATABASE_MAIN_EVENTS_PULLER), user_id).execute().get();
            if(data.size()>0){
                Events.fromJson(data, activity);
                bundle = new EventBundle(data);
                for(JSONObject obj :data) {
                    double lat = obj.getDouble("lat");
                    double lng = obj.getDouble("lng");
                    eventName.add(obj.getString("event_name"));
                    eventImage.add(obj.getString("img_path"));
                    eventDistance.add(obj.getString("event_distance"));
                    cameraPosition = CameraPosition.fromLatLngZoom(new LatLng(lat, lng), 14f);
                    eventPositions.add(cameraPosition);
                }
            }else{
                Toast.makeText(activity, "No events found...", Toast.LENGTH_SHORT).show();
                cameraPosition = CameraPosition.fromLatLngZoom(new LatLng(0.0, 0.0), 14f);
                eventPositions.add(cameraPosition);
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<CameraPosition> getCameraPositions(int i) {
        List<CameraPosition> position = new ArrayList<>();
        if(eventPositions.size()>0)
            position.add(eventPositions.get(i));
        else
            position.add(CameraPosition.fromLatLngZoom(new LatLng(0.0, 0.0), 14f));
        return position;
    }

    @Override
    public String getMarkerTitle(int page, int position) {
        if(eventName.size()>0)
            return eventName.get(page);
        else return "";
    }


    @Override
    public Fragment getItem(int position) {
        return MapFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        if(eventPositions.size()>0)
            return eventPositions.size();
        else return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(eventName.size()>0)
            return eventName.get(position);
        else return "";
    }
}
