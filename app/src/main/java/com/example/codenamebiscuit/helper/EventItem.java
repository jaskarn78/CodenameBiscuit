package com.example.codenamebiscuit.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.codenamebiscuit.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by jaskarnjagpal on 4/26/17.
 */

public class EventItem implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    public final String image;
    private Bitmap bitmap;
    private Drawable d;

    public EventItem(double lat, double lng, String mImage, String mTitle){
        mPosition=new LatLng(lat, lng);
        this.mTitle=mTitle;
        mSnippet= null;
        image = mImage;
    }
    public EventItem(double lat, double lng, String title, String snippet, String mImage){
        mPosition = new LatLng(lat, lng);
        mTitle=title;
        mSnippet=snippet;
        image=mImage;
    }



    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
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
