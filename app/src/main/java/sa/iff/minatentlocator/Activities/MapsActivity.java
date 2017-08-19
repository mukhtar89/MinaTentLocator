package sa.iff.minatentlocator.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;

import sa.iff.minatentlocator.Utils.FusedLocationService;
import sa.iff.minatentlocator.MapUtils.Locations;
import sa.iff.minatentlocator.Utils.PermissionManager;
import sa.iff.minatentlocator.R;
import sa.iff.minatentlocator.MapUtils.RouteCalculateEngine;
import sa.iff.minatentlocator.SharedPrefArrayUtils;
import sa.iff.minatentlocator.Dialogs.SwitchNetworkDialog;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private Context context;
    private TextView distance, estTime, from, progressText;
    private Location currentLocation;
    private Locations locations;
    private String place, editFrom, editTo, editFromLabel, editToLabel;
    public static boolean favourite;
    protected PermissionManager locationPermission;
    private GoogleApiClient mGoogleApiClient;
    protected FusedLocationService fusedLocationService;
    private SharedPreferences sharedPreferences;
    private SharedPrefArrayUtils sharedPrefArrayUtils;
    private LinearLayout mainLayout, navigateButton;
    private RelativeLayout progressLayout;
    private Snackbar snackBarSetFavourite;
    private Boolean navigationStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = this;
        locations = new Locations(this);

        mapView = (MapView) findViewById(R.id.navigator_map);
        mapView.onCreate(savedInstanceState);
        if (mapView != null)
            mapView.getMapAsync(this);

        LinearLayout networkStatusContainer = (LinearLayout) findViewById(R.id.network_mode_container);
        TextView networkStatusModeText = (TextView) findViewById(R.id.network_mode_status_text);
        ImageView networkStatusModeIcon = (ImageView) findViewById(R.id.network_mode_status_icon);
        SwitchNetworkDialog switchNetworkDialog = new SwitchNetworkDialog();
        if (switchNetworkDialog.isOnline(this)) {
            networkStatusContainer.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            networkStatusModeText.setText("Online mode");
            networkStatusModeIcon.setImageResource(R.drawable.ic_phonelink_ring_white_48dp);
            /*if (!sharedPreferences.getBoolean("showOfflineTip", false)) {
                AlertDialog.Builder offlineTipDialog = new AlertDialog.Builder(this);
                offlineTipDialog.setIcon(R.drawable.tent_logo)
                        .setCancelable(true)
                        .setTitle("Performance Tip")
                        .setMessage("\nPlease use offline mode, if you are ")
                        .setMultiChoiceItems(R.array.do_not_show_tip_again, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                            }
                        }).setPositiveButton("WI-FI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                wifiManager.setWifiEnabled(true);
                            }
                        }).setNegativeButton("MOBILE DATA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(
                                "com.android.settings",
                                "com.android.settings.Settings$DataUsageSummaryActivity"));
                        startActivity(intent);
                    }
                }).setNeutralButton("EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
                return offlineTipDialog.create();
            }*/
        } else {
            networkStatusContainer.setBackgroundColor(getResources().getColor(R.color.black_overlay));
            networkStatusModeText.setText("Offline mode");
            networkStatusModeIcon.setImageResource(R.drawable.ic_phonelink_erase_white_48dp);
        }

        place = getIntent().getStringExtra("PLACE");
        editFromLabel = getIntent().getStringExtra("FROM_LABEL");
        editToLabel = getIntent().getStringExtra("TO_LABEL");
        favourite = getIntent().getBooleanExtra("FAV", false);
        editFrom = getIntent().getStringExtra("FROM");
        editTo = getIntent().getStringExtra("TO");

        locationPermission = new PermissionManager(this);
        fusedLocationService = new FusedLocationService(this, locationPermission, locationHandler);
        mGoogleApiClient = fusedLocationService.buildGoogleApiClient();

        sharedPreferences = getSharedPreferences("Favourite_Management", Context.MODE_PRIVATE);
        sharedPrefArrayUtils = new SharedPrefArrayUtils(sharedPreferences, place);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(place + " Navigation");

        mainLayout = (LinearLayout) findViewById(R.id.navigation_maps_main);
        progressLayout = (RelativeLayout) findViewById(R.id.navigation_maps_progress);
        progressText = (TextView) findViewById(R.id.navigation_progress_text);
        navigateButton = (LinearLayout) findViewById(R.id.navigation_maps_button);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleMap != null) googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        if (currentLocation != null) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 19));
                            navigationStarted = true;
                            navigateButton.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        from = (TextView) findViewById(R.id.map_from);
        from.setText(editFromLabel);
        Spinner toList = (Spinner) findViewById(R.id.map_list_category_to);
        final Hashtable<String, String[]> returnLocations;
        final Object[] locationList;
        final ArrayList<String> destList;
        try {
            returnLocations = locations.parseLocations(place);
            locationList = returnLocations.keySet().toArray();
            destList = new ArrayList<>(Arrays.asList(Arrays.copyOf(locationList, locationList.length, String[].class)));
            destList.remove(editFromLabel);
            Collections.sort(destList);
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_map, destList);
            toList.setAdapter(categoryAdapter);
            toList.setSelection(destList.indexOf(editToLabel));
            toList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean initialDest = true;
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!initialDest) {
                        editToLabel = destList.get(position);
                        RouteCalculateEngine.executeDestination(distance, estTime, returnLocations.get(destList.get(position))[0], editToLabel, progressText, progressLayout, mainLayout);
                    }
                    initialDest = false;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {  }
            });
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbar_layout);
            snackBarSetFavourite = Snackbar.make(coordinatorLayout, "Do you want to save the map based on Origin to your Favourites?", Snackbar.LENGTH_INDEFINITE);
            snackBarSetFavourite.setAction("SAVE", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    ArrayList<String> favPlaces = sharedPrefArrayUtils.loadArray();
                                                    favPlaces.add(editFromLabel);
                                                    sharedPrefArrayUtils.saveArray(favPlaces);
                                                    RouteCalculateEngine.saveGraphSource(MapsActivity.this);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                .setActionTextColor(Color.YELLOW);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!sharedPreferences.getString("currentLocation", "").isEmpty() && currentLocation == null)
            currentLocation = new Gson().fromJson(sharedPreferences.getString("currentLocation",""), Location.class);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (locationPermission.checkLocationPermission()) googleMap.setMyLocationEnabled(true);
        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (i == 1) {
                    navigationStarted = false;
                    navigateButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        MapsInitializer.initialize(this);
        setUpMap();
        distance = (TextView) findViewById(R.id.distance);
        estTime = (TextView) findViewById(R.id.esttime);
        if (editFrom.equals("myloc")) locationCheck();
        else {
            RouteCalculateEngine.setParams(this, this.googleMap, editFrom, editTo, place, editFromLabel, editToLabel, snackBarSetFavourite, MapsActivity.this);
            if (!favourite) RouteCalculateEngine.executeFull(distance, estTime, progressText, progressLayout, mainLayout);
            else RouteCalculateEngine.executeDestination(distance, estTime, editTo, editToLabel, progressText, progressLayout, mainLayout);
        }
        //new GetPathPoints(this, googleMap, editFrom, editTo, place).executeFull();
    }

    public void locationCheck() {
        if (currentLocation != null) {
            LatLng loc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            if (loc.latitude >= locations.returnBounds(place)[0].latitude && loc.latitude <= locations.returnBounds(place)[1].latitude
                    && loc.longitude >= locations.returnBounds(place)[0].longitude && loc.longitude <= locations.returnBounds(place)[1].longitude) {
                RouteCalculateEngine.setParams(this, googleMap, loc.latitude+","+loc.longitude, editTo, place, "My Location", editToLabel, snackBarSetFavourite, MapsActivity.this);
                RouteCalculateEngine.executeFull(distance, estTime, progressText, progressLayout, mainLayout);
            } else {
                Toast.makeText(context, "You are not in " + place + ". Directions cannot be calculated", Toast.LENGTH_LONG).show();
                Intent intentMain = new Intent(MapsActivity.this, MainActivity.class);
                finish();
                startActivity(intentMain);
            }
        } else {
            Toast.makeText(context, "Cannot detect your location because Access to your Location is Denied", Toast.LENGTH_LONG).show();
            Intent intentMain = new Intent(MapsActivity.this, MainActivity.class);
            finish();
            startActivity(intentMain);
        }
    }

    private Handler locationHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            locationUpdate();
            return true;
        }
    });

    protected void locationUpdate() {
        if (fusedLocationService.returnLocation() != null) {
            currentLocation = fusedLocationService.returnLocation();
            sharedPreferences.edit().putString("currentLocation", new Gson().toJson(currentLocation)).apply();
            if (navigationStarted) {
                navigateButton.setVisibility(View.GONE);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 19));
            }
        }
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
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        RouteCalculateEngine.graph = null;
        RouteCalculateEngine.locations = null;
        RouteCalculateEngine.favouritePredecessors = null;
        RouteCalculateEngine.locationPermission = null;
        RouteCalculateEngine.activity = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
