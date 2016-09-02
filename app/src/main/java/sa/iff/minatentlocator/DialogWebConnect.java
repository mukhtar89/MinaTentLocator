package sa.iff.minatentlocator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by Mukhtar on 8/29/2015.
 */
public class DialogWebConnect {

    private Context context;
    private final AlertDialog.Builder builderNoWeb, builderWebSelect;

    public DialogWebConnect(Context context, Activity activity) {
        this.context = context;
        this.builderNoWeb = new AlertDialog.Builder(activity);
        this.builderWebSelect = new AlertDialog.Builder(activity);
    }

    public void show() {
        builderNoWeb.setTitle("No Internet!");
        builderNoWeb.setMessage("Hajj Navigator detected that your system is not connected to Internet.");
        builderNoWeb.setIcon(R.drawable.tent_logo);
        builderNoWeb.setCancelable(true);
        builderNoWeb.setPositiveButton(R.string.dialog_internet, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                builderWebSelect.setTitle("No Internet!");
                builderWebSelect.setIcon(R.drawable.tent_logo);
                builderWebSelect.setMessage("Hajj Navigator detected that your system is not connected to Internet.");
                builderWebSelect.setCancelable(true);
                builderWebSelect.setPositiveButton(R.string.dialog_wifi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                builderWebSelect.setNegativeButton(R.string.dialog_data, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final  Intent intent=new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        final ComponentName cn = new ComponentName("com.android.phone","com.android.phone.Settings");
                        intent.setComponent(cn);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        //context.startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
                    }
                });
                builderWebSelect.show();
            }
        });
        builderNoWeb.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builderNoWeb.show();
    }
}

