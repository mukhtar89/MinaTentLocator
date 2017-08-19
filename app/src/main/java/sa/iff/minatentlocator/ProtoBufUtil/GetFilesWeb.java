package sa.iff.minatentlocator.ProtoBufUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import sa.iff.minatentlocator.Utils.GenerateNotification;

/**
 * Created by Mukhtar on 9/18/2015.
 */
public class GetFilesWeb extends AsyncTask<String, Integer, Boolean> {

    private Context context;
    private File path;

    private String place;
    private Snackbar snackbar;
    private Handler handler;
    private GenerateNotification downloadNotification;

    public GetFilesWeb(Context context, String place, Snackbar snackbar, Handler handleDownloadFinished) {
        this.context = context;
        path = this.context.getExternalFilesDir(null);
        this.place = place;
        this.snackbar = snackbar;
        this.handler = handleDownloadFinished;
        downloadNotification = new GenerateNotification(context, place);
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
            int read = 0, total = 0, pub = 0;
            byte[] bytes = new byte[4096];
            while ((read = inputVertex.read(bytes)) != -1) {
                streamVertex.write(bytes, 0, read);
                total+=read;
                if (pub < 100) pub++;
                else {
                    pub = 0;
                    publishProgress((total * 100) / (lenVertex + lenEdge));
                }
            }
            bytes = new byte[4096];
            while ((read = inputEdge.read(bytes)) != -1) {
                streamEdge.write(bytes, 0, read);
                total+=read;
                if (pub < 100) pub++;
                else {
                    pub = 0;
                    publishProgress((total * 100) / (lenVertex + lenEdge));
                }
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
        downloadNotification.showNotification();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        downloadNotification.completed();
        snackbar.show();
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                Message msg = new Message();
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        downloadNotification.setProgress(values[0]);
    }
}
