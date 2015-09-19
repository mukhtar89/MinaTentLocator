package sa.iff.minatentlocator;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

/**
 * Created by Mukhtar on 9/15/2015.
 */
public class Locations {

    private Context context;

    public Locations(Context context) {
        this.context = context;
    }

    public Hashtable<String, String> parseLocations() throws IOException {
        Hashtable<String, String> locations = new Hashtable<>();
        InputStream inputStream = context.getAssets().open("mina_locations.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String tempName;
        String tempLat, tempLng;
        while ((line = reader.readLine()) != null) {
            tempName = line.substring(0, line.indexOf(","));
            tempLat = line.substring(line.indexOf("\"")+1, line.lastIndexOf(","));
            tempLng = line.substring(line.lastIndexOf(","), line.length() - 1);
            locations.put(tempName, tempLat+tempLng);
        }
        return locations;
    }
}
