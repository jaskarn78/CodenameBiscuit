package com.example.codenamebiscuit.helper;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;
import android.app.Application;


/**
 * Created by jaskarnjagpal on 2/7/17.
 */

public class App extends Application {

    private GoogleApiHelper googleApiHelper;
    private GoogleApiClient mGoogleApiClient;


    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        googleApiHelper = new GoogleApiHelper(getApplicationContext());
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }

    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }
    public void setClient(GoogleApiClient client){
        mGoogleApiClient = client;
    }
    public GoogleApiClient getClient(){
        return mGoogleApiClient;
    }

}