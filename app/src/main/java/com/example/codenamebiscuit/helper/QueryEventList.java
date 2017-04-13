package com.example.codenamebiscuit.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.codenamebiscuit.MainActivity;
import com.example.codenamebiscuit.rv.EventAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaskarnjagpal on 2/2/17.
 */

public class QueryEventList extends AsyncTask<Void, Void, ArrayList<JSONObject>> {

    private ArrayList<JSONObject> eventList;
    private JSONObject userJSON;
    private String main_events;
    private EventAdapter adapter;
    private Context context;
    private RecyclerView recyclerView;
    ProgressDialog dialog;


    public QueryEventList(String main_events, String userId){
        this.main_events=main_events;
        userJSON = new JSONObject();
        try {
            userJSON.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected ArrayList<JSONObject> doInBackground(Void... params) {

        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        try {
            String data;
            if (userJSON != null)
                data = userJSON.toString();  // data is the JSONObject being sent to the php server
            else
                return null;

            // Connect to the URL
            URL url = new URL(main_events);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());

            // POST the information to the URL
            wr.write(data);
            wr.flush();

            // Create a means to read the output from the PHP
            reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                Log.v("PrintLine", line);
                sb.append(line);
            }

            JSONArray jArray;
            jArray = new JSONArray(sb.toString());
            eventList = new ArrayList<JSONObject>();

            for (int i = 0; i < jArray.length(); i++) {
                eventList.add(jArray.getJSONObject(i));
                //Log.v("PrintLine", eventList.get(i).toString());
            }

            wr.close(); // close OutputStreamWriter
            reader.close(); // close BufferedReader

            return eventList;
        } catch (MalformedURLException e) {
            Log.e("MalformedURL", e.toString());
            return null;
        } catch (IOException e) {
            Log.e("IOException", e.toString());
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onPostExecute(ArrayList<JSONObject> objs) {
        if (objs != null) {
            setEventList(objs);
        }

    }

    public void setEventList(ArrayList<JSONObject> list){
         eventList = list;
    }
}