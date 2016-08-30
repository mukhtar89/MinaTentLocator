package sa.iff.minatentlocator.GraphUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import sa.iff.minatentlocator.Activities.MapsActivity;
import sa.iff.minatentlocator.R;

/**
 * Created by mukht on 8/27/2016.
 */
public class FavouritePredecessors {

    private List<Vertex> nodes;
    private Map<Vertex, Vertex> predecessors;
    private Context context;
    private String place, editFromLabel;
    private File path;

    public FavouritePredecessors(Context context, String place, String editFromLabel) {
        this.context = context;
        this.place = place;
        this.editFromLabel = editFromLabel;
        path = context.getExternalFilesDir(null);
    }

    public Map<Vertex, Vertex> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(Map<Vertex, Vertex> predecessors) {
        this.predecessors = predecessors;
    }

    public List<Vertex> getNodes() {
        return nodes;
    }

    public void setNodes(List<Vertex> nodes) {
        this.nodes = nodes;
    }

    public void saveObject(final Activity activity) throws IOException {

        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                AlertDialog.Builder dialogPermissionBuilder = new AlertDialog.Builder(activity);
                dialogPermissionBuilder.setTitle("Location Access")
                        .setIcon(R.drawable.tent_logo)
                        .setMessage("Hajj Navigator requires you to grant permission to the App for accessing your external storage.")
                        .setCancelable(true)
                        .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(activity, permissions, 1);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Snackbar snackbarDenied = Snackbar.make()
                            }
                        })
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(activity, permissions, 1);
            }
        }

        File saveObjectClassNodes = new File(path, "graph_" + place + "_" + editFromLabel + "_nodes.csv");
        FileWriter writerOutputClass = new FileWriter(saveObjectClassNodes);
        BufferedWriter writer = new BufferedWriter(writerOutputClass);
        for (Vertex vtx : getNodes()) {
            writer.write(vtx.getCoordinates().latitude + "," + vtx.getCoordinates().longitude);
            writer.newLine();
        }
        writer.flush();
        writer.close();

        File saveObjectClassMap= new File(path, "graph_" + place + "_" + editFromLabel + "_map.csv");
        writerOutputClass = new FileWriter(saveObjectClassMap);
        writer = new BufferedWriter(writerOutputClass);
        for (Map.Entry<Vertex, Vertex> entry : getPredecessors().entrySet()) {
            writer.write(entry.getKey().getCoordinates().latitude
                    + "," + entry.getKey().getCoordinates().longitude + "," +
                    entry.getValue().getCoordinates().latitude + "," +
                    entry.getValue().getCoordinates().longitude);
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    public FavouritePredecessors readObject() throws IOException {
        FavouritePredecessors readClass = new FavouritePredecessors(context, place, editFromLabel);
        Hashtable<LatLng, Vertex> nodesSet = new Hashtable<>();
        Map<Vertex, Vertex> predecessorsSet = new HashMap<>();

        File readObjectClassNodes = new File(path, "graph_" + place + "_" + editFromLabel + "_nodes.csv");
        FileReader readerInputClass = new FileReader(readObjectClassNodes);
        BufferedReader reader = new BufferedReader(readerInputClass);
        String line;
        while ((line = reader.readLine()) != null) {
            String[] reading = line.split(",");
            Float lat1 = Float.parseFloat(reading[0]);
            Float lat2 = Float.parseFloat(reading[1]);
            LatLng coordinates = new LatLng(lat1, lat2);
            Vertex vtx = new Vertex(coordinates);
            nodesSet.put(coordinates, vtx);
        }

        File readObjectClassMap = new File(path, "graph_" + place + "_" + editFromLabel + "_map.csv");
        readerInputClass = new FileReader(readObjectClassMap);
        reader = new BufferedReader(readerInputClass);
        while ((line = reader.readLine()) != null) {
            String[] reading = line.split(",");
            Float lat1 = Float.parseFloat(reading[0]);
            Float lat2 = Float.parseFloat(reading[1]);
            LatLng coord1 = new LatLng(lat1, lat2);
            Float lat3 = Float.parseFloat(reading[2]);
            Float lat4 = Float.parseFloat(reading[3]);
            LatLng coord2 = new LatLng(lat3, lat4);
            predecessorsSet.put(nodesSet.get(coord1), nodesSet.get(coord2));
        }
        readClass.setNodes(new ArrayList<>(nodesSet.values()));
        readClass.setPredecessors(predecessorsSet);
        return readClass;
    }
}
