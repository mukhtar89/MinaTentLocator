package sa.iff.minatentlocator;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mukht on 8/26/2016.
 */
public class SharedPrefArrayUtils {

    private SharedPreferences sharedPreferences;
    private String place;

    public SharedPrefArrayUtils(SharedPreferences sharedPreferences, String place) {
        this.sharedPreferences = sharedPreferences;
        this.place = place;
    }

    public void saveArray(ArrayList<String> arrayList) {
        Set<String> mySet = new HashSet<>(arrayList);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putStringSet("fav_" + place, mySet);
        prefEditor.apply();
    }

    public ArrayList<String> loadArray() {
        Set<String> mySet = sharedPreferences.getStringSet("fav_" + place, null);
        if (mySet != null)
            return new ArrayList<>(mySet);
        else return new ArrayList<>();
    }
}
