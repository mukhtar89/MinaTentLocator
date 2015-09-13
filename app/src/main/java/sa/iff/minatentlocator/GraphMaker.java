package sa.iff.minatentlocator;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class GraphMaker {

	private static Graph graph;
	private static List<Vertex> nodes;
	private static List<Edge> edges = new ArrayList<>();
	private static Hashtable<LatLng, Vertex> nodeMapping = new Hashtable<>();
	
	public static void add (LatLng point, LatLng neighbour) {
		Vertex tempPoint = null, tempNeighbour = null;
		if (point != null) {
			if (!nodeMapping.containsKey(point)) {
				tempPoint = new Vertex(point);
				nodeMapping.put(point, tempPoint);
			} else tempPoint = nodeMapping.get(point);
		}
		if (neighbour != null) {
			if (!nodeMapping.containsKey(neighbour)) {
				tempNeighbour = new Vertex(neighbour);
				nodeMapping.put(neighbour, tempNeighbour);
			} else tempNeighbour = nodeMapping.get(neighbour);
		}
		if (point != null && neighbour != null)
			edges.add(new Edge(tempPoint, tempNeighbour));
	}

	public static Graph makeGraph() {
		nodes = new ArrayList<>(nodeMapping.values());
		graph = new Graph(nodes, edges);
		return graph;
	}

	public static Vertex getNode(LatLng node) {
		return nodeMapping.get(node);
	}

}
