package com.example.codenamebiscuit.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.example.codenamebiscuit.MainActivity;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.SwipeEvents;
import com.example.codenamebiscuit.UserSettingsActivity;
import com.example.codenamebiscuit.ViewDeletedEvents;
import com.example.codenamebiscuit.ViewSavedEvents;
import com.example.codenamebiscuit.rv.EventAdapter;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jaskarnjagpal on 2/22/17.
 */

public class SetupDrawer {
    private AccountHeader headerResult;
    private Drawer result;
    private String fName, lName, email, pic;
    private Toolbar toolbar;
    private JSONObject currentUserId = new JSONObject();
    private EventAdapter mEventAdapter;
    private String userId;
    private Context context;
    private Activity activity;

    public SetupDrawer(AccountHeader headerResult, Drawer result, Toolbar toolbar,
                       JSONObject currentUserId, EventAdapter mEventAdapter, String userId, Context context,
                       String fName, String lName, String email, String pic) {
        this.headerResult = headerResult;
        this.result=result;
        this.toolbar = toolbar;
        this.currentUserId = currentUserId;
        this.mEventAdapter = mEventAdapter;
        this.userId = userId;
        this.context = context;
        this.fName=fName;
        this.lName=lName;
        this.email=email;
        this.pic=pic;

    }

    public void setupNavDrawer(Bundle savedState, final Activity activity) {


        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext())
                        .load(uri)
                        .placeholder(placeholder)
                        .fit()
                        .centerCrop()
                        .into(imageView);

            }
        });

        IProfile profile = new ProfileDrawerItem().withName(fName + " " + lName).withIcon(Uri.parse(pic)).withEmail(email).withIdentifier(100);

        //new DrawerBuilder().withActivity(this).build();
        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(profile)
                .withActivity(activity)
                .withSavedInstance(savedState)
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Home")
                .withIcon(R.drawable.ic_home_black_24dp).withIdentifier(100000);

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName("Home").withIcon(R.drawable.ic_home_black_24dp).withIdentifier(1),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Full Screen").withIcon(R.drawable.ic_fullscreen_black_24dp).withIdentifier(2),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Saved Events").withIcon(R.drawable.ic_save_black_24dp).withIdentifier(3),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Deleted Events").withIcon(R.drawable.ic_delete_black_24dp).withIdentifier(4),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Preferences").withIcon(R.drawable.ic_settings_black_24dp).withIdentifier(5)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(context.getApplicationContext(), MainActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {

                                intent = new Intent(context.getApplicationContext(), SwipeEvents.class);
                                intent.putExtra("user_id", currentUserId + "");
                                ArrayList<String> data = new ArrayList<String>();
                                for (int i = 0; i < mEventAdapter.getObject().size(); i++) {
                                    data.add(mEventAdapter.getObject().get(i).toString());

                                }
                                JSONArray jsonArray = new JSONArray(mEventAdapter.getObject());
                                intent.putStringArrayListExtra("data", data);
                                intent.putExtra("jArray", jsonArray.toString());

                            } else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(context.getApplicationContext(), ViewSavedEvents.class);
                                intent.putExtra("user_id", userId);
                            } else if (drawerItem.getIdentifier() == 4) {
                                intent = new Intent(context.getApplicationContext(), ViewDeletedEvents.class);
                                intent.putExtra("user_id", userId);

                            } else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(context.getApplicationContext(), UserSettingsActivity.class);
                            }

                            if (intent != null) {
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedState)
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };
}
