package sa.iff.minatentlocator.MapUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import sa.iff.minatentlocator.Activities.MapsActivity;
import sa.iff.minatentlocator.GraphUtil.GraphMaker;
import sa.iff.minatentlocator.GraphUtil.DjikstraEngine;
import sa.iff.minatentlocator.GraphUtil.DjikstraEngineConcurrent;
import sa.iff.minatentlocator.GraphUtil.FavouritePredecessors;
import sa.iff.minatentlocator.GraphUtil.Graph;
import sa.iff.minatentlocator.GraphUtil.Vertex;
import sa.iff.minatentlocator.Utils.PermissionManager;
import sa.iff.minatentlocator.Dialogs.SwitchNetworkDialog;

import static sa.iff.minatentlocator.Utils.AnimationPack.crossFade;

/**
 * Created by Mukhtar on 8/26/2015.
 */
public class RouteCalculateEngine {

    private static final String TOAST_MSG = "Sorry, No route found...!!";
    private static Context context;
    private static GoogleMap gMap;
    private static String editFrom, editFromLabel;
    private static String editTo, editToLabel;
    private static DjikstraEngine engine;
    private static DjikstraEngineConcurrent engineConcurrent;
    public static Locations locations;
    private static String place;
    public static Graph graph = null;
    private static Snackbar snackbar;
    public static FavouritePredecessors favouritePredecessors;
    public static PermissionManager locationPermission;
    private static boolean errorFile = false;
    public static Activity activity;
    private static SharedPreferences sharedDefaultPreferences;

    public static void setParams(final Context context, final GoogleMap gMap, final String editFrom, final String editTo, String place,
                                 String editFromLabel, String editToLabel, Snackbar snackbarSetFavourite, Activity activity) {
        RouteCalculateEngine.graph = null;
        RouteCalculateEngine.context = context;
        RouteCalculateEngine.gMap = gMap;
        RouteCalculateEngine.editFrom = editFrom;
        RouteCalculateEngine.editTo = editTo;
        RouteCalculateEngine.editFromLabel = editFromLabel;
        RouteCalculateEngine.editToLabel = editToLabel;
        RouteCalculateEngine.place = place;
        RouteCalculateEngine.locations = new Locations(context);
        RouteCalculateEngine.snackbar = snackbarSetFavourite;
        RouteCalculateEngine.favouritePredecessors = new FavouritePredecessors(context, place, editFromLabel);
        RouteCalculateEngine.locationPermission = new PermissionManager(context);
        RouteCalculateEngine.activity = activity;
        RouteCalculateEngine.sharedDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static LatLng getLatLng(String location) {
        String[] loc = location.split(",");
        String Lat = loc[0], Lng = loc[1];
        return new LatLng(Double.parseDouble(Lat), Double.parseDouble(Lng));
    }

    public static void executeFull(final TextView distance, final TextView estTime, final TextView progressText, final RelativeLayout progressLayout, final LinearLayout mainLayout) {
        final SwitchNetworkDialog switchNetworkDialog = new SwitchNetworkDialog();
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
                polylines.color(Color.BLUE);
                if (switchNetworkDialog.isOnline(context))
                    fetchPathFromGoogle(source, destination, polylines);
                else {
                    while (graph == null)
                        graph = GraphMaker.makeGraph(context, place);
                    DjikstraEngine.setGraph(graph);
                    DjikstraEngine.execute(GraphMaker.getNearestNode(source));
                    /*ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
                    engineConcurrent = new DjikstraEngineConcurrent(graph);
                    engineConcurrent.executeConcurrent(GraphMaker.getNearestNode(source), executor);
                    try {
                        if (executor.awaitTermination(3, TimeUnit.MINUTES)) {*/
                    if (DjikstraEngine.getPath(GraphMaker.getNearestNode(destination)) == null) {
                        //if (engineConcurrent.getPath(GraphMaker.getNearestNode(destination)) == null) {
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
                        for (final Vertex nodes : DjikstraEngine.getPath(GraphMaker.getNearestNode(destination))) {
                            //for (final Vertex nodes : engineConcurrent.getPath(GraphMaker.getNearestNode(destination))) {
                            polylines.add(nodes.getCoordinates());
                        }
                        polylines.add(destination);
                    }
                        /*}
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                }
                return polylines;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                crossFade(context, progressLayout, mainLayout, null);
                progressText.setText("Please wait while calculating route....");
            }

            @Override
            protected void onPostExecute(final PolylineOptions polylines) {
                super.onPostExecute(polylines);
                mainLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        crossFade(context, mainLayout, progressLayout, null);
                    }
                }, 1000);
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
                gMap.addPolyline(polylines);
                gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(source);
                        builder.include(destination);
                        for (LatLng point : polylines.getPoints())
                            builder.include(point);
                        LatLngBounds bounds = builder.build();
                        final int zoomWidth = context.getResources().getDisplayMetrics().widthPixels;
                        final int zoomHeight = context.getResources().getDisplayMetrics().heightPixels;
                        final int zoomPadding = (int) (zoomWidth * 0.2);
                        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,zoomWidth,zoomHeight,zoomPadding));
                    }
                });
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
                if (!MapsActivity.favourite && !editFromLabel.equals("My Location")) {
                    snackbar.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            snackbar.dismiss();
                        }
                    },10000);
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
        if (locationPermission.checkLocationPermission()) gMap.setMyLocationEnabled(true);
        /*final MarkerOptions myLoc = new MarkerOptions();
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
        });*/
    }

    public static void executeDestination(final TextView distance, final TextView estTime, final String newDestin, final String newEditToLabel,
                                          final TextView progressText, final RelativeLayout progressLayout, final LinearLayout mainLayout) {
        final SwitchNetworkDialog switchNetworkDialog = new SwitchNetworkDialog();
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
                    crossFade(context, mainLayout, progressLayout, null);
                    executeFull(distance, estTime, progressText, progressLayout, mainLayout);
                }
                return false;
            }
        });
        new AsyncTask<Void, Void, PolylineOptions>() {
            LatLng source = getLatLng(editFrom.replace(" ", ""));
            LatLng newDestination = getLatLng(newDestin);

            @Override
            protected PolylineOptions doInBackground(Void... params) {
                polylines.color(Color.BLUE);
                if (switchNetworkDialog.isOnline(context))
                    fetchPathFromGoogle(source, newDestination, polylines);
                else {
                    try {
                        while (graph == null)
                            readGraphSource();
                    } catch (IOException e) {
                        cancel(true);
                        Message message = new Message();
                        message.arg1 = 1;
                        errorFileRead.sendMessage(message);
                    }
                    Vertex destin = getNode(newDestination);
                    if (DjikstraEngine.getPath(destin) == null) {
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
                        for (final Vertex nodes : DjikstraEngine.getPath(destin)) {
                            polylines.add(nodes.getCoordinates());
                        }
                        polylines.add(newDestination);
                    }
                }
                return polylines;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (MapsActivity.favourite){
                    crossFade(context, progressLayout, mainLayout, null);
                    progressText.setText("Please wait while calculating route....");
                }
                else{
                    crossFade(context, progressLayout, mainLayout, null);
                    progressText.setText("Please wait while calculating route for different Destination....");
                }
            }

            @Override
            protected void onPostExecute(final PolylineOptions polylines) {
                super.onPostExecute(polylines);
                mainLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        crossFade(context, mainLayout, progressLayout, null);
                    }
                }, 1000);
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
                gMap.addPolyline(polylines);
                gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(source);
                        builder.include(newDestination);
                        for (LatLng point : polylines.getPoints())
                            builder.include(point);
                        LatLngBounds bounds = builder.build();
                        final int zoomWidth = context.getResources().getDisplayMetrics().widthPixels;
                        final int zoomHeight = context.getResources().getDisplayMetrics().heightPixels;
                        final int zoomPadding = (int) (zoomWidth * 0.2);
                        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,zoomWidth,zoomHeight,zoomPadding));
                    }
                });
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
                /*if (!MapsActivity.favourite) {
                    snackbar.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            snackbar.dismiss();
                        }
                    },7000);
                }*/
            }
        }.execute();
        if (locationPermission.checkLocationPermission()) gMap.setMyLocationEnabled(true);
    }

    protected static void showPath() {
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

    public static void saveGraphSource(Activity activity) throws IOException {
        favouritePredecessors.setPredecessors(DjikstraEngine.getPredecessors());
        favouritePredecessors.setNodes(DjikstraEngine.getNodes());
        favouritePredecessors.saveObject(activity);
        MapsActivity.favourite = true;
    }

    public static boolean readGraphSource() throws IOException {
        favouritePredecessors = favouritePredecessors.readObject();
        if (favouritePredecessors.getNodes().size() < 10)
            return false;
        while (graph == null)
            graph = GraphMaker.makeGraph(context, place);
        DjikstraEngine.setGraph(graph);
        DjikstraEngine.setPredecessors(favouritePredecessors.getPredecessors());
        DjikstraEngine.setNodes(favouritePredecessors.getNodes());
        return true;
    }

    private static Vertex getNode(LatLng point) {
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

    private static void fetchPathFromGoogle(LatLng source, LatLng destination, PolylineOptions polylines) {
        try {
            final StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/xml?language=eng&mode=walking");
            url.append("&origin=");
            url.append(source.latitude).append(",").append(source.longitude);
            url.append("&destination=");
            url.append(destination.latitude).append(",").append(destination.longitude);
            url.append("&key=AIzaSyCwdeOYNKaFK2TC6tuFhBhaypj7vflk6qw");

            Log.d("fetchPathFromGoogle", "url: " + url);
            final InputStream stream = new URL(url.toString()).openStream();
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setIgnoringComments(true);
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(stream);
            document.getDocumentElement().normalize();

            final String status = document.getElementsByTagName("status").item(0).getTextContent();
            if (!"OK".equals(status)) {
                return;
            }
            final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
            final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
            final int length = nodeListStep.getLength();
            polylines.add(source);
            for (int i = 0; i < length; i++) {
                final Node nodeStep = nodeListStep.item(i);
                if (nodeStep.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elementStep = (Element) nodeStep;
                    polylines.addAll(PolyUtil.decode(elementStep.getElementsByTagName("points").item(0).getTextContent()));
                }
            }
            polylines.add(destination);

        } catch (final Exception ignored) {}
    }
}

