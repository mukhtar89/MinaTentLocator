package sa.iff.minatentlocator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;

import sa.iff.minatentlocator.Activities.MapsActivity;
import sa.iff.minatentlocator.GraphUtil.DjikstraEngine;
import sa.iff.minatentlocator.GraphUtil.FavouritePredecessors;
import sa.iff.minatentlocator.GraphUtil.Graph;
import sa.iff.minatentlocator.GraphUtil.Vertex;
import sa.iff.minatentlocator.MapUtils.SphericalUtil;

/**
 * Created by Mukhtar on 8/26/2015.
 */
public class RotaTask {

    private static final String TOAST_MSG = "Sorry, No route found...!!";
    private Context context;
    private GoogleMap gMap;
    private String editFrom, editFromLabel;
    private String editTo, editToLabel;
    private DjikstraEngine engine;
    public ProgressDialog progressEngine;
    private Locations locations;
    private String place;
    private Graph graph = null;
    private Snackbar snackbar;
    private FavouritePredecessors favouritePredecessors;
    private LocationPermission locationPermission;
    private boolean errorFile = false;
    private Activity activity;
    private SharedPreferences sharedDefaultPreferences;

    public RotaTask(final Context context, final GoogleMap gMap, final String editFrom, final String editTo, String place, String editFromLabel, String editToLabel, Snackbar snackbarSetFavourite, Activity activity) {
        this.context = context;
        this.gMap = gMap;
        this.editFrom = editFrom;
        this.editTo = editTo;
        this.editFromLabel = editFromLabel;
        this.editToLabel = editToLabel;
        this.place = place;
        progressEngine = new ProgressDialog(this.context);
        locations = new Locations(context);
        this.snackbar = snackbarSetFavourite;
        favouritePredecessors = new FavouritePredecessors(context, place, editFromLabel);
        locationPermission = new LocationPermission(context, activity);
        this.activity = activity;
        sharedDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public LatLng getLatLng(String location) {
        String[] loc = location.split(",");
        String Lat = loc[0], Lng = loc[1];
        return new LatLng(Double.parseDouble(Lat), Double.parseDouble(Lng));
    }

    public void executeFull(final TextView distance, final TextView estTime) {
        final PolylineOptions polylines = new PolylineOptions();
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.arg1 == 1)
                    Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();
                return false;
            }
        });
        new AsyncTask<Void, Void, PolylineOptions>() {
            LatLng source = getLatLng(editFrom.replace(" ", ""));
            LatLng destination = getLatLng(editTo.replace(" ", ""));

            @Override
            protected PolylineOptions doInBackground(Void... params) {
                while (graph == null)
                    graph = GraphMaker.makeGraph(context, place);
                engine = new DjikstraEngine(graph);
                engine.execute(GraphMaker.getNearestNode(source));
                polylines.color(Color.BLUE);
                if (engine.getPath(GraphMaker.getNearestNode(destination)) == null) {
                    final Message msg = new Message();
                    new Thread() {
                        public void run() {
                            msg.arg1 = 1;
                            handler.sendMessage(msg);
                        }
                    }.start();
                    polylines.add(source);
                    polylines.add(destination);
                } else {
                    polylines.add(source);
                    for (final Vertex nodes : engine.getPath(GraphMaker.getNearestNode(destination))) {
                        polylines.add(nodes.getCoordinates());
                    }
                    polylines.add(destination);
                }
                return polylines;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressEngine.setTitle("Calculating route");
                progressEngine.setMessage("Please wait while calculating route....");
                progressEngine.setCancelable(false);
                progressEngine.show();
            }

            @Override
            protected void onPostExecute(PolylineOptions polylines) {
                super.onPostExecute(polylines);
                progressEngine.dismiss();
                final MarkerOptions markerAoptions = new MarkerOptions();
                markerAoptions.position(source)
                        .title("Origin: " + editFromLabel)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                Marker markerA = gMap.addMarker(markerAoptions);
                markerA.showInfoWindow();
                final MarkerOptions markerBOptions = new MarkerOptions();
                markerBOptions.position(destination)
                                .title("Destination: " + editToLabel)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                Marker markerB = gMap.addMarker(markerBOptions);
                markerB.showInfoWindow();
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 15));
                gMap.addPolyline(polylines);
                double dist = 0;
                LatLng temp = null;
                for (LatLng point : polylines.getPoints()) {
                    if (temp != null)
                        dist += SphericalUtil.computeDistanceBetween(temp, point);
                    temp = point;
                }
                double time = dist / (1.4 * 60);
                if (sharedDefaultPreferences.getBoolean("distance_unit", false))
                    distance.setText("(" + (int) (dist * 3.28084) + " ft)");
                else distance.setText("(" + ((int) dist)  + " m)");
                estTime.setText((int) time + " min");
                if (!MapsActivity.favourite) {
                    snackbar.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            snackbar.dismiss();
                        }
                    },7000);
                }
                if (errorFile)
                    try {
                        saveGraphSource(activity);
                        errorFile = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }.execute();
        locationPermission.checkLocationPermission();
        gMap.setMyLocationEnabled(true);
        final MarkerOptions myLoc = new MarkerOptions();
        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                if (loc.latitude >= locations.returnBounds(place)[0].longitude && loc.latitude <= locations.returnBounds(place)[1].longitude
                        && loc.longitude >= locations.returnBounds(place)[0].latitude && loc.longitude <= locations.returnBounds(place)[1].latitude) {
                    gMap.addMarker(myLoc.position(loc));
                    if (gMap != null)
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                }
            }
        });
    }

    public void executeDestination(final TextView distance, final TextView estTime, final String newDestin, final String newEditToLabel, final SharedPreferences sharedPreferences) {
        final PolylineOptions polylines = new PolylineOptions();
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.arg1 == 1)
                    Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();
                return false;
            }
        });
        final Handler errorFileRead = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.arg1 == 1) {
                    errorFile = true;
                    progressEngine.dismiss();
                    executeFull(distance, estTime);
                }
                return false;
            }
        });
        new AsyncTask<Void, Void, PolylineOptions>() {
            LatLng source = getLatLng(editFrom.replace(" ", ""));
            LatLng newDestination = getLatLng(newDestin);

            @Override
            protected PolylineOptions doInBackground(Void... params) {
                try {
                    readGraphSource();
                } catch (IOException e) {
                    cancel(true);
                    Message message = new Message();
                    message.arg1 = 1;
                    errorFileRead.sendMessage(message);
                }
                polylines.color(Color.BLUE);
                Vertex destin = getNode(newDestination);
                if (engine.getPath(destin) == null) {
                    final Message msg = new Message();
                    new Thread() {
                        public void run() {
                            msg.arg1 = 1;
                            handler.sendMessage(msg);
                        }
                    }.start();
                    polylines.add(source);
                    polylines.add(newDestination);
                } else {
                    polylines.add(source);
                    for (final Vertex nodes : engine.getPath(destin)) {
                        polylines.add(nodes.getCoordinates());
                    }
                    polylines.add(newDestination);
                }
                return polylines;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (MapsActivity.favourite){
                    progressEngine.setTitle("Loading route");
                    progressEngine.setMessage("Please wait while loading route....");
                }
                else{
                    progressEngine.setTitle("Calculating route");
                    progressEngine.setMessage("Please wait while calculating route for different Destination....");
                }
                progressEngine.setCancelable(false);
                progressEngine.show();
            }

            @Override
            protected void onPostExecute(PolylineOptions polylines) {
                super.onPostExecute(polylines);
                progressEngine.dismiss();
                gMap.clear();
                final MarkerOptions markerAoptions = new MarkerOptions();
                markerAoptions.position(source)
                        .title("Origin: " + editFromLabel)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                Marker markerA = gMap.addMarker(markerAoptions);
                markerA.showInfoWindow();
                final MarkerOptions markerBOptions = new MarkerOptions();
                markerBOptions.position(newDestination)
                        .title("Destination: " + newEditToLabel)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                Marker markerB = gMap.addMarker(markerBOptions);
                markerB.showInfoWindow();
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 15));
                gMap.addPolyline(polylines);
                double dist = 0;
                LatLng temp = null;
                for (LatLng point : polylines.getPoints()) {
                    if (temp != null)
                        dist += SphericalUtil.computeDistanceBetween(temp, point);
                    temp = point;
                }
                double time = dist / (1.4 * 60);
                if (sharedDefaultPreferences.getBoolean("distance_unit", false))
                    distance.setText("(" + (int) (dist * 3.28084) + " ft)");
                else distance.setText("(" + ((int) dist)  + " m)");
                estTime.setText((int) time + " min");
                if (!MapsActivity.favourite) {
                    snackbar.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            snackbar.dismiss();
                        }
                    },7000);
                }
            }
        }.execute();
        locationPermission.checkLocationPermission();
        gMap.setMyLocationEnabled(true);
        final MarkerOptions myLoc = new MarkerOptions();
        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                if (loc.latitude >= locations.returnBounds(place)[0].longitude && loc.latitude <= locations.returnBounds(place)[1].longitude
                        && loc.longitude >= locations.returnBounds(place)[0].latitude && loc.longitude <= locations.returnBounds(place)[1].latitude) {
                    gMap.addMarker(myLoc.position(loc));
                    if (gMap != null)
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                }
            }
        });
    }

    protected void showPath() {
        GraphMaker.makeGraph(context, place);
        GraphMaker.nodes = new ArrayList<>(GraphMaker.readNodeMapping.values());
        Vertex source = GraphMaker.nodes.get(0);
        Vertex destination = GraphMaker.nodes.get(GraphMaker.nodes.size()-1);
        final MarkerOptions markerA = new MarkerOptions();
        markerA.position(source.getCoordinates());
        markerA.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        final MarkerOptions markerB = new MarkerOptions();
        markerB.position(destination.getCoordinates());
        markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source.getCoordinates(), 10));
        gMap.addMarker(markerA);
        for (final Vertex node : GraphMaker.nodes) {
            final MarkerOptions marker = new MarkerOptions();
            marker.position(node.getCoordinates());
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            gMap.addMarker(marker);
        }
    }

    public void saveGraphSource(Activity activity) throws IOException {
        favouritePredecessors.setPredecessors(engine.getPredecessors());
        favouritePredecessors.setNodes(engine.getNodes());
        favouritePredecessors.saveObject(activity);
        MapsActivity.favourite = true;
    }

    public boolean readGraphSource() throws IOException {
        favouritePredecessors = favouritePredecessors.readObject();
        if (favouritePredecessors.getNodes().size() < 10)
            return false;
        while (graph == null)
            graph = GraphMaker.makeGraph(context, place);
        engine = new DjikstraEngine(graph);
        engine.setPredecessors(favouritePredecessors.getPredecessors());
        engine.setNodes(favouritePredecessors.getNodes());
        return true;
    }

    private Vertex getNode(LatLng point) {
        Double distance = Double.MAX_VALUE;
        Vertex pointNode = null;
        for (Vertex vertex : engine.getNodes()) {
            if (distance > SphericalUtil.computeDistanceBetween(vertex.getCoordinates(), point)) {
                distance = SphericalUtil.computeDistanceBetween(vertex.getCoordinates(), point);
                pointNode = vertex;
            }
        }
        return pointNode;
    }
}

