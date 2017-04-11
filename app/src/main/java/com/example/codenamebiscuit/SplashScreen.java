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
import android.os.AsyncTask;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
