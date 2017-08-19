package sa.iff.minatentlocator.GraphUtil;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Mukhtar on 9/12/2015.
 */
public class DjikstraEngineConcurrent{

    private List<Vertex> nodes;
    private List<Edge> edges;
    private ConcurrentHashMap<Vertex, Boolean> nodesVisitStatus;
    private ConcurrentHashMap<Vertex, Vertex> predecessors;
    private ConcurrentHashMap<Vertex, Double> distance;

    public DjikstraEngineConcurrent(Graph graph) {
        this.nodes = graph.getVertexes();
        this.edges = graph.getEdges();
    }

    public class FindMinimalDistances implements Runnable {

        private Vertex node;

        public FindMinimalDistances(Vertex node) {
            this.node = node;
        }

        @Override
        public void run() {
            List<Vertex> neighbours = getNeighbours(node);
            Iterator<Vertex> iter = neighbours.iterator();
            Vertex target;
            while (iter.hasNext()) {
                target = iter.next();
                if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                    distance.put(target, getShortestDistance(node) + getDistance(node, target));
                    predecessors.put(target, node);
                    nodesVisitStatus.put(target, false);
                }
            }
            Log.d("THREAD_TASK", "Thread Task for Node " + node.toString() + " done!");
        }
    }

    public void executeConcurrent(Vertex source, ThreadPoolExecutor executor) {
        nodesVisitStatus = new ConcurrentHashMap<>();
        distance = new ConcurrentHashMap<>();
        predecessors = new ConcurrentHashMap<>();
        distance.put(source, 0.00);
        nodesVisitStatus.put(source, false);
        int counter=0;
        while (counter < nodes.size()*0.9) {
            while (nodesVisitStatus.containsValue(false)) {
                Vertex node = getMinimum();
                nodesVisitStatus.replace(node, true);
                FindMinimalDistances task = new FindMinimalDistances(node);
                executor.execute(task);
                counter++;
            }
        }
        executor.shutdown();
    }


    /*private synchronized void findMinimalDistances(Vertex node) {
        List<Vertex> neighbours = getNeighbours(node);
        Iterator<Vertex> iter = neighbours.iterator();
        Vertex target;
        while (iter.hasNext()) {
            target = iter.next();
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                predecessors.put(target, node);
                nodesVisitStatus.put(target, false);
            }
        }
    }*/

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

    private Vertex getMinimum() {
        Vertex minimum = null, temp;
        for (Vertex unVisitedNode : nodesVisitStatus.keySet()) {
            if (!nodesVisitStatus.get(unVisitedNode)){
                temp = unVisitedNode;
                if (minimum == null)
                    minimum = temp;
                else if (getShortestDistance(minimum) > getShortestDistance(temp))
                    minimum = temp;
            }
        }
        return minimum;
    }

    private boolean isSettled(Vertex destination) {
        if (nodesVisitStatus.contains(destination))
            return nodesVisitStatus.get(destination);
        else
            return false;
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
        this.predecessors = new ConcurrentHashMap<>(predecessors);
    }
}
