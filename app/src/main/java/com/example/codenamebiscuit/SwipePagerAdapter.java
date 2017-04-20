package com.example.codenamebiscuit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jaskarnjagpal on 4/19/17.
 */

public class SwipePagerAdapter extends PagerAdapter{
    Context context;
    ArrayList<JSONObject> data;

    public SwipePagerAdapter(Context context){
        this.context=context;
    }
    public void setPagerData(ArrayList<JSONObject> data){
        this.data=data;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.standard_launch, null);
        TextView eventName = (TextView)itemView.findViewById(R.id.slidename);
        TextView eventLocation = (TextView)itemView.findViewById(R.id.slideLocation);
        TextView eventPreference = (TextView)itemView.findViewById(R.id.slidePref);
        TextView eventHoster = (TextView)itemView.findViewById(R.id.slideHoster);
        ImageView eventImage = (ImageView)itemView.findViewById(R.id.slideImage);

        try {
            String imagePager= getImageURL(data.get(position).getString("img_path"));
            eventName.setText(data.get(position).getString("event_name"));
            eventLocation.setText(data.get(position).getString("event_location"));
            eventPreference.setText(data.get(position).getString("preference_name"));
            eventHoster.setText("Presented By: "+(data.get(position)).getString("event_sponsor"));
            Glide.with(context).load(imagePager).placeholder(R.drawable.progress).into(eventImage); }

        catch (JSONException e) { e.printStackTrace();}


        container.addView(itemView);
        return itemView; }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private String getImageURL(String path) {
        return context.getString(R.string.IMAGE_URL_PATH) + path; }

    private Bundle getBundle(int position) {
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
        return bundle; }
}