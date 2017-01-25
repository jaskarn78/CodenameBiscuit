package com.example.codenamebiscuit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends FragmentActivity{
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;
    private LoginButton mLoginButton;
    private String mUserEmail;

    private static final String DATABASE_CONNECTION_LINK =
            "http://athena.ecs.csus.edu/~teamone/php/query.php";
    private static final String DATABASE_ID_FIELD_NAME = "user_id";

    // Login requests permission to access user's email and friends list
    private final List<String> mPermissions = Arrays.asList("email", "user_friends");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mCallbackManager = CallbackManager.Factory.create();

        initializeTokens();

        // Initialize Facebook LoginButton
        mLoginButton = (LoginButton)findViewById(R.id.facebook_login_button);

        // Set permissions and register the callback
        mLoginButton.setReadPermissions(mPermissions);
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //getUserFacebookEmail(); // pull email from facebook
                checkIfProfileExists(); // check if user exists on database
                //nextActivity();
            }

            @Override
            public void onCancel() {
                Log.v("FacebookLogin: ", "Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("FacebookLogin: ", error.toString());
            }
        });
    }

    /**
     * initializeTokens initializes the AccessToken, AccessTokenTracker, and ProfileTracker.
     *
     */
    private void initializeTokens() {
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Change tracked AccessToken to currentAccessToken
                AccessToken.setCurrentAccessToken(currentAccessToken);
            }
        };

        // Initialize ProfileTracker
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // Change tracked Profile to currentProfile
                Profile.setCurrentProfile(currentProfile);
            }
        };

        mAccessTokenTracker.startTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Facebook login
        nextActivity();
        //checkIfProfileExists();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProfileTracker.stopTracking();
        mAccessTokenTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        mCallbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    /**********************************************************************************************
     Request Facebook Info
     **********************************************************************************************/


    /**********************************************************************************************
         Profile Checking
     **********************************************************************************************/

    // Check if the user profile exists on the database, if not create one
    private void checkIfProfileExists() {
        String userId = AccessToken.getCurrentAccessToken().getUserId();
        new SigninActivity().execute(DATABASE_CONNECTION_LINK, DATABASE_ID_FIELD_NAME, userId);
    }

    // If there is an active AccessToken, go to the MainActivity
    private void nextActivity() {
        if (AccessToken.getCurrentAccessToken() != null) {
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(main);
        }
    }
}
