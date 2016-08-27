package sa.iff.minatentlocator.ProtoBufUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import sa.iff.minatentlocator.DialogWebConnect;
import sa.iff.minatentlocator.RotaTask;

/**
 * Created by Mukhtar on 9/18/2015.
 */
public class GetFilesWeb extends AsyncTask<String, Integer, Boolean> {

    private Context context;
    private File path;
    private GoogleMap gMap;
    private String editFrom, editFromLabel;
    private String editTo, editToLabel;
    private ProgressDialog progressMeta;
    private TextView distance, estTime;
    private DialogWebConnect dialogWebConnect;
    private NetworkInfo activeNetworkInfo;
    private String place;
    private Snackbar snackbarSetFavourite;

    public GetFilesWeb(Context context, GoogleMap gMap, String editFrom, String editTo, TextView distance, TextView estTime, String place, String editFromLabel, String editToLabel, Snackbar snackbar) {
        this.context = context;
        path = this.context.getExternalFilesDir(null);
        this.gMap = gMap;
        this.editFrom = editFrom;
        this.editFromLabel = editFromLabel;
        this.editTo = editTo;
        this.editToLabel = editToLabel;
        this.distance = distance;
        this.estTime = estTime;
        this.place = place;
        this.snackbarSetFavourite = snackbar;
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
            URL urlVertex = new URL(params[0]), urlEdge = new URL(params[1]);
            URLConnection connectionVertex = urlVertex.openConnection();
            URLConnection connectionEdge = urlVertex.openConnection();
            int lenVertex = connectionVertex.getContentLength();
            int lenEdge = connectionEdge.getContentLength();
            InputStream inputVertex = urlVertex.openStream();
            InputStream inputEdge = urlEdge.openStream();
            File fileVertex = new File(path, "vertexes_" + place + ".ser");
            File fileEdge = new File(path, "edges_" + place + ".ser");
            FileOutputStream streamVertex = new FileOutputStream(fileVertex);
            FileOutputStream streamEdge = new FileOutputStream(fileEdge);
            int read = 0, total = 0;
            byte[] bytes = new byte[4096];
            while ((read = inputVertex.read(bytes)) != -1) {
                streamVertex.write(bytes, 0, read);
                total+=read;
                publishProgress((total*100)/(lenVertex+lenEdge));
            }
            bytes = new byte[4096];
            while ((read = inputEdge.read(bytes)) != -1) {
                streamEdge.write(bytes, 0, read);
                total+=read;
                publishProgress((total*100)/(lenVertex+lenEdge));
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
        progressMeta.setMessage("Initial downloading of Map Directions Metadata. Please wait...");
        progressMeta.setIndeterminate(false);
        progressMeta.setMax(100);
        progressMeta.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressMeta.setCancelable(false);
        progressMeta.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        progressMeta.dismiss();
        if (!editFrom.equals("myloc")) {
            if (result)
                new RotaTask(context, gMap, editFrom, editTo, place, editFromLabel, editToLabel, snackbarSetFavourite).execute(distance, estTime);
        }
        else
            new RotaTask(context, gMap, editTo, editTo, place, editFromLabel, editToLabel, snackbarSetFavourite).execute(distance, estTime);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressMeta.setProgress(values[0]);
    }
}
