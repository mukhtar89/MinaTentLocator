package sa.iff.minatentlocator.GraphUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mukht on 8/27/2016.
 */
public class FavouritePredecessors {

    private List<Vertex> nodes;
    private Map<Vertex, Vertex> predecessors;

    public Map<Vertex, Vertex> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(Map<Vertex, Vertex> predecessors) {
        this.predecessors = predecessors;
    }

    public List<Vertex> getNodes() {
        return nodes;
    }

    public void setNodes(List<Vertex> nodes) {
        this.nodes = nodes;
    }
}
