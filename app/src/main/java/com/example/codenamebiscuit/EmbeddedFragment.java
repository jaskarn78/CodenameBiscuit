package com.example.codenamebiscuit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ContainerDrawerItem;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * Created by jaskarnjagpal on 3/10/17.
 */

public class EmbeddedFragment {
    private static final int PROFILE_SETTING = 1;
    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private SharedPreferences pref;
    private int livinPink, livinBlack, livinWhite;
    private ListView lv;
    private SlidingUpPanelLayout mLayout;
    private Context context;
    private Activity activity;
    private Bundle savedInstanceState;

    public EmbeddedFragment(Context context, Activity activity, Bundle savedInstanceState){
        this.context=context;
        this.activity=activity;
        this.savedInstanceState=savedInstanceState;

    }

    public void setupSideDrawer() {
        pref= PreferenceManager.getDefaultSharedPreferences(context);
        final String pic = pref.getString("user_image", null);
        final String fName = pref.getString("fName", null);
        final String lName = pref.getString("lName", null);
        final String email = pref.getString("email", null);
        final String user = pref.getString("user_id", null);
        livinBlack=context.getColor(R.color.livinBlack);
        livinPink=context.getColor(R.color.livinPink);
        livinWhite=context.getColor(R.color.livinWhite);


        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri)
                        .fit().centerCrop().into(imageView);
            }
        });
        IProfile profile = new ProfileDrawerItem().withName(fName + " " + lName).withIcon(Uri.parse(pic)).withEmail(email).withIdentifier(100);
        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .addProfiles(profile)
                .withCompactStyle(true)
                .withTranslucentStatusBar(true)
                .withSavedInstance(savedInstanceState)
                .build();

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder(activity)
                .withRootView(R.id.slidingLayer1)
                .withSliderBackgroundColor(activity.getColor(R.color.black_overlay))
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new SwitchDrawerItem()
                                .withName("Music").withIcon(GoogleMaterial.Icon.gmd_collection_music)
                                .withIdentifier(1).withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem()
                                .withName("Food/Drink").withIcon(FontAwesome.Icon.faw_beer).withIdentifier(2)
                                .withTextColor(activity.getColor(R.color.livinWhite))
                                .withIconColor(activity.getColor(R.color.livinWhite))
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Sports").withIcon(FontAwesome.Icon.faw_delicious).withIdentifier(3)
                                .withTextColor(activity.getColor(R.color.livinWhite))
                                .withIconColor(activity.getColor(R.color.livinWhite))
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Outdoors").withIcon(FontAwesome.Icon.faw_sun_o).withIdentifier(4)
                                .withTextColor(activity.getColor(R.color.livinWhite))
                                .withIconColor(activity.getColor(R.color.livinWhite))
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Health/Fitness").withIcon(FontAwesome.Icon.faw_bicycle).withIdentifier(5)
                                .withTextColor(activity.getColor(R.color.livinWhite))
                                .withIconColor(activity.getColor(R.color.livinWhite))
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Family Friendly").withIcon(FontAwesome.Icon.faw_child).withIdentifier(6)
                                .withTextColor(activity.getColor(R.color.livinWhite))
                                .withIconColor(activity.getColor(R.color.livinWhite))
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Retail").withIcon(FontAwesome.Icon.faw_shopping_cart).withIdentifier(7)
                                .withTextColor(activity.getColor(R.color.livinWhite))
                                .withIconColor(activity.getColor(R.color.livinWhite))
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Performing Arts").withIcon(FontAwesome.Icon.faw_snapchat_ghost).withIdentifier(8)
                                .withTextColor(activity.getColor(R.color.livinWhite))
                                .withIconColor(activity.getColor(R.color.livinWhite))
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Entertainment").withIcon(FontAwesome.Icon.faw_youtube_play).withIdentifier(9)
                            .withTextColor(activity.getColor(R.color.livinWhite))
                            .withIconColor(activity.getColor(R.color.livinWhite))
                            .withSelectable(false)

                )
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .buildView();

    }

}
