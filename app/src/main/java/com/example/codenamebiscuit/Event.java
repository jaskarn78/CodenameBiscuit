package com.example.codenamebiscuit;

import org.json.JSONObject;

import java.util.ArrayList;

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

    public Event(ArrayList<JSONObject> data){

    }
}
