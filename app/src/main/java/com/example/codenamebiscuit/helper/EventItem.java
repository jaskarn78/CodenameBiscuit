package com.example.codenamebiscuit.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by jaskarnjagpal on 4/26/17.
 */

public class EventItem implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private String mImage;
    private Bitmap bitmap;
    private Drawable drawable;

    public EventItem(double lat, double lng, String mImage, String mTitle, Drawable eventImage){
        mPosition=new LatLng(lat, lng);
        this.mTitle=mTitle;
        mSnippet= null;
        this.mImage = mImage;
        drawable = eventImage;
    }
    public EventItem(double lat, double lng, String title, String snippet, String mImage){
        mPosition = new LatLng(lat, lng);
        mTitle=title;
        mSnippet=snippet;
        this.mImage = mImage;
    }

    public Drawable getDrawable(){
        return drawable;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public String getImage(){
        return mImage;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSnippet(String snippet) {
        mSnippet = snippet;
    }


}
