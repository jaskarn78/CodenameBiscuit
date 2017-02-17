package com.example.codenamebiscuit;

/**
 * Created by jaskarnjagpal on 1/31/17.
 */
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.codenamebiscuit.helper.App;
import com.example.codenamebiscuit.login.ChooseLogin;


public class SplashScreen extends Activity {
    //Splash screen timer
    private static int SPLASH_TIME_OUT;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progress = (ProgressBar) findViewById(R.id.progressBar1);
        progress.getIndeterminateDrawable().setColorFilter(Color.rgb(255, 157, 252), PorterDuff.Mode.MULTIPLY);

        //Toast.makeText(this, "Loading...Please Wait", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //this method will executed once timer runs out
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);

                //close the activity
                finish();
            }
        }, SPLASH_TIME_OUT);
        SPLASH_TIME_OUT=3000;
    }


}
