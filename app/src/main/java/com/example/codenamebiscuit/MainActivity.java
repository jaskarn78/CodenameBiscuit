package com.example.codenamebiscuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.codenamebiscuit.helper.DatabaseHelper;
import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.helper.RecyclerItemClickListener;
import com.example.codenamebiscuit.login.ChooseLogin;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private EventAdapter mEventAdapter;
    private JSONObject currentUserId = new JSONObject();
    private SwipeRefreshLayout swipeContainer;
    private ImageView iv;
    private ArrayList<JSONObject> eventData;
    private SharedPreferences pref;
    private DatabaseHelper db;




    private static final String DATABASE_CONNECTION_LINK =
            "http://athena.ecs.csus.edu/~teamone/php/user_insert.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        setupRecyclerView();
        loadEventData();
        setupSwipeDownRefresh();


        if (AccessToken.getCurrentAccessToken() == null && pref.getString("user_idG", null)==null) {
            Intent intent = new Intent(MainActivity.this, ChooseLogin.class);
            startActivity(intent);
        }

        if(AccessToken.getCurrentAccessToken()!=null){
            try {
                currentUserId.put("user_id", AccessToken.getCurrentAccessToken().getUserId());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(pref.getString("user_idG", null)!=null && AccessToken.getCurrentAccessToken()==null){
            try{
                currentUserId.put("user_id", pref.getString("user_idG", null));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        onClick();
        loadEventData();
    }


    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        loadEventData();
    }
    @Override
    public void onStart(){
        super.onStart();
        loadEventData();
    }


    private void setupRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_events);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mEventAdapter = new EventAdapter(this);
        mRecyclerView.setAdapter(mEventAdapter);
    }



    private void onClick(){
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onItemClick(View v, int position) throws JSONException {
                Log.v("input", String.valueOf(position));
            }
            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }


    private void setupSwipeDownRefresh(){
        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        //iv=(ImageView)findViewById(R.id.full_screen_image);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEventData();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }


    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadEventData() {
        new QueryEventList(mEventAdapter, mRecyclerView).execute(currentUserId);
        eventData =new QueryEventList(mEventAdapter, mRecyclerView).getEventList();
        mRecyclerView.setVisibility(View.VISIBLE);

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

}
