package com.example.codenamebiscuit;

import android.app.ProgressDialog;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class FacebookLoginActivity extends FragmentActivity{
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;
    private ProgressDialog mProgressDialog;

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
        LoginButton mLoginButton = (LoginButton)findViewById(R.id.facebook_login_button);

        // Set permissions and register the callback
        mLoginButton.setReadPermissions(mPermissions);

        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                System.out.println("onSuccess");
                mProgressDialog = new ProgressDialog(FacebookLoginActivity.this);
                mProgressDialog.setMessage("Processing data...");
                mProgressDialog.show();
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("FacebookLoginActivity", response.toString());
                        // Get facebook data from login
                        JSONObject bFacebookData = getFacebookData(object);
                        try {
                            Log.v("DATA BITCH", bFacebookData.toString());

                            // Update Database
                            SigninActivity userSignin = new SigninActivity();
                            userSignin.execute(bFacebookData);

                            nextActivity();

                        } catch (NullPointerException e) {
                            Log.e("ERRR", e.toString());
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,first_name,last_name,email,gender,birthday,location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println("onError");
                Log.v("FacebookLoginActivity", exception.getCause().toString());
            }
        });
    }

    /**********************************************************************************************
     Facebook Methods
     **********************************************************************************************/

    /**
     * getFacebookData(JSONObject object)
     *  Takes a JSONObject full of the user's facebook information, and converts it to a normalized
     *  version of the data to be used with the CodenameBiscuit application.
     * @param object
     * @return normalizedObj
     */
    private JSONObject getFacebookData(JSONObject object) {

        try {
            JSONObject normalizedObj = new JSONObject();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                normalizedObj.put("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            normalizedObj.put("user_id", id);
            if (object.has("first_name"))
                normalizedObj.put("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                normalizedObj.put("last_name", object.getString("last_name"));
            if (object.has("email"))
                normalizedObj.put("user_email", object.getString("email"));
            if (object.has("gender"))
                normalizedObj.put("gender", object.getString("gender"));
            if (object.has("birthday"))
                normalizedObj.put("birthday", object.getString("birthday"));
            if (object.has("location"))
                normalizedObj.put("location", object.getJSONObject("location").getString("name"));


            /** For Now... **/
            normalizedObj.put("user_name", object.getString("first_name")
                    + " " + object.getString("last_name"));

            return normalizedObj;
        }
        catch(JSONException e) {
            Log.d("JSONException", e.toString());
            return null;
        }
    }



    /**
     * initializeTokens initializes the AccessToken, AccessTokenTracker, and ProfileTracker.
     */
    private void initializeTokens() {
        // Initialize AccessToken Tracker
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
        //nextActivity();
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
         Profile Checking
     **********************************************************************************************/

    // If there is an active AccessToken, go to the MainActivity
    private void nextActivity() {
        if (AccessToken.getCurrentAccessToken() != null) {
            Intent main = new Intent(FacebookLoginActivity.this, MainActivity.class);
            startActivity(main);
        }
    }
}
