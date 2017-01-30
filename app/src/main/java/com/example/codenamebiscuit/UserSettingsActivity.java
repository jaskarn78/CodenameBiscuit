package com.example.codenamebiscuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.codenamebiscuit.helper.RoundedImageView;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.io.InputStream;
import java.lang.ref.WeakReference;

public class UserSettingsActivity
        extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView mNameView; // User's name

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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

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
