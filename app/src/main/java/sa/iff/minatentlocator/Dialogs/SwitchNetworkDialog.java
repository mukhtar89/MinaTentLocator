package sa.iff.minatentlocator.Dialogs;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import sa.iff.minatentlocator.R;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Created by mukht on 1/1/2017.
 */

public class SwitchNetworkDialog extends DialogFragment {

    private static final String TAG = SwitchNetworkDialog.class.getSimpleName();
    private WifiManager wifiManager;
    private Boolean isWifiConn, isMobileConn;
    private ConnectivityManager connMgr;

    public static SwitchNetworkDialog newInstance() {
        Bundle args = new Bundle();
        SwitchNetworkDialog fragment = new SwitchNetworkDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (connMgr != null && connMgr.getActiveNetwork() != null)
                    switch (connMgr.getActiveNetworkInfo().getType()) {
                        case TYPE_MOBILE:
                            isWifiConn = true;
                            break;
                        case TYPE_WIFI:
                            isMobileConn = true;
                            break;
                        default:
                            break;
                    }
            }
        }catch (NoSuchMethodError ignored) {}
    }

    public boolean isOnline(Context context) {
        connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder switchNetworkDialog = new AlertDialog.Builder(getActivity());
        switchNetworkDialog.setCancelable(false)
                .setTitle("Select an Network Service")
                .setIcon(R.drawable.tent_logo)
                .setCancelable(false)
                .setMessage("\nPlease select a Network service (WiFi or Mobile Data) to connect to internet for the app to load data")
                .setPositiveButton("WI-FI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wifiManager.setWifiEnabled(true);
                    }
                }).setNegativeButton("MOBILE DATA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(
                                "com.android.settings",
                                "com.android.settings.Settings$DataUsageSummaryActivity"));
                        startActivity(intent);
                    }
                }).setNeutralButton("EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
        });
        return switchNetworkDialog.create();
    }

}
