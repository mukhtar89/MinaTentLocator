package sa.iff.minatentlocator;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.NotificationCompat;

/**
 * Created by mukht on 8/29/2016.
 */
public class GenerateNotification {

    private static Context context;
    private String place;
    public static NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public GenerateNotification(Context context, String place) {
        this.context = context;
        this.place = place;
        notificationBuilder = new NotificationCompat.Builder(this.context);
        notificationBuilder.setSmallIcon(R.drawable.ic_file_download_black_48dp);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.tent_logo));
        notificationBuilder.setAutoCancel(true);
        notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            notificationBuilder.setContentText("Initial downloading of Map Directions Metadata of " + place);
        else {
            notificationBuilder.setContentText("Initial downloading of Map Directions Metadata of " + place);
        }
        notificationBuilder.setContentTitle("Downloading Metadata");
        notificationManager.notify(place.charAt(1), notificationBuilder.build());

    }

    public void setProgress(Integer value) {
        notificationBuilder.setProgress(100, value, false);
        notificationManager.notify(place.charAt(1), notificationBuilder.build());
    }

    public void completed() {
        notificationBuilder.setContentTitle("Downloaded Metadata");
        notificationBuilder.setContentText(place + " Map MetaData Download complete")
                .setProgress(0,0,false);
        notificationManager.notify(place.charAt(1), notificationBuilder.build());
    }
}

