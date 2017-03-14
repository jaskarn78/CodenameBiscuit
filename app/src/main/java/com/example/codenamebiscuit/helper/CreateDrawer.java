package com.example.codenamebiscuit.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codenamebiscuit.EmbeddedFragment;
import com.example.codenamebiscuit.R;
import com.example.codenamebiscuit.eventfragments.DeletedEventsFrag;
import com.example.codenamebiscuit.eventfragments.GridMainEventsFrag;
import com.example.codenamebiscuit.eventfragments.SavedEventsFrag;
import com.example.codenamebiscuit.eventfragments.SwipeEvents;
import com.example.codenamebiscuit.settings.UserSettingsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialize.Materialize;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by jaskarnjagpal on 3/3/17.
 */

public class CreateDrawer {
    private String fName, lName, pic, email;
    private Bundle savedState;
    private Toolbar toolbar;
    private Context context;
    private Activity activity;
    Drawer result=null;
    private Drawer resultAppended = null;
    AccountHeader headerResult=null;
    private FragmentManager fragmentManager;
    private MiniDrawer miniResult = null;
    private int livinPink, overlay;
    private int livinBlack, livinWhite;
    GPSTracker gps;
    LatLng latLng;

    public CreateDrawer(String fName, String lName, String pic, String email, Bundle savedState,
                        Toolbar toolbar, Context context, Activity activity, FragmentManager fragmentManager) {
        this.fName=fName; this.lName=lName;
        this.pic=pic; this.email=email;
        this.savedState=savedState; this.toolbar=toolbar;
        this.context=context;
        this.activity=activity;
        this.fragmentManager=fragmentManager;
        livinPink=context.getColor(R.color.livinPink);
        overlay=context.getColor(R.color.black_overlay);
        livinBlack=context.getColor(R.color.livinBlack);
        livinWhite=context.getColor(R.color.livinWhite);



    }

    public void loadDrawer() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder)
                        .fit().centerCrop().into(imageView);
            }
        });

        IProfile profile = new ProfileDrawerItem().withName(fName + " " + lName).withIcon(Uri.parse(pic)).withEmail(email).withIdentifier(100);
        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(profile)
                .withSavedInstance(savedState)
                .build();

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withSliderBackgroundColor(context.getColor(R.color.black_overlay))
                .withDelayDrawerClickEvent(40)
                //.withDelayOnDrawerClose(300)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName("Grid Events").withIcon(GoogleMaterial.Icon.gmd_home)
                                .withIdentifier(1)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectedColor(context.getColor(R.color.translivinPink)) .withSetSelected(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withName("Full Screen").withIcon(GoogleMaterial.Icon.gmd_fullscreen).withIdentifier(2)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectedColor(livinBlack) .withSetSelected(true),
                        new DividerDrawerItem(),

                        new PrimaryDrawerItem()
                                .withName("Saved Events").withIcon(GoogleMaterial.Icon.gmd_save).withIdentifier(3)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectedColor(livinBlack) .withSetSelected(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withName("Deleted Events").withIcon(GoogleMaterial.Icon.gmd_delete).withIdentifier(4)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectedColor(livinBlack) .withSetSelected(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withName("Preferences").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(5)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectable(false),

                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withName("Current coordinates").withIcon(GoogleMaterial.Icon.gmd_gps).withIdentifier(7)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Day/Night").withIcon(FontAwesome.Icon.faw_sun_o).withIdentifier(8)
                                .withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener)
                                .withTextColor(livinWhite)
                                .withIconColor(livinWhite)
                                .withSelectedTextColor(livinPink)
                                .withSelectedIconColor(livinPink)
                                .withSelectedColor(livinBlack) .withSetSelected(true)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D

                        Fragment fragment = null;
                        if (drawerItem != null) {
                            Intent intent = null;

                            if (drawerItem.getIdentifier()==1)
                                fragment = new GridMainEventsFrag().newInstance();
                            else if (drawerItem.getIdentifier()==2)
                                fragment = new SwipeEvents().newInstance();
                            else if (drawerItem.getIdentifier()==3)
                                fragment = new SavedEventsFrag().newInstance();
                            else if (drawerItem.getIdentifier()==4)
                                fragment = new DeletedEventsFrag().newInstance();
                            else if (drawerItem.getIdentifier()==5)
                                intent = new Intent(activity, UserSettingsActivity.class);
                            else if (drawerItem.getIdentifier()==6) {}
                                //fragment = new GridMainEventsFrag();
                            else if(drawerItem.getIdentifier()==7)
                                getLocation();
                            //result.closeDrawer();

                            if (intent != null) {
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent); }
                            if(fragment!=null){
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.setCustomAnimations(R.anim.enter, R.anim.exit);
                                ft.replace(R.id.fragment_container, fragment);

                                //if (fragmentManager.getBackStackEntryCount() > 0) {
                                 //  fragmentManager.popBackStackImmediate(); }
                                ft.addToBackStack(null);
                                ft.commit();
                                fragmentManager.executePendingTransactions();

                                return false;
                            } }
                        return false; }
                })
                .withSavedInstance(savedState)
                .withShowDrawerOnFirstLaunch(true)
                .build();


    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            View v = result.getSlider();
            List<IDrawerItem> primaryDrawerItem = result.getDrawerItems();
            TextView title = (TextView)activity.findViewById(R.id.toolbar_title);
            if (drawerItem.getIdentifier()==8) {
                if(isChecked) {
                    Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
                    toolbar.setBackgroundColor(context.getColor(R.color.livinBlack));
                    v.setBackgroundColor(context.getColor(R.color.livinBlack));
                    title.setTextColor(livinPink);

                }else {
                    toolbar.setBackgroundColor(context.getColor(R.color.livinPink));
                    v.setBackgroundColor(context.getColor(R.color.material_drawer_background));
                    title.setTextColor(context.getColor(R.color.livinBlack)); } } } };

    /**********************************************************************************************
     * Obtains the users current location via GPS location services
     * Location is converted into Lat and Lng coordinates
     * Coordinates are then passed into a Geocoder to retrieve the current address
     **********************************************************************************************/

    private void getLocation(){
        double latitude=0; double longitude=0;
        gps = new GPSTracker(context.getApplicationContext());
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            latLng = new LatLng(latitude, longitude);
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(activity.getApplicationContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                StyleableToast st = new StyleableToast(context.getApplicationContext(),
                        addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getLocality() + ", "
                                + addresses.get(0).getAdminArea() + " " + addresses.get(0).getPostalCode(), Toast.LENGTH_SHORT);
                st.spinIcon();
                st.setBackgroundColor(Color.BLUE);
                st.setTextColor(Color.WHITE);
                st.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public Drawer getResult(){
        return result;
    }
    public AccountHeader getHeader(){
        return headerResult;
    }

}
