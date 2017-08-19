package sa.iff.minatentlocator.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import sa.iff.minatentlocator.R;
import sa.iff.minatentlocator.Utils.StringManipulation;

/**
 * Created by mukht on 10/30/2016.
 */

public class PermissionManager {

    private Context context;
    private Activity activity;
    private AlertDialog.Builder dialogPermissionBuilder = null;
    private String[] permissions;

    public PermissionManager(Context context) {
        this.context = context;
        this.activity = (Activity) context;
    }

    private void getPermission(String type) {
        if (ActivityCompat.checkSelfPermission(context, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                dialogPermissionBuilder = new AlertDialog.Builder(activity);
                dialogPermissionBuilder.setTitle(StringManipulation.CapsFirst(type) + " Access")
                        .setIcon(R.drawable.logo)
                        .setMessage("CargoMate Driver requires you to grant permission to the App to access your " + type + ".")
                        .setCancelable(false)
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
                                dialogPermissionBuilder.show();
                            }
                        })
                        .setNeutralButton("EXIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                            }
                        })
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(activity, permissions, 1);
            }
        }
}

    public void getLocationPermission()  {
        permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        getPermission("currentLocation");
    }

    public void getWriteStoragePermission()  {
        permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        getPermission("storage");
    }

    public void getReadStoragePermission()  {
        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        getPermission("storage");
    }

    public void getPhonePermission()  {
        permissions = new String[]{Manifest.permission.CALL_PHONE};
        getPermission("phone");
    }

    public void getSmsPermission()  {
        permissions = new String[]{Manifest.permission.SEND_SMS};
        getPermission("message");
    }

    public void getCameraPermission()  {
        permissions = new String[]{Manifest.permission.CAMERA};
        getPermission("camera");
    }

    public boolean checkReadStoragePermission() {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public boolean checkWriteStoragePermission() {
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public boolean checkPhonePermission() {
        String permission = "android.permission.CALL_PHONE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public boolean checkSmsPermission() {
        String permission = "android.permission.SEND_SMS";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public boolean checkCameraPermission() {
        String permission = "android.permission.CAMERA";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public Boolean getDialogStatus() {
        if (dialogPermissionBuilder == null)
            return false;
        return true;
    }
}
