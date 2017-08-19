package sa.iff.minatentlocator.MapUtils;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * Created by Mukhtar on 9/15/2015.
 */
public class Locations {

    private Context context;
    private Hashtable<String, String> csv_files = new Hashtable<>();
    private Hashtable<String, HashSet<String>> Categories = new Hashtable<>();
    private Hashtable<String, LatLng> locMin = new Hashtable<>();
    private Hashtable<String, LatLng> locMax = new Hashtable<>();
    private Hashtable<String, String[]> urlFiles = new Hashtable<>();

    public Locations(Context context) {
        this.context = context;
        csv_files.put("Mina", "mina_locations.csv");
        csv_files.put("Makkah", "makkah_locations.csv");
        csv_files.put("Aziziyah", "aziziyah_locations.csv");
        locMin.put("Mina", new LatLng(21.398361, 39.864674));
        locMax.put("Mina", new LatLng(21.430332, 39.912254));
        locMin.put("Aziziyah", new LatLng(21.383519, 39.843803));
        locMax.put("Aziziyah", new LatLng(21.424618, 39.886822));
        locMin.put("Makkah", new LatLng(21.381307, 39.759305));
        locMax.put("Makkah", new LatLng(21.476456, 39.848789));
        //urlFiles.put("Mina", new String[]{"vertexex", "edges"});
        urlFiles.put("Mina", new String[]{"https://goo.gl/ST0qjS", "https://goo.gl/XukhhN"});
        urlFiles.put("Aziziyah", new String[]{"https://goo.gl/e8eoaZ", "https://goo.gl/DyDqwl"});
        urlFiles.put("Makkah", new String[]{"https://goo.gl/Fk0wVU", "https://goo.gl/jbclZR"});
    }

    public Hashtable<String, String[]> parseLocations(String place) throws IOException {
        Hashtable<String, String[]> locations = new Hashtable<>();
        InputStream inputStream = context.getAssets().open(csv_files.get(place));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String tempName;
        String tempLat, tempLng;
        String tempCategory;
        if (!Categories.containsKey(place))
            Categories.put(place, new HashSet<String>());
        while ((line = reader.readLine()) != null) {
            tempName = line.substring(0, line.indexOf(","));
            tempLat = line.substring(line.indexOf("\"")+1, line.indexOf(",", line.indexOf("\"")+1));
            tempLng = line.substring(line.indexOf(",", line.indexOf("\"")+1)+2, line.lastIndexOf(",")-1);
            tempCategory = line.substring(line.lastIndexOf(",")+1, line.length());
            if (!Categories.get(place).contains(tempCategory))
                Categories.get(place).add(tempCategory);
            locations.put(tempName, new String[] {tempLat+","+tempLng, tempCategory});
        }
        return locations;
    }

    public HashSet<String> returnCategories (String place) {
        return Categories.get(place);
    }

    public LatLng[] returnBounds(String place) {
        return new LatLng[]{locMin.get(place), locMax.get(place)};
    }

    public String[] returnUrls (String place) {
        return urlFiles.get(place);
    }
}
