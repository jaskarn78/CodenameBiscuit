package com.example.codenamebiscuit;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.codenamebiscuit.helper.DistanceCalculator;
import com.example.codenamebiscuit.helper.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by jaskarnjagpal on 4/11/17.
 */

public class Events implements Serializable{
    private String eventId;
    private String eventName;
    private String eventImage;
    private String preference1;
    private String preference2;
    private String preference3;
    private Double lat, lng;
    private String eventHoster;
    private String eventDescription;
    private String eventLocation;
    private String eventCost;
    private String eventStartTime;
    private String eventEndTime;
    private String eventStartDate;
    private String eventEndDate;
    private String eventWebsite;
    private String Origin, destination;
    private String currentLat, currentLng;

    public Events() {

    }

    public String getEventId(){
        return this.eventId;
    }
    private String getEventName(){
        return this.eventName;
    }
    private String getPreference1(){
        return this.preference1;
    }
    private String getPreference2(){
        return this.preference2;
    }
    private String getPreference3(){
        return this.preference3;
    }
    private Double getLat(){
        return this.lat;
    }
    private Double getLng(){
        return this.lng;
    }
    private String getEventImage(){
        return this.eventImage;
    }
    private String getEventHoster(){
        return this.eventHoster;
    }
    private String getEventCost(){
        return this.eventCost;
    }
    private String getEventStartTime(){
        return this.eventStartTime;
    }
    private String getEventStartDate(){
        return this.eventStartDate;
    }
    private String getEventEndDate(){
        return this.eventEndDate;
    }
    private String getEventEndTime(){
        return this.eventEndTime;
    }
    private String getEventDescaription(){
        return this.eventDescription;
    }
    private String getEventLocation(){
        return this.eventLocation;
    }

    private String getEventWebsite(){
        return this.eventWebsite;
    }


    public static Events fromJson(JSONObject jsonObject){
        Events events = new Events();
        try{
            events.eventName = jsonObject.getString("event_name");
            events.eventId = jsonObject.getString("event_id");
            events.eventImage = jsonObject.getString("img_path");
            events.preference1 = jsonObject.getString("preference_name");
            events.lat = jsonObject.getDouble("lat");
            events.lng = jsonObject.getDouble("lng");
            events.eventHoster = jsonObject.getString("event_sponsor");
            events.eventDescription = jsonObject.getString("event_description");
            events.eventLocation = jsonObject.getString("event_location");
            events.eventCost = jsonObject.getString("event_cost");
            events.eventStartTime = jsonObject.getString("start_time");
            events.eventEndTime = jsonObject.getString("end_time");
            events.eventStartDate = jsonObject.getString("start_date");
            events.eventEndDate = jsonObject.getString("end_date");
            events.eventWebsite = jsonObject.getString("event_website");

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return events;
    }



    public static ArrayList<Events> fromJson(ArrayList<JSONObject> jsonArray, Activity activity){
        JSONObject event;
        Location currLocation = currentLocation(activity);
        ArrayList<Events> eventsList = new ArrayList<>(jsonArray.size());
        for(int i=0; i<jsonArray.size(); i++){
            try{
                event = jsonArray.get(i);
                event.put("event_distance",calculateDistance(currLocation,event.getDouble("lat"), event.getDouble("lng")));
            }catch(Exception e){
                e.printStackTrace();
                continue;
            }
            Events events = Events.fromJson(event);
            if(events !=null){
                eventsList.add(events);
            }
        }
        Collections.sort(jsonArray, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                if(getDistance(o1)==getDistance(o2))
                    return 0;
                else
                    return getDistance(o1)-getDistance(o2);
            }
        });
        return eventsList;
    }

    public static void toFurthest(ArrayList<JSONObject> eventList){
        Collections.sort(eventList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                if(getDistance(o1)==getDistance(o2))
                    return 0;
                else
                    return getDistance(o2)-getDistance(o1);
            }
        });
    }

    public static void toEarliest(ArrayList<JSONObject> eventList){
        Collections.sort(eventList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                if(getDate(getDate(o1))==getDate((getDate(o2))))
                    return 0;
                else if(getDate(getDate(o1)).after(getDate((getDate(o2)))))
                    return 1;
                else return -1;
            }
        });
    }

    public static void toLatest(ArrayList<JSONObject>eventList){
        Collections.sort(eventList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                if(getDate(getDate(o1))==getDate((getDate(o2))))
                    return 0;
                else if(getDate(getDate(o1)).before(getDate((getDate(o2)))))
                    return 1;
                else return -1;
            }
        });
    }


    public static int calculateDistance(Location location2, double eventLat, double eventLng) {
        double distance;
        Location location1 = new Location("");
        location1.setLongitude(eventLng);
        location1.setLatitude(eventLat);
        distance = location1.distanceTo(location2);
        return (int)Math.round(distance*0.000621371192);

    }


    public static Location currentLocation(Activity activity){
        GPSTracker gps = new GPSTracker(activity.getApplicationContext());
        Location curLocation = new Location("");
        if(gps.canGetLocation()){
            curLocation.setLatitude(gps.getLatitude());
            curLocation.setLongitude(gps.getLongitude());
        }
        return curLocation;
    }

    public static String getDate(JSONObject object){
        try { return object.getString("start_date");
        }catch(JSONException e){e.printStackTrace();}
        return null;
    }
    public static int getDistance(JSONObject object){
        try {
            return object.getInt("event_distance");
        } catch (JSONException e) { e.printStackTrace(); }
        return 0;
    }

    public static Date getDate(String dateString) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try { date = sdf.parse(dateString);
        } catch (ParseException e) { e.printStackTrace(); }
        return date; }

}
