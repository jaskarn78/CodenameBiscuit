package com.example.codenamebiscuit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Profile mUserProfile;
    private String mUserEmail;

    private static final String DATABASE_CONNECTION_LINK =
            "http://athena.ecs.csus.edu/~teamone/php";
    private static final String DATABASE_CHECK_USER_LINK = "post_fb_id.php";
    private static final String DATABASE_CREATE_USER_LINK = "test.php";
    private static final String DATABASE_ID_FIELD_NAME = "user_id";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        mUserProfile = Profile.getCurrentProfile();

        if (AccessToken.getCurrentAccessToken() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            getUserFacebookEmail();
            checkIfProfileExists();
            //createLayout();
        }
    }

    /**********************************************************************************************
        Create Menu
     **********************************************************************************************/

    private void getUserFacebookEmail() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            mUserEmail = object.getString("email");
                            Log.v("Response: ", object.getString("email"));
                        } catch (JSONException e) {
                            Log.e("GraphRequest: ", e.toString());
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_page_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.events_list_menu_action) {
            Intent startUserSettingsActivity = new Intent(this, UserSettingsActivity.class);
            startActivity(startUserSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
/*
    private void createLayout() {
        View eventLayout = findViewById(R.id.event_view_linear_layout);
        LinearLayout event = findViewById(R.layout.event_layout);
    }
*/

    // Check if the user profile exists on the database, if not create one
    private void checkIfProfileExists() {
        // Grab the user id
        String userId = AccessToken.getCurrentAccessToken().getUserId();
        // Create a SigninActivity to sign the user in
        SigninActivity userSignin = new SigninActivity();
        userSignin.execute(DATABASE_CONNECTION_LINK, DATABASE_CHECK_USER_LINK,
                DATABASE_CREATE_USER_LINK, DATABASE_ID_FIELD_NAME, userId, mUserEmail);
    }
}
