package com.example.codenamebiscuit.helper;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jaskarnjagpal on 4/20/17.
 */

public class EventBundle {
    private ArrayList<JSONObject> data;
    private Bundle bundle;
    public EventBundle(ArrayList<JSONObject> data){
        this.data=data;
    }

    public Bundle getBundle(int position){
        Bundle bundle = new Bundle();
        try {
            bundle.putString("eventName", data.get(position).getString("event_name"));
            bundle.putString("eventImage", data.get(position).getString("img_path"));
            bundle.putString("eventDate", data.get(position).getString("start_date"));
            bundle.putString("eventHoster", data.get(position).getString("event_sponsor"));
            bundle.putString("eventDistance", data.get(position).getString("event_distance"));
            bundle.putString("eventPreference", data.get(position).getString("preference_name"));
            bundle.putString("eventDescription", data.get(position).getString("event_description"));
            bundle.putString("eventLocation", data.get(position).getString("event_location"));
            bundle.putString("eventCost", data.get(position).getString("event_cost"));
            bundle.putString("eventTime", data.get(position).getString("start_time"));
            bundle.putString("eventId", data.get(position).getString("event_id"));
            bundle.putDouble("eventLat", data.get(position).getDouble("lat"));
            bundle.putDouble("eventLng", data.get(position).getDouble("lng"));
            bundle.putString("eventWebsite", data.get(position).getString("event_website"));
        } catch (JSONException e) { e.printStackTrace(); }
        return bundle;
    }

    public static ArrayList<String> getNameBundle(ArrayList<JSONObject> data){
        ArrayList<String> eventNames = new ArrayList<>();
        for(int i=0; i<data.size(); i++){
            try {
                eventNames.add(data.get(i).getString("event_name"));
            } catch (JSONException e) {e.printStackTrace();}
        }
        return eventNames;
    }
    public static ArrayList<String> getImageBundle(ArrayList<JSONObject> data){
        Bundle bundle = new Bundle();
        ArrayList<String> eventImage = new ArrayList<>();
        for(int i=0; i<data.size(); i++){
            try {
                eventImage.add(data.get(i).getString("img_path"));
            } catch (JSONException e) {e.printStackTrace();}
        } bundle.putStringArrayList("eventImageList", eventImage);
        return eventImage;
    }
    public static ArrayList<String> getLocationBundle(ArrayList<JSONObject> data){
        Bundle bundle = new Bundle();
        ArrayList<String> eventLocation = new ArrayList<>();
        for(int i=0; i<data.size(); i++){
            try {
                eventLocation.add(data.get(i).getString("event_location"));
            } catch (JSONException e) {e.printStackTrace();}
        } bundle.putStringArrayList("eventLocation", eventLocation);
        return eventLocation;
    }
    public static ArrayList<String> getHosterBundle(ArrayList<JSONObject> data){
        Bundle bundle = new Bundle();
        ArrayList<String> eventHoster = new ArrayList<>();
        for(int i=0; i<data.size(); i++){
            try {
                eventHoster.add(data.get(i).getString("event_sponsor"));
            } catch (JSONException e) {e.printStackTrace();}
        } bundle.putStringArrayList("eventHoster", eventHoster);
        return eventHoster;
    }
}
