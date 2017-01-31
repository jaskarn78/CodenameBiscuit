package com.example.codenamebiscuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.codenamebiscuit.helper.RoundedImageView;
import com.example.codenamebiscuit.login.FacebookLoginActivity;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;

public class UserSettingsActivity
        extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView mNameView; // User's name

    private String mPreferenceString;

    private boolean mMusicPreference;
    private boolean mFoodDrinkPreference;
    private boolean mSportsPreference;
    private boolean mOutdoorPreference;
    private boolean mHealthFitnessPreference;
    private boolean mFamilyFriendlyPreference;
    private boolean mRetailPreference;
    private boolean mPerformingArtsPreference;
    private boolean mEntertainmentPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_user_settings);

        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Profile profile = Profile.getCurrentProfile();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (profile != null) {
            initializeProfileInfo(profile);
        }

        initializeLogoutButton();
        setupSharedPreferences();
    }

    /**
     * initializeProfileInfo
     *  Takes a Bundle, and uses information in Bundle to create the ImageView and TextView
     * @param profile
     */
    private void initializeProfileInfo(Profile profile) {
        String name = profile.getFirstName();
        String surname = profile.getLastName();
        String imageUrl = profile.getProfilePictureUri(200,200).toString();

        new DownloadImage((RoundedImageView)findViewById(R.id.pref_user_image)).execute(imageUrl);

        mNameView = (TextView) findViewById(R.id.pref_user_name);
        mNameView.setText(name + " " + surname);
    }

    /**
     * initializeLogoutButton creates the button that will log the user out of the application
     */
    private void initializeLogoutButton() {
        // Create the Logout button
        Button logout = (Button) findViewById(R.id.user_logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent login = new Intent(UserSettingsActivity.this, FacebookLoginActivity.class);
                startActivity(login);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the VisualizerActivity
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSharedPreferences() {
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

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        createPreferenceString();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_music_key))) {
            mMusicPreference = sharedPreferences.getBoolean(getString(R.string.pref_music_key),
                    getResources().getBoolean(R.bool.pref_music_value));
        } else if (key.equals(getString(R.string.pref_food_drink_key))) {
            mFoodDrinkPreference = sharedPreferences.getBoolean(getString(R.string.pref_food_drink_key),
                    getResources().getBoolean(R.bool.pref_food_drink_value));
        } else if (key.equals(getString(R.string.pref_sports_key))) {
            mSportsPreference = sharedPreferences.getBoolean(getString(R.string.pref_sports_key),
                    getResources().getBoolean(R.bool.pref_sports_value));
        } else if (key.equals(getString(R.string.pref_food_drink_key))) {
            mOutdoorPreference = sharedPreferences.getBoolean(getString(R.string.pref_outdoor_key),
                    getResources().getBoolean(R.bool.pref_outdoor_value));
        } else if (key.equals(getString(R.string.pref_food_drink_key))) {
            mHealthFitnessPreference = sharedPreferences.getBoolean(getString(R.string.pref_health_fitness_key),
                    getResources().getBoolean(R.bool.pref_health_fitness_value));
        } else if (key.equals(getString(R.string.pref_food_drink_key))) {
            mFamilyFriendlyPreference = sharedPreferences.getBoolean(getString(R.string.pref_family_friendly_key),
                    getResources().getBoolean(R.bool.pref_family_friendly_value));
        } else if (key.equals(getString(R.string.pref_food_drink_key))) {
            mRetailPreference = sharedPreferences.getBoolean(getString(R.string.pref_retail_key),
                    getResources().getBoolean(R.bool.pref_retail_value));
        } else if (key.equals(getString(R.string.pref_food_drink_key))) {
            mPerformingArtsPreference = sharedPreferences.getBoolean(getString(R.string.pref_performing_arts_key),
                    getResources().getBoolean(R.bool.pref_performing_arts_value));
        } else if (key.equals(getString(R.string.pref_food_drink_key))) {
            mEntertainmentPreference = sharedPreferences.getBoolean(getString(R.string.pref_entertainment_key),
                    getResources().getBoolean(R.bool.pref_entertainment_value));
        }

        createPreferenceString();
        new ChangePreferences().execute(createUserPreferencesJSON());
    }

    /**
     * DownloadImage class runs background process to find user's facebook image
     */
    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> viewReference;

        public DownloadImage(ImageView bmImage) {
            viewReference = new WeakReference<ImageView>(bmImage);
        }

        // Find the image via Url in a background thread
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap image = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return image;
        }

        // Set the ImageView to have the Bitmap result
        protected void onPostExecute(Bitmap result) {
            ImageView imageView = viewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(result);
            }
        }
    }


    /**
     * createPreferenceJSONObject
     */
    private void createPreferenceString () {

        String preferences = "";

        if (mMusicPreference)
            preferences += getResources().getInteger(R.integer.pref_music_identifier);
        if (mFoodDrinkPreference)
            preferences += getResources().getInteger(R.integer.pref_food_drink_identifier);
        if (mSportsPreference)
            preferences += getResources().getInteger(R.integer.pref_sports_identifier);
        if (mOutdoorPreference)
            preferences += getResources().getInteger(R.integer.pref_outdoor_identifier);
        if (mHealthFitnessPreference)
            preferences += getResources().getInteger(R.integer.pref_health_fitness_identifier);
        if (mFamilyFriendlyPreference)
            preferences += getResources().getInteger(R.integer.pref_family_friendly_identifier);
        if (mRetailPreference)
            preferences += getResources().getInteger(R.integer.pref_retail_identifier);
        if (mPerformingArtsPreference)
            preferences += getResources().getInteger(R.integer.pref_performing_arts_identifier);
        if (mEntertainmentPreference)
            preferences += getResources().getInteger(R.integer.pref_entertainment_identifier);

        mPreferenceString = preferences;
    }

    private JSONObject createUserPreferencesJSON () {

        try {
            JSONObject preferenceJSON = new JSONObject();

            preferenceJSON.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
            preferenceJSON.put("pref_id", "2");

            return preferenceJSON;
        }
        catch(JSONException e) {
            Log.d("JSONException", e.toString());
            return null;
        }
    }

    public String getPreferenceString() {
        createPreferenceString();
        return mPreferenceString;
    }
}
