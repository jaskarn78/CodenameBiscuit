package com.example.codenamebiscuit.helper;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jaskarnjagpal on 4/20/17.
 */

public class EventBundle {
    private ArrayList<JSONObject> data;
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
            bundle.putString("eventPhone", data.get(position).getString("event_phone"));
        } catch (JSONException e) { e.printStackTrace(); }
        return bundle;
    }

    public ArrayList<String> getEventStringList(){
        ArrayList<String> events = new ArrayList<>();
        for(JSONObject obj : data){
            try {
                events.add(obj.getString("event_name")+" By " + ""+obj.getString("event_sponsor"));
            } catch (JSONException e) { e.printStackTrace(); }
        }
        return events;
    }

}
