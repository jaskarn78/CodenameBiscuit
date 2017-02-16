package com.example.codenamebiscuit;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.helper.QueryEventList;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewEvent extends AppCompatActivity{
    private final String IMAGE_URL_PATH = "http://athena.ecs.csus.edu/~teamone/AndroidUploadImage/uploads/";
    private ImageView eventImage;
    private EventAdapter mEventAdapter;
    private ArrayList<JSONObject> eventData = new ArrayList<>();
    private TextView event_name_tv;
    private TextView event_location_tv;
    private TextView event_preference_tv;
    private  String event_location, img_path,
            event_preference, event_name;
    private ArrayList<JSONObject> jlist;
    private SharedPreferences pref;
    // Container Activity must implement this interface
    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;
    int position, updatedPosition;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.event_details);

        eventImage = (ImageView)findViewById(R.id.iv_event_details);
        Bundle extras = getIntent().getExtras();

        if(extras!=null) {
            String array = extras.getString("jArray");
            try {
                JSONArray jsonAr = new JSONArray(array);

                //Log.v("jarray", list.get(0).toString());
                for(int i=0; i<jsonAr.length(); i++) {
                    JSONObject jsonObj = jsonAr.getJSONObject(i);
                    eventData.add(jsonObj);

                }
                position = extras.getInt("position", 0);
                img_path = eventData.get(position).getString("img_path");
                event_name = eventData.get(position).getString("event_name");
                event_location = eventData.get(position).getString("event_location");
                event_preference = eventData.get(position).getString("preference_name");
                img_path = eventData.get(position).getString("img_path");
                //loadImage(getImageURL(img_path));
                Picasso.with(this).load(getImageURL(img_path))
                        .fit()
                        .centerCrop()
                        .into(eventImage);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        eventImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog alert = new Dialog(v.getContext());
                alert.setContentView(R.layout.dialog_layout);

                event_location_tv = (TextView)alert.findViewById(R.id.location);
                event_location_tv.setText("Location: "+event_location);
                event_name_tv = (TextView)alert.findViewById(R.id.eventName);
                event_name_tv.setText("Event: ");
                event_preference_tv = (TextView)alert.findViewById(R.id.preferences);


                alert.setTitle("Event Details");
                alert.show();
                alert.getWindow().setTitle("Event Details");
                alert.getWindow().setBackgroundDrawableResource(R.drawable.shape);
                alert.getWindow().setLayout(1200, 400);
                return true;
            }
        });


        if(savedInstanceState !=null){
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

    }

    @Override
    public void onStart(){
      super.onStart();

    }
    public String getImageURL(String path){
        return IMAGE_URL_PATH+path;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.events_list_menu_action) {
            //Intent startUserSettingsActivity = new Intent(this, UserSettingsActivity.class);
            //startActivity(startUserSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
