package sa.iff.minatentlocator.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

import sa.iff.minatentlocator.LocationPermission;
import sa.iff.minatentlocator.Locations;
import sa.iff.minatentlocator.ProtoBufUtil.GetFilesWeb;
import sa.iff.minatentlocator.R;
import sa.iff.minatentlocator.RotaTask;
import sa.iff.minatentlocator.SharedPrefArrayUtils;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Context context;
    private TextView distance, estTime, from;
    private Spinner toList;
    private Locations locations;
    private String place, editFrom, editTo, editFromLabel, editToLabel;
    public static boolean favourite;
    private RotaTask rotaTask = null;
    private LocationPermission locationPermission;
    private SharedPreferences sharedPreferences;
    private SharedPrefArrayUtils sharedPrefArrayUtils;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbarSetFavourite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locations = new Locations(this);
        locationPermission = new LocationPermission(context, MapsActivity.this);

        place = getIntent().getStringExtra("PLACE");
        editFromLabel = getIntent().getStringExtra("FROM_LABEL");
        editToLabel = getIntent().getStringExtra("TO_LABEL");
        favourite = getIntent().getBooleanExtra("FAV", false);

        sharedPreferences = getSharedPreferences("Favourite_Management", Context.MODE_PRIVATE);
        sharedPrefArrayUtils = new SharedPrefArrayUtils(sharedPreferences, place);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(place + " Navigation");

        from = (TextView) findViewById(R.id.map_from);
        from.setText(editFromLabel);
        toList = (Spinner) findViewById(R.id.map_list_category_to);
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
                    if (rotaTask != null && !initialDest) {
                        editToLabel = destList.get(position);
                        rotaTask.executeDestination(distance, estTime, returnLocations.get(destList.get(position))[0], editToLabel, null);
                    }
                    else if(!initialDest)
                        Toast.makeText(context, "Sorry, cannot show the directions now, please go back and try again.", Toast.LENGTH_LONG).show();
                    initialDest = false;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {  }
            });
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbar_layout);
            snackbarSetFavourite = Snackbar.make(coordinatorLayout, "Do you want to save the map based on Origin to your Favourites?", Snackbar.LENGTH_INDEFINITE);
            snackbarSetFavourite.setAction("SAVE", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (rotaTask != null) {
                                                    ArrayList<String> favPlaces = sharedPrefArrayUtils.loadArray();
                                                    favPlaces.add(editFromLabel);
                                                    sharedPrefArrayUtils.saveArray(favPlaces);
                                                    rotaTask.saveGraphSource(sharedPreferences);
                                                }
                                            }
                                        })
                                .setActionTextColor(Color.YELLOW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            supportMapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) {
            mMap = googleMap;
            context = this;
            setUpMap();
            distance = (TextView) findViewById(R.id.distance);
            estTime = (TextView) findViewById(R.id.esttime);
            editFrom = getIntent().getStringExtra("FROM");
            editTo = getIntent().getStringExtra("TO");
            getExternalFilesDir(null);
            ArrayList<String> metaFiles = new ArrayList<>(Arrays.asList(getExternalFilesDir(null).list()));
            if (!metaFiles.contains("vertexes_" + place + ".ser"))
                new GetFilesWeb(this, mMap, editFrom, editTo, distance, estTime, place, editFromLabel, editToLabel, snackbarSetFavourite).execute(locations.returnUrls(place)[0], locations.returnUrls(place)[1]);
            else {
                if (editFrom.equals("myloc")) {
                    locationCheck(getMyLocation());
                    mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {
                            locationCheck(location);
                        }
                    });
                } else {
                    rotaTask = new RotaTask(this, mMap, editFrom, editTo, place, editFromLabel, editToLabel, snackbarSetFavourite);
                    if (!favourite) rotaTask.execute(distance, estTime);
                    else rotaTask.executeDestination(distance, estTime, editTo, editToLabel, sharedPreferences);
                }
            }
            //new GetPathPoints(this, mMap, editFrom, editTo, place).execute();
        }
    }

    public void locationCheck(Location location) {
        if (location != null) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            if (loc.latitude >= locations.returnBounds(place)[0].latitude && loc.latitude <= locations.returnBounds(place)[1].latitude
                    && loc.longitude >= locations.returnBounds(place)[0].longitude && loc.longitude <= locations.returnBounds(place)[1].longitude) {
                rotaTask = new RotaTask(this, mMap, editFrom, editTo, place, editFromLabel, editToLabel, snackbarSetFavourite);
                rotaTask.execute(distance, estTime);
            } else {
                Toast.makeText(context, "You are not in " + place + ". Directions cannot be calculated", Toast.LENGTH_LONG).show();
                Intent intentMain = new Intent(MapsActivity.this, MainActivity.class);
                finish();
                startActivity(intentMain);
            }
        }
        else {
            Toast.makeText(context, "Cannot detect your location because Access to your Location is Denied", Toast.LENGTH_LONG).show();
            Intent intentMain = new Intent(MapsActivity.this, MainActivity.class);
            finish();
            startActivity(intentMain);
        }
    }

    public Location getMyLocation() {
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        final Location[] location = new Location[1];
        if (locationPermission.checkLocationPermission())
            location[0] = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        else {
            final Handler handlerLocation = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.arg1 == 1) {
                        if (locationPermission.checkLocationPermission())
                            location[0] = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }
                    else location[0] = null;
                    return false;
                }
            });
            locationPermission.getLocationPermission(handlerLocation);
        }
        return location[0];
    }


}
