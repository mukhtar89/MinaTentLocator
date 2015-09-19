package sa.iff.minatentlocator.ProtoBufUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

    public GetFilesWeb(Context context, GoogleMap gMap, String editFrom, String editTo) {
        this.context = context;
        path = this.context.getExternalFilesDir(null);
        this.gMap = gMap;
        this.editFrom = editFrom;
        this.editTo = editTo;
        progressMeta = new ProgressDialog(this.context);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            InputStream inputVertex = new URL(params[0]).openStream();
            InputStream inputEdge = new URL(params[1]).openStream();
            File fileVertex = new File(path, "vertexes.ser");
            File fileEdge = new File(path, "edges.ser");
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
        progressMeta.setCancelable(true);
        progressMeta.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        progressMeta.dismiss();
        if (!editFrom.equals("myloc")) {
            if (result)
                new RotaTask(context, gMap, editFrom, editTo).execute();
        }
        else
            new RotaTask(context, gMap, editTo, editTo).execute();
    }
}
