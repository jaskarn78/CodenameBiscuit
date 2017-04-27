package com.example.codenamebiscuit.helper;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.codenamebiscuit.R;

/**
 * Created by jaskarnjagpal on 4/26/17.
 */

public class ImageLoader {
    public static void loadImageFromUri(Context context, Uri uri, ImageView imageView){
        Glide.with(context).load(uri).centerCrop().into(imageView);
    }
    public static void loadCroppedImage(Context context, String imgPath, ImageView imageView, final ProgressBar progressBar){
        Glide.with(context).load(getPath(imgPath)).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }
    public static void loadFullImage(Context context, String imgPath, ImageView imageView, final ProgressBar progressBar){
        Glide.with(context).load(getPath(imgPath))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }
    public static void loadImage(Context context, String imgPath, ImageView imageView, final ProgressBar progressBar){
        Glide.with(context).load(getPath(imgPath)).error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }
    public static void loadImage(Context context, String imgPath, ImageView imageView){
        Glide.with(context).load(getPath(imgPath)).diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.placeholder).into(imageView);
    }
    public static void loadBackgroundImage(Context context, String imgPath, ImageView imageView){
        Glide.with(context).load(getPath(imgPath)).error(R.drawable.placeholder)
                .thumbnail(0.50f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }
    public static void loadBackgroundResource(Context context, int imgPath, ImageView imageView){
        Glide.with(context).load(imgPath).error(R.drawable.placeholder)
                .thumbnail(0.25f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .sizeMultiplier(0.25f)
                .centerCrop()
                .fitCenter().load(imgPath)
                .into(imageView);
    }
    public static String getPath(String path){
        return "http://athena.ecs.csus.edu/~teamone/events/"+path;
    }
}
