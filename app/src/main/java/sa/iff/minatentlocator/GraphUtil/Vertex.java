package sa.iff.minatentlocator.GraphUtil;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mukhtar on 9/12/2015.
 */
public class Vertex {

    private LatLng coordinates;

    public Vertex(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }
}
