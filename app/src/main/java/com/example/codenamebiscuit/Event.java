package com.example.codenamebiscuit;



import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by jaskarnjagpal on 4/18/17.
 */

public class Event {
    private String eventId;         private String eventName;           private String eventImage;
    private String preference1;     private String preference2;         private String preference3;
    private Double lat, lng;        private String eventHoster;         private String eventDescription;
    private String eventLocation;   private String eventCost;           private String eventStartTime;
    private String eventEndTime;    private String eventStartDate;      private String eventEndDate;
    private String eventWebsite;    private String Origin, destination; private String currentLat, currentLng;

    public Event(JSONObject data) throws JSONException {
        this.eventName = data.getString("event_name");
        this.eventImage = getImageString(data.getString("img_path"));
        this.lat = data.getDouble("lat");
        this.lng = data.getDouble("lng");

    }


    private String getImageString(String fileName){
        return "http://athena.ecs.csus.edu/~teamone/events/"+fileName;
    }
    public String name(){
        return eventName;
    }
    public String image(){
        return eventImage;
    }
}
