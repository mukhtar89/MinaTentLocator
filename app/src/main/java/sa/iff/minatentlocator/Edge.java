package sa.iff.minatentlocator;

/**
 * Created by Mukhtar on 9/12/2015.
 */
public class Edge {

    private Vertex source, destination;
    private Double distance;

    public Edge(Vertex source, Vertex destination) {
        this.source = source;
        this.destination = destination;
        this.distance = SphericalUtil.computeDistanceBetween(this.source.getCoordinates(), this.destination.getCoordinates());
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Double getDistance() {
        return distance;
    }
}
