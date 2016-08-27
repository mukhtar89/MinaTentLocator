package sa.iff.minatentlocator.GraphUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mukhtar on 9/12/2015.
 */
public class Graph {

    private final List<Vertex> vertexes;
    private final List<Edge> edges;

    public Graph(List<Vertex> vertexes, List<Edge> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
