package sa.iff.minatentlocator;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import sa.iff.minatentlocator.GraphUtil.DjikstraEngine;
import sa.iff.minatentlocator.GraphUtil.Vertex;

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

    public RotaTask(final Context context, final GoogleMap gMap, final String editFrom, final String editTo) {
        this.context = context;
        this.gMap= gMap;
        this.editFrom = editFrom;
        this.editTo = editTo;
    }

    public LatLng getLatLng(String location) {
        String[] loc = location.split(",");
        String Lat = loc[0], Lng = loc[1];
        return new LatLng(Double.parseDouble(Lat), Double.parseDouble(Lng));
    }

    protected void execute() {
        engine = new DjikstraEngine(GraphMaker.makeGraph(context));
        /*Vertex source = GraphMaker.getNearestNode(lstLatLng.get(0));
        Vertex destination = GraphMaker.getNearestNode(lstLatLng.get(lstLatLng.size()-1));
        Vertex source = GraphMaker.getNearestNode(getLatLng(editFrom));
        Vertex destination = GraphMaker.getNearestNode(getLatLng(editTo));*/
        Vertex source = GraphMaker.getNearestNode(getLatLng("21.397910,39.899868"));
        Vertex destination = GraphMaker.getNearestNode(getLatLng("21.397638,39.900956"));
        engine.execute(source);
        final PolylineOptions polylines = new PolylineOptions();
        polylines.color(Color.BLUE);
        if (engine.getPath(destination) == null)
            Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();

        else {
            for (final Vertex nodes : engine.getPath(destination)) {
                polylines.add(nodes.getCoordinates());
            }
            final MarkerOptions markerA = new MarkerOptions();
            markerA.position(source.getCoordinates());
            markerA.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            final MarkerOptions markerB = new MarkerOptions();
            markerB.position(destination.getCoordinates());
            markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source.getCoordinates(), 10));
            gMap.addMarker(markerA);
            gMap.addPolyline(polylines);
            gMap.addMarker(markerB);
        }
    }

    protected void showPath() {
        GraphMaker.nodes = new ArrayList<>(GraphMaker.nodeMapping.values());
        Vertex source = GraphMaker.nodes.get(0);
        Vertex destination = GraphMaker.nodes.get(GraphMaker.nodes.size()-1);
        final PolylineOptions polylines = new PolylineOptions();
        polylines.color(Color.BLUE);
        for (final Vertex node : GraphMaker.nodes) {
            polylines.add(node.getCoordinates());
        }
        final MarkerOptions markerA = new MarkerOptions();
        markerA.position(source.getCoordinates());
        markerA.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        final MarkerOptions markerB = new MarkerOptions();
        markerB.position(destination.getCoordinates());
        markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source.getCoordinates(), 10));
        gMap.addMarker(markerA);
        gMap.addPolyline(polylines);
        gMap.addMarker(markerB);
    }
}

