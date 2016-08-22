package sa.iff.minatentlocator.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;

import sa.iff.minatentlocator.GetPathPoints;
import sa.iff.minatentlocator.Locations;
import sa.iff.minatentlocator.ProtoBufUtil.GetFilesWeb;
import sa.iff.minatentlocator.R;
import sa.iff.minatentlocator.RotaTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Context context;
    private TextView distance, estTime;
    private Locations locations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locations = new Locations(this);
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
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) {
            mMap = googleMap;
            distance = (TextView) findViewById(R.id.distance);
            estTime = (TextView) findViewById(R.id.esttime);
            context = this;
            final String editFrom = getIntent().getStringExtra("FROM");
            final String editTo = getIntent().getStringExtra("TO");
            final String place = getIntent().getStringExtra("PLACE");
            /*getExternalFilesDir(null);
            try {
                ArrayList<String> metaFiles = new ArrayList<>(Arrays.asList(getExternalFilesDir(null).list()));
                if (!metaFiles.contains("vertexes_"+place+".ser"))
                    new GetFilesWeb(this, mMap, editFrom, editTo, distance, estTime, place).execute(locations.returnUrls(place)[0], locations.returnUrls(place)[1]);
                else {
                    if (editFrom.equals("myloc")) {
                        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                            @Override
                            public void onMyLocationChange(Location location) {
                                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                                if (loc.latitude >= locations.returnBounds(place)[0].latitude && loc.latitude <= locations.returnBounds(place)[1].latitude
                                        && loc.longitude >= locations.returnBounds(place)[0].longitude && loc.longitude <= locations.returnBounds(place)[1].longitude) {
                                    String locString = loc.latitude + "," + loc.longitude;
                                    new RotaTask(context, mMap, locString, editTo, place).execute(distance, estTime);
                                } else {
                                    Toast.makeText(context, "You are not in " + place + ". Directions cannot be calculated", Toast.LENGTH_LONG).show();
                                    Intent intentMain = new Intent(MapsActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(intentMain);
                                }
                            }
                        });
                    } else new RotaTask(this, mMap, editFrom, editTo, place).execute(distance, estTime);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }*/
            new GetPathPoints(this, mMap, editFrom, editTo, place).execute();
            setUpMapIfNeeded();
        }
    }
}
