package sa.iff.minatentlocator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

/**
 * Created by Mukhtar on 9/3/2015.
 */
public class AboutInfo extends AlertDialog {

    private Context context;
    private Builder aboutInfo;
    private PackageManager packageManager;

    public AboutInfo(Context context) {
        super(context);
        this.context = context;
        packageManager = context.getPackageManager();
    }

    public void showDialog () throws PackageManager.NameNotFoundException {
        aboutInfo = new Builder(context);
        aboutInfo.setTitle("About Mina Tent Locator");
        aboutInfo.setIcon(R.drawable.tent_logo);
        aboutInfo.setMessage("Mina Tent Locator v" + packageManager.getPackageInfo(context.getPackageName(), 0).versionName +
                "\n\n\tApp Created by Mohammed Mukhtar" +
                "\n\tFor Intermedia Publishing Co.");
        aboutInfo.setCancelable(true);
        aboutInfo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        aboutInfo.show();
    }
}

