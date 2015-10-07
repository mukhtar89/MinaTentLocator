package sa.iff.minatentlocator;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import sa.iff.minatentlocator.Activities.MapsActivity;
import sa.iff.minatentlocator.GraphUtil.DjikstraEngine;
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
    private String editFrom;
    private String editTo;
    private DjikstraEngine engine;
    private ProgressDialog progressEngine;
    private static MapsActivity parent;

    public RotaTask(final Context context, final GoogleMap gMap, final String editFrom, final String editTo) {
        this.context = context;
        this.gMap= gMap;
        this.editFrom = editFrom;
        this.editTo = editTo;
        progressEngine = new ProgressDialog(this.context);
    }

    public LatLng getLatLng(String location) {
        String[] loc = location.split(",");
        String Lat = loc[0], Lng = loc[1];
        return new LatLng(Double.parseDouble(Lat), Double.parseDouble(Lng));
    }

    public void execute(final TextView distance, final TextView estTime) {
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
            LatLng source = getLatLng(editFrom.replace(" ",""));
            LatLng destination = getLatLng(editTo.replace(" ", ""));

            @Override
            protected PolylineOptions doInBackground(Void... params) {
                Graph graph = null;
                while (graph == null)
                    graph = GraphMaker.makeGraph(context);
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
                }
                else {
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
                final MarkerOptions markerA = new MarkerOptions();
                markerA.position(source);
                markerA.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                final MarkerOptions markerB = new MarkerOptions();
                markerB.position(destination);
                markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 15));
                gMap.addMarker(markerA);
                gMap.addPolyline(polylines);
                gMap.addMarker(markerB);
                double dist = 0;  LatLng temp = null;
                for (LatLng point : polylines.getPoints()) {
                    if (temp != null)
                        dist += SphericalUtil.computeDistanceBetween(temp, point);
                    temp = point;
                }
                double time = dist/(1.4*60);
                distance.setText("Distance: " + (int)dist + " meters");
                estTime.setText("Avg Time: " + (int)time + " minutes");
            }
        }.execute();
        gMap.setMyLocationEnabled(true);
        final MarkerOptions myLoc = new MarkerOptions();
        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                if (loc.latitude >= 21.398361 && loc.latitude <= 21.430332
                        && loc.longitude >= 39.864674 && loc.longitude <= 39.912254) {
                    gMap.addMarker(myLoc.position(loc));
                    if (gMap != null)
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                }
            }
        });
    }

    protected void showPath() {
        GraphMaker.makeGraph(context);
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
}

