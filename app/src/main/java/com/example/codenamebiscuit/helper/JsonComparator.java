package com.example.codenamebiscuit.helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jaskarnjagpal on 4/11/17.
 */

public class JsonComparator{
    private ArrayList<JSONObject> array;

    public JsonComparator(ArrayList<JSONObject> array){
        this.array=array;
    }

    public ArrayList<JSONObject> sortJsonArray(){
        List<JSONObject> jsons = new ArrayList<>();
        for(int i=0; i<array.size(); i++){
            jsons.add(array.get(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                Double loc1 = null;
                Double loc2 = null;
                try {
                    loc1 = o1.getDouble("location1");
                    loc2 = o2.getDouble("location2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return loc1.compareTo(loc2);
            }
        });
        return new ArrayList<JSONObject>(jsons);
    }

}
