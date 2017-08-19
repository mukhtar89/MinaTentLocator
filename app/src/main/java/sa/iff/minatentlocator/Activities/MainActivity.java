package sa.iff.minatentlocator.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import sa.iff.minatentlocator.AboutInfo;
import sa.iff.minatentlocator.Fragments.NavPageAdapter;
import sa.iff.minatentlocator.Utils.FusedLocationService;
import sa.iff.minatentlocator.Utils.PermissionManager;
import sa.iff.minatentlocator.R;
import sa.iff.minatentlocator.Dialogs.SwitchNetworkDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int REQUEST_LOCATION = 6534;
    private MaterialViewPager materialViewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";

    private final Handler drawerActionHandler = new Handler();
    private DrawerLayout drawerLayout;
    private ImageView materialLogo;

    private int navItemId;
    private AboutInfo aboutInfo;
    protected PermissionManager locationPermission;
    private SwitchNetworkDialog switchNetworkDialog;
    private GoogleApiClient mGoogleApiClient;
    protected FusedLocationService fusedLocationService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        aboutInfo = new AboutInfo(this);
        // load saved navigation state if present
        if (null == savedInstanceState) {
            navItemId = Integer.parseInt(sharedPreferences.getString("tab_default", "0"));
        } else {
            navItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navDrawer);
        navigationView.setNavigationItemSelectedListener(this);

        switchNetworkDialog = new SwitchNetworkDialog();
        ArrayList<String> metaFiles = new ArrayList<>(Arrays.asList(context.getExternalFilesDir(null).list()));
        if (metaFiles.size() != 6 && !switchNetworkDialog.isOnline(context)) {
                switchNetworkDialog.show(getSupportFragmentManager(), "SwitchNetworkDialog");
        }

        locationPermission = new PermissionManager(this);
        fusedLocationService = new FusedLocationService(this, locationPermission, null);
        mGoogleApiClient = fusedLocationService.buildGoogleApiClient();
        if (!locationPermission.checkLocationPermission())
            locationPermission.getLocationPermission();

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        LocationSettingsRequest.Builder locationSettingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsBuilder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                final LocationSettingsStates settingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d("onResult", "SUCCESS");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d("onResult", "RESOLUTION_REQUIRED");
                        try
                        {status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);}
                        catch (IntentSender.SendIntentException e) {}
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d("onResult", "UNAVAILABLE");
                        break;
                }
            }
        });

        materialViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        /*PagerSlidingTabStrip tabs = materialViewPager.getPagerTitleStrip();
        tabs.setViewPager(materialViewPager.getViewPager());*/
        materialViewPager.getViewPager().setAdapter(new NavPageAdapter(getSupportFragmentManager(), MainActivity.this, switchNetworkDialog));

        materialViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                materialLogo.setColorFilter(getResources().getColor(android.R.color.white));
                switch (page) {
                    case 0:
                        materialLogo.setImageResource(R.drawable.night_camping);
                        return HeaderDesign.fromColorResAndDrawable(R.color.theme_color,
                                getResources().getDrawable(R.drawable.mina_back));
                    case 1:
                        materialLogo.setImageResource(R.drawable.kaaba_mecca);
                        return HeaderDesign.fromColorResAndDrawable(R.color.theme_color,
                                getResources().getDrawable(R.drawable.makkah_back));
                    case 2:
                        materialLogo.setImageResource(R.drawable.embassy);
                        return HeaderDesign.fromColorResAndDrawable(R.color.theme_color,
                                getResources().getDrawable(R.drawable.aziziyah_back));
                }
                return null;
            }
        });

        materialViewPager.getPagerTitleStrip().setTextColor(Color.WHITE);
        materialViewPager.getViewPager().setOffscreenPageLimit(materialViewPager.getViewPager().getAdapter().getCount());
        materialViewPager.getPagerTitleStrip().setViewPager(materialViewPager.getViewPager());


        materialLogo = (ImageView) findViewById(R.id.logo_white);
        /*if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialViewPager.notifyHeaderChanged();
                    Toast.makeText(getApplicationContext(), "Yes, the title is clickable", Toast.LENGTH_SHORT).show();
                }
            });
        }*/

        /*tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        navPageAdapter = new NavPageAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(navPageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        tabLayout.setupWithViewPager(viewPager);*/
    }

    @Override
    public void onResume() {
        super.onResume();
        NavPageAdapter navPageAdapter = new NavPageAdapter(getSupportFragmentManager(), MainActivity.this, switchNetworkDialog);
        materialViewPager.getViewPager().setAdapter(navPageAdapter);
        materialViewPager.getPagerTitleStrip().setTextColor(Color.WHITE);
        materialViewPager.getViewPager().setOffscreenPageLimit(materialViewPager.getViewPager().getAdapter().getCount());
        materialViewPager.getPagerTitleStrip().setViewPager(materialViewPager.getViewPager());
        materialViewPager.getViewPager().setCurrentItem(navItemId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.about) {
            try {
                aboutInfo.showDialog();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (id == R.id.settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }


    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, navItemId);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(true);
        navItemId = item.getItemId();

        drawerLayout.closeDrawer(GravityCompat.START);
        drawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (navItemId) {
                    case R.id.nav_mina: materialViewPager.getViewPager().setCurrentItem(0);
                        break;
                    case R.id.nav_makkah: materialViewPager.getViewPager().setCurrentItem(1);
                        break;
                    case R.id.nav_aziziyah: materialViewPager.getViewPager().setCurrentItem(2);
                        break;
                    case R.id.nav_manage: Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settingsIntent);
                        break;
                    case R.id.nav_send: Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "muhammad.28.1989@gmail.com", null));
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Hajj Navigator App");
                        startActivity(Intent.createChooser(sendIntent, "Send email..."));
                        break;
                    case R.id.nav_share: String url = "https://play.google.com/store/apps/details?id=sa.iff.minatentlocator";
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "This app is a must if you are going for Hajj: " + url);
                        startActivity(shareIntent);
                        break;
                    case R.id.nav_about: try {
                                    aboutInfo.showDialog();
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                        break;
                    case R.id.nav_favourites: Intent favIntent = new Intent(MainActivity.this, FavouriteActivity.class);
                        startActivity(favIntent);
                        break;
                    default: break;

                }
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location Access Granted!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Location Access Denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Toast.makeText(this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

}
