package com.example.codenamebiscuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.example.codenamebiscuit.helper.App;
import com.example.codenamebiscuit.helper.DatabaseHelper;
import com.example.codenamebiscuit.helper.RoundedImageView;
import com.example.codenamebiscuit.login.ChooseLogin;
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


    private boolean mMusicPreference;
    private boolean mFoodDrinkPreference;
    private boolean mSportsPreference;
    private boolean mOutdoorPreference;
    private boolean mHealthFitnessPreference;
    private boolean mFamilyFriendlyPreference;
    private boolean mRetailPreference;
    private boolean mPerformingArtsPreference;
    private boolean mEntertainmentPreference;

    private JSONObject pref = new JSONObject();
    private SharedPreferences prefs;
    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_user_settings);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ActionBar actionBar = this.getSupportActionBar();
        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Profile profile = Profile.getCurrentProfile();
        if(profile==null) {
            //signIn();
            db = new DatabaseHelper(this);
            Cursor rs = db.getPerson("0");
            rs.moveToFirst();
            String img = rs.getString(rs.getColumnIndex(DatabaseHelper.PERSON_COLUMN_URL));
            Uri pic = Uri.parse(img);
            String fName = rs.getString(rs.getColumnIndex(DatabaseHelper.PERSON_COLUMN_FNAME));
            String lName = rs.getString(rs.getColumnIndex(DatabaseHelper.PERSON_COLUMN_LNAME));
            initializeGoogleProfileInfo(fName, lName, pic);

        }


        if (profile != null) {
            initializeFBProfileInfo(profile);
        }

        initializeLogoutButton();
        try {
            setupSharedPreferences();
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

        new DownloadImage((RoundedImageView) findViewById(R.id.pref_user_image)).execute(imageUrl);
        mNameView = (TextView) findViewById(R.id.pref_user_name);
        mNameView.setText(name + " " + surname);
    }

    private void initializeGoogleProfileInfo(String fName, String lName, Uri url){
        new DownloadImage((RoundedImageView) findViewById(R.id.pref_user_image)).execute(url.toString());
        mNameView = (TextView) findViewById(R.id.pref_user_name);
        mNameView.setText(fName + " " + lName);
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
                if(LoginManager.getInstance()!=null) {
                    LoginManager.getInstance().logOut();
                }else{
                    App.getGoogleApiHelper().disconnect();
                }
                Intent login = new Intent(UserSettingsActivity.this, ChooseLogin.class);
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
            Intent intent = new Intent(this, MainActivity.class);
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
        } else if (key.equals(getString(R.string.pref_food_drink_key))) {
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
                pref.put("user_id", prefs.getString("user_idG", null));
                pref.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
            }

            else if(prefs.getString("user_id", null)!=null) {
                pref.put("user_id", prefs.getString("user_idG", null));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ChangePreferences().execute(pref);
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
}

