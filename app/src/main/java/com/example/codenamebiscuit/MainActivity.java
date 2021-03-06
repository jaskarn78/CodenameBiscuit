package com.example.codenamebiscuit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

public class MainActivity extends AppCompatActivity {
    private static final String DATABASE_CONNECTION_LINK =
            "http://athena.ecs.csus.edu/~teamone/php/query.php";
    private static final String DATABASE_ID_FIELD_NAME = "user_id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        if (AccessToken.getCurrentAccessToken() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            checkIfProfileExists();
        }
    }

    /**********************************************************************************************
        Create Menu
     **********************************************************************************************/

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


    // Check if the user profile exists on the database, if not create one
    private void checkIfProfileExists() {
        String userId = AccessToken.getCurrentAccessToken().getUserId();
        new SigninActivity(1).execute(DATABASE_CONNECTION_LINK, DATABASE_ID_FIELD_NAME, userId);
    }
}
