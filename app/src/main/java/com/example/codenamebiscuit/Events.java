package com.example.codenamebiscuit;

import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jaskarnjagpal on 4/11/17.
 */

public class Events {
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
            events.eventCost = jsonObject.getString("cost");
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

    public static ArrayList<Events> fromJson(ArrayList<JSONObject> jsonArray){
        JSONObject event;
        ArrayList<Events> eventsList = new ArrayList<>(jsonArray.size());
        for(int i=0; i<jsonArray.size(); i++){
            try{
                event = jsonArray.get(i);
            }catch(Exception e){
                e.printStackTrace();
                continue;
            }
            Events events = Events.fromJson(event);
            if(events !=null){
                eventsList.add(events);
            }
        }
        return eventsList;
    }
}
