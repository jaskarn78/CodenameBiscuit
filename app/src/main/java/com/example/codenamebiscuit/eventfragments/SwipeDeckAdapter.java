package com.example.codenamebiscuit.eventfragments;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.codenamebiscuit.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Assigns values to views within the cards
 * Handles flip animation to reveal additional event information
 * inflates layout cards.xml and assigns values to the views
 */
public class SwipeDeckAdapter extends BaseAdapter {
    private ArrayList<JSONObject> data;
    private ViewPager viewPager;
    private Activity activity;
    private String image;

    public SwipeDeckAdapter(ViewPager viewPager, Activity activity, ArrayList<JSONObject> data) {
        this.viewPager=viewPager;
        this.activity=activity;
        this.data=data;
    }

    @Override
    public int getCount() { return data.size(); }

    public void clear(){ data.clear(); }

    @Override
    public JSONObject getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View v = convertView;
        if(v==null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            v = inflater.inflate(R.layout.cards, parent, false);
        }
        viewPager.setCurrentItem(position-1);
        try {image = getImageURL(data.get(position).getString("img_path")); } catch (JSONException e) { e.printStackTrace();}
        /**
         * initialize all views on the back side of the card
         * assign values to all views
         * event information retrieved from json array testData
         */
        ImageView frontCardImage = (ImageView) v.findViewById(R.id.offer_image);
        Picasso.with(frontCardImage.getContext())
                .load(image)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.progress)
                .into(frontCardImage);
        return v;
    }

    private String getImageURL(String path) {
        Log.i("image: ",activity.getString(R.string.IMAGE_URL_PATH)+path);
        return activity.getString(R.string.IMAGE_URL_PATH) + path; }

}
