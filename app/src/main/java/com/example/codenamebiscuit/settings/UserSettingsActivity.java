package com.example.codenamebiscuit.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.helper.App;
import com.example.codenamebiscuit.helper.ChangePreferences;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;




public class UserSettingsActivity
        extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView mNameView; // User's name


    private boolean mMusicPreference;
    private boolean mFoodDrinkPreference;
    private boolean mSportsPreference;
    private boolean mOutdoorPreference;
    private boolean mHealthFitnessPreference;
    private boolean mFamilyFriendlyPreference;
    private boolean mRetailPreference;
    private boolean mPerformingArtsPreference;
    private boolean mEntertainmentPreference;
    private Toolbar toolbar;
    private JSONObject pref = new JSONObject();
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        //Remove notification bar
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_user_settings);
        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tv = (TextView)findViewById(R.id.toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Black.ttf");
        tv.setTypeface(typeface);


        Profile fbprofile = Profile.getCurrentProfile();
        if(fbprofile==null) {

            Uri pic = Uri.parse(prefs.getString("user_image", null));
            String fName = prefs.getString("fName", null);
            String lName = prefs.getString("lName", null);
            initializeGoogleProfileInfo(fName, lName, pic);
        }


        if (fbprofile != null)
            initializeFBProfileInfo(fbprofile);

        try {
            setupSharedPreferences();
            initializeLogoutButton();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
        /**
         * initializeProfileInfo
         * Takes a Bundle, and uses information in Bundle to create the ImageView and TextView
         *
         * @param profile
         */
    private void initializeFBProfileInfo(Profile profile) {
        String name = profile.getFirstName();
        String surname = profile.getLastName();
        String imageUrl = profile.getProfilePictureUri(200, 200).toString();

        //        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Picasso.with(this).load(imageUrl).centerCrop().fit().into((ImageView)findViewById(R.id.pref_user_image));

        mNameView = (TextView) findViewById(R.id.pref_user_name);
        mNameView.setText(name + " " + surname);
    }

    private void initializeGoogleProfileInfo(String fName, String lName, Uri url){
        Picasso.with(this).load(url).centerCrop().fit().into((ImageView)findViewById(R.id.pref_user_image));
        mNameView = (TextView) findViewById(R.id.pref_user_name);
        mNameView.setText(fName + " " + lName);
    }
    /**
     * initializeLogoutButton creates the button that will log the user out of the application
     */
    private void initializeLogoutButton() {
        // Create the Logout button
        Button logout = (Button) findViewById(R.id.user_logout_button);
        Log.i("google login", App.getInstance().getGoogleApiHelperInstance().getApiClient().isConnected()+"");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit().clear().apply();
                Intent login = new Intent(UserSettingsActivity.this, ChooseLogin.class);
                startActivity(login);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupSharedPreferences() throws JSONException {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mMusicPreference = sharedPreferences.getBoolean(getString(R.string.pref_music_key),
                getResources().getBoolean(R.bool.pref_music_value));
        mFoodDrinkPreference = sharedPreferences.getBoolean(getString(R.string.pref_food_drink_key),
                getResources().getBoolean(R.bool.pref_food_drink_value));
        mSportsPreference = sharedPreferences.getBoolean(getString(R.string.pref_sports_key),
                getResources().getBoolean(R.bool.pref_sports_value));
        mOutdoorPreference = sharedPreferences.getBoolean(getString(R.string.pref_outdoor_key),
                getResources().getBoolean(R.bool.pref_outdoor_value));
        mHealthFitnessPreference = sharedPreferences.getBoolean(getString(R.string.pref_health_fitness_key),
                getResources().getBoolean(R.bool.pref_health_fitness_value));
        mFamilyFriendlyPreference = sharedPreferences.getBoolean(getString(R.string.pref_family_friendly_key),
                getResources().getBoolean(R.bool.pref_family_friendly_value));
        mRetailPreference = sharedPreferences.getBoolean(getString(R.string.pref_retail_key),
                getResources().getBoolean(R.bool.pref_retail_value));
        mPerformingArtsPreference = sharedPreferences.getBoolean(getString(R.string.pref_performing_arts_key),
                getResources().getBoolean(R.bool.pref_performing_arts_value));
        mEntertainmentPreference = sharedPreferences.getBoolean(getString(R.string.pref_entertainment_key),
                getResources().getBoolean(R.bool.pref_entertainment_value));

        pref.put("pref_id1", mMusicPreference);
        pref.put("pref_id2", mFoodDrinkPreference);
        pref.put("pref_id3", mSportsPreference);
        pref.put("pref_id4", mOutdoorPreference);
        pref.put("pref_id5", mHealthFitnessPreference);
        pref.put("pref_id6", mFamilyFriendlyPreference);
        pref.put("pref_id7", mRetailPreference);
        pref.put("pref_id8", mPerformingArtsPreference);
        pref.put("pref_id9", mEntertainmentPreference);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.pref_music_key))) {
            mMusicPreference = sharedPreferences.getBoolean(getString(R.string.pref_music_key),
                    getResources().getBoolean(R.bool.pref_music_value));
            if (mMusicPreference) {
                try {
                    pref.put("pref_id1", mMusicPreference);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    pref.put("pref_id1", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        if (key.equals(getString(R.string.pref_food_drink_key))) {
            mFoodDrinkPreference = sharedPreferences.getBoolean(getString(R.string.pref_food_drink_key),
                    getResources().getBoolean(R.bool.pref_food_drink_value));
            if (mFoodDrinkPreference) {
                try {
                    pref.put("pref_id2", mFoodDrinkPreference);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    pref.put("pref_id2", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (key.equals(getString(R.string.pref_sports_key))) {
            mSportsPreference = sharedPreferences.getBoolean(getString(R.string.pref_sports_key),
                    getResources().getBoolean(R.bool.pref_sports_value));
            if (mSportsPreference) {
                try {
                    pref.put("pref_id3", mSportsPreference);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    pref.put("pref_id3", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (key.equals(getString(R.string.pref_outdoor_key))) {
            mOutdoorPreference = sharedPreferences.getBoolean(getString(R.string.pref_outdoor_key),
                    getResources().getBoolean(R.bool.pref_outdoor_value));
            if (mOutdoorPreference) {
                try {
                    pref.put("pref_id4", mOutdoorPreference);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    pref.put("pref_id4", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (key.equals(getString(R.string.pref_health_fitness_key))) {
            mHealthFitnessPreference = sharedPreferences.getBoolean(getString(R.string.pref_health_fitness_key),
                    getResources().getBoolean(R.bool.pref_health_fitness_value));
            if (mHealthFitnessPreference) {
                try {
                    pref.put("pref_id5", mHealthFitnessPreference);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    pref.put("pref_id5", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (key.equals(getString(R.string.pref_family_friendly_key))) {
            mFamilyFriendlyPreference = sharedPreferences.getBoolean(getString(R.string.pref_family_friendly_key),
                    getResources().getBoolean(R.bool.pref_family_friendly_value));
            if (mFamilyFriendlyPreference) {
                try {
                    pref.put("pref_id6", mFamilyFriendlyPreference);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    pref.put("pref_id6", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (key.equals(getString(R.string.pref_retail_key))) {
            mRetailPreference = sharedPreferences.getBoolean(getString(R.string.pref_retail_key),
                    getResources().getBoolean(R.bool.pref_retail_value));
            if (mRetailPreference) {
                try {
                    pref.put("pref_id7", mRetailPreference);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    pref.put("pref_id7", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (key.equals(getString(R.string.pref_performing_arts_key))) {
            mPerformingArtsPreference = sharedPreferences.getBoolean(getString(R.string.pref_performing_arts_key),
                    getResources().getBoolean(R.bool.pref_performing_arts_value));
            if (mPerformingArtsPreference) {
                try {
                    pref.put("pref_id8", mPerformingArtsPreference);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    pref.put("pref_id8", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (key.equals(getString(R.string.pref_entertainment_key))) {
            mEntertainmentPreference = sharedPreferences.getBoolean(getString(R.string.pref_entertainment_key),
                    getResources().getBoolean(R.bool.pref_entertainment_value));
            if (mEntertainmentPreference) {
                try {
                    pref.put("pref_id9", mEntertainmentPreference);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    pref.put("pref_id9", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if(AccessToken.getCurrentAccessToken()!=null) {
                //pref.put("user_id", prefs.getString("user_idG", null));
                pref.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
            }

            else if(prefs.getString("user_id", null)!=null) {
                pref.put("user_id", prefs.getString("user_id", null));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //new ChangePreferences().execute(pref);
    }


}

