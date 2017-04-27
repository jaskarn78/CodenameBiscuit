package com.example.codenamebiscuit.helper;

/**
 * Created by jaskarnjagpal on 1/31/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.codenamebiscuit.MainActivity;


public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
