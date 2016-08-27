package sa.iff.minatentlocator.GraphUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mukhtar on 9/12/2015.
 */
public class DjikstraEngine {

    private List<Vertex> nodes;
    private List<Edge> edges;
    private Set<Vertex> visitedNodes, unVisitedNodes;
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Double> distance;

    public DjikstraEngine(Graph graph) {
        this.nodes = graph.getVertexes();
        this.edges = graph.getEdges();
    }

    public void execute(Vertex source) {
        visitedNodes = new HashSet<>();
        unVisitedNodes = new HashSet<>();
        distance = new HashMap<>();
        predecessors = new HashMap<>();
        distance.put(source, 0.00);
        unVisitedNodes.add(source);
        while (unVisitedNodes.size() > 0) {
            Vertex node = getMinimum(unVisitedNodes);
            visitedNodes.add(node);
            unVisitedNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(Vertex node) {
        List<Vertex> neighbours = getNeighbours(node);
        Iterator<Vertex> iter = neighbours.iterator();
        Vertex target;
        while (iter.hasNext()) {
            target = iter.next();
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                predecessors.put(target, node);
                unVisitedNodes.add(target);
            }
        }
    }

    private Double getDistance(Vertex node, Vertex target) {
        for (Edge edge :  edges) {
            if ((edge.getSource() == node && edge.getDestination() == target) || (edge.getSource() == target && edge.getDestination() == node))
                return edge.getDistance();
        }
        return Double.MAX_VALUE;
    }

    private List<Vertex> getNeighbours(Vertex node) {
        List<Vertex> neighbours = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource() == node && !isSettled(edge.getDestination()))
                neighbours.add(edge.getDestination());
            else if (edge.getDestination() == node && !isSettled(edge.getSource()))
                neighbours.add(edge.getSource());
        }
        return neighbours;
    }

    private Vertex getMinimum(Set<Vertex> unVisitedNodes) {
        Vertex minimum = null, temp;
        for (Vertex unVisitedNode : unVisitedNodes) {
            temp = unVisitedNode;
            if (minimum == null)
                minimum = temp;
            else if (getShortestDistance(minimum) > getShortestDistance(temp))
                minimum = temp;
        }
        return minimum;
    }

    private boolean isSettled(Vertex destination) {
        return visitedNodes.contains(destination);
    }

    private Double getShortestDistance(Vertex node) {
        Double dist = distance.get(node);
        if (dist == null)
            return Double.MAX_VALUE;
        else
            return dist;
    }

    public ArrayList<Vertex> getPath(Vertex target) {
        ArrayList<Vertex> path = new ArrayList<>();
        Vertex step = target;
       if (!predecessors.containsKey(step))
           return null;
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        Collections.reverse(path);
        return path;
    }

    public List<Vertex> getNodes () {
        return nodes;
    }

    public void setNodes(List<Vertex> nodes) { this.nodes = nodes;}

    public Map<Vertex, Vertex> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(Map<Vertex, Vertex> predecessors) {
        this.predecessors = predecessors;
    }
}
