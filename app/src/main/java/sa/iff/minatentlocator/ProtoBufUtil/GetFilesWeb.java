package sa.iff.minatentlocator.ProtoBufUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import sa.iff.minatentlocator.DialogWebConnect;
import sa.iff.minatentlocator.RotaTask;

/**
 * Created by Mukhtar on 9/18/2015.
 */
public class GetFilesWeb extends AsyncTask<String, Void, Boolean> {

    private Context context;
    private File path;
    private GoogleMap gMap;
    private String editFrom;
    private String editTo;
    private ProgressDialog progressMeta;
    private TextView distance, estTime;
    private DialogWebConnect dialogWebConnect;
    private NetworkInfo activeNetworkInfo;
    private String place;

    public GetFilesWeb(Context context, GoogleMap gMap, String editFrom, String editTo, TextView distance, TextView estTime, String place) {
        this.context = context;
        path = this.context.getExternalFilesDir(null);
        this.gMap = gMap;
        this.editFrom = editFrom;
        this.editTo = editTo;
        this.distance = distance;
        this.estTime = estTime;
        this.place = place;
        progressMeta = new ProgressDialog(this.context);
        dialogWebConnect = new DialogWebConnect(this.context);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            if (!activeNetworkInfo.isConnected())
                dialogWebConnect.show();
        }
        else dialogWebConnect.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            InputStream inputVertex = new URL(params[0]).openStream();
            InputStream inputEdge = new URL(params[1]).openStream();
            File fileVertex = new File(path, "vertexes_" + place + ".ser");
            File fileEdge = new File(path, "edges_" + place + ".ser");
            FileOutputStream streamVertex = new FileOutputStream(fileVertex);
            FileOutputStream streamEdge = new FileOutputStream(fileEdge);
            int read = 0;
            byte[] bytes = new byte[4096];
            while ((read = inputVertex.read(bytes)) != -1) {
                streamVertex.write(bytes, 0, read);
            }
            bytes = new byte[4096];
            while ((read = inputEdge.read(bytes)) != -1) {
                streamEdge.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressMeta.setTitle("Downloading Metadata");
        progressMeta.setMessage("Initial downloading of Map Directions Metadata");
        progressMeta.setCancelable(false);
        progressMeta.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        progressMeta.dismiss();
        if (!editFrom.equals("myloc")) {
            if (result)
                new RotaTask(context, gMap, editFrom, editTo, place).execute(distance, estTime);
        }
        else
            new RotaTask(context, gMap, editTo, editTo, place).execute(distance, estTime);
    }
}
