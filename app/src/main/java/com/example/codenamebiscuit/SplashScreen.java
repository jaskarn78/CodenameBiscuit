package com.example.codenamebiscuit;

/**
 * Created by jaskarnjagpal on 1/31/17.
 */
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.codenamebiscuit.helper.App;
import com.example.codenamebiscuit.helper.GoogleApiHelper;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.google.android.gms.common.api.GoogleApiClient;
import com.muddzdev.styleabletoastlibrary.StyleableToast;


public class SplashScreen extends Activity {
    //Splash screen timer
    private static int SPLASH_TIME_OUT;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //progress = (ProgressBar) findViewById(R.id.progressBar1);
       //progress.getIndeterminateDrawable().setColorFilter(Color.rgb(255, 157, 252), PorterDuff.Mode.MULTIPLY);

        //StyleableToast st = new StyleableToast(getApplicationContext(), "LOADING EVENTS...", Toast.LENGTH_SHORT);
        //setupWindowAnimations();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }


}
