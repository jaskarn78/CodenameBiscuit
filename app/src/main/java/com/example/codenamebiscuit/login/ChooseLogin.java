package com.example.codenamebiscuit.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.example.codenamebiscuit.MainActivity;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.App;
import com.example.codenamebiscuit.helper.DatabaseHelper;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jaskarnjagpal on 2/6/17.
 */

public class ChooseLogin extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {
    private final List<String> mPermissions = Arrays.asList("email", "user_friends");
    LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;
    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private SharedPreferences pref;
    private DatabaseHelper db;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        mLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        mGoogleApiClient = App.getGoogleApiHelper().getApiClient();
        db = new DatabaseHelper(this);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupViaGoogle();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupViaFB();

        }
        });
    }

    private  void setupViaGoogle(){

        signIn();
        App.getGoogleApiHelper().connect();

    }
    private void handleSignInResult(GoogleSignInResult result) throws JSONException {

        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            App.getGoogleApiHelper().setSignInResult(result);
            GoogleSignInAccount acct = App.getGoogleApiHelper().getSignInAccount();

            pref.edit().putString("user_idG", acct.getId()).apply();
            JSONObject obj = new JSONObject();
            obj.put("user_id", acct.getId());
            obj.put("first_name", acct.getGivenName());
            obj.put("last_name", acct.getFamilyName());
            obj.put("user_email", acct.getEmail());
            obj.put("gender", "M/F");
            db.insertPerson("0", acct.getGivenName(), acct.getFamilyName(), String.valueOf(acct.getPhotoUrl()));


            try {
                if (obj != null) {
                    Log.v("DATA BITCH", obj.toString());
                }

                // Update Database
                SigninActivity userSignin = new SigninActivity();
                userSignin.execute(obj);

            } catch (NullPointerException e) {
                Log.e("ERRR", e.toString());
            }


        }
        nextActivity();
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setupViaFB(){
        // Set permissions and register the callback
        mLoginButton.setReadPermissions(mPermissions);
        mCallbackManager = CallbackManager.Factory.create();

        mCallbackManager = CallbackManager.Factory.create();
        initializeTokens();


        // Initialize Facebook LoginButton
        LoginButton mLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);

        // Set permissions and register the callback

        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                System.out.println("onSuccess");
                mProgressDialog = new ProgressDialog(ChooseLogin.this);
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
                                JSONObject bFacebookData = createNormalizedJSONObject(object);
                                try {
                                    if (bFacebookData != null) {
                                        Log.v("DATA BITCH", bFacebookData.toString());
                                    }

                                    // Update Database
                                    SigninActivity userSignin = new SigninActivity();
                                    userSignin.execute(bFacebookData);


                                } catch (NullPointerException e) {
                                    Log.e("ERRR", e.toString());
                                }
                                nextActivity();

                            }


                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender,birthday,location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }


        });


    }

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

    private void nextActivity() {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
    }

    private JSONObject createNormalizedJSONObject(JSONObject object) {

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


            return normalizedObj;
        } catch (JSONException e) {
            Log.d("JSONException", e.toString());
            return null;
        }
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
        if(mProfileTracker!=null) {
            mProfileTracker.stopTracking();
            mAccessTokenTracker.stopTracking();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            try {
                handleSignInResult(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            //Facebook login
            mCallbackManager.onActivityResult(requestCode, responseCode, intent);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
