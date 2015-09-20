package sa.iff.minatentlocator.Activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import sa.iff.minatentlocator.ProtoBufUtil.GetFilesWeb;
import sa.iff.minatentlocator.R;
import sa.iff.minatentlocator.RotaTask;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Context context;
    private TextView distance, estTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_maps);
        mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        distance = (TextView) findViewById(R.id.distance);
        estTime = (TextView) findViewById(R.id.esttime);
        mMap.setMyLocationEnabled(true);
        context = this;
        final String editFrom = getIntent().getStringExtra("FROM");
        final String editTo = getIntent().getStringExtra("TO");
        getExternalFilesDir(null);
        if (getExternalFilesDir(null).list().length < 2)
            new GetFilesWeb(this, mMap, editFrom, editTo, distance, estTime).execute("https://goo.gl/TWSPTO", "https://goo.gl/nlGB0u");
        else {
            if (editFrom.equals("myloc")) {
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                        if (loc.latitude >= 21.398361 && loc.latitude <= 21.430332
                                && loc.longitude >= 39.864674 && loc.longitude <= 39.912254) {
                            String locString = loc.latitude + "," + loc.longitude;
                            new RotaTask(context, mMap, locString, editTo).execute(distance, estTime);
                        } else {
                            Toast.makeText(context, "You are not in Mina. Directions cannot be calculated", Toast.LENGTH_LONG).show();
                            Intent intentMain = new Intent(MapsActivity.this, MainActivity.class);
                            finish();
                            startActivity(intentMain);
                        }
                    }
                });
            } else new RotaTask(this, mMap, editFrom, editTo).execute(distance, estTime);
        }
        //new GetPathPoints(this, mMap, editFrom, editTo).execute();
        setUpMapIfNeeded();
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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
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
    }
}
