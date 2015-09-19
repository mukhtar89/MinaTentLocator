package sa.iff.minatentlocator;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import sa.iff.minatentlocator.GraphUtil.Edge;
import sa.iff.minatentlocator.GraphUtil.Graph;
import sa.iff.minatentlocator.GraphUtil.Vertex;
import sa.iff.minatentlocator.MapUtils.SphericalUtil;
import sa.iff.minatentlocator.ProtoBufUtil.EdgProto;
import sa.iff.minatentlocator.ProtoBufUtil.VtxProto;


public class GraphMaker {

	private static Graph graph;
	public static List<Vertex> nodes;
	public static List<Edge> edges = new ArrayList<>();
	public static Hashtable<LatLng, Vertex> nodeMapping = new Hashtable<>();
	public static Hashtable<Vertex[], Edge> edgeMapping = new Hashtable<>();

	public static List<Vertex> readNodes;
	public static List<Edge> readEdges = new ArrayList<>();
	public static Hashtable<LatLng, Vertex> readNodeMapping = new Hashtable<>();

	private static File path;
	
	public static synchronized void add (LatLng point, LatLng neighbour) {
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
			if (!edgeMapping.containsKey(new Vertex[]{tempPoint, tempNeighbour}))
				edges.add(new Edge(tempPoint, tempNeighbour));
	}

	public static Graph makeGraph(Context context) {
		nodes = new ArrayList<>(nodeMapping.values());
		path = context.getExternalFilesDir(null);
		try {
			graph = readGraph();
			//saveGraph();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//graph = new Graph(nodes, edges);
		return graph;
	}

	public static Vertex getNode(LatLng node) {
		return nodeMapping.get(node);
	}

	public static Vertex getNearestNode(LatLng node) {
		Double distance = Double.MAX_VALUE;
		Vertex nearest = null;
		for (Vertex vertex : readNodes) {
			if (distance > SphericalUtil.computeDistanceBetween(vertex.getCoordinates(), node)) {
				distance = SphericalUtil.computeDistanceBetween(vertex.getCoordinates(), node);
				nearest = vertex;
			}
		}
		return nearest;
	}

	public static void saveGraph() throws IOException {
		File fileVertex = new File(path, "vertexes_final.ser");
		FileOutputStream streamVertex = new FileOutputStream(fileVertex);
		streamVertex.flush();
		Integer offsetVertex;
		for (Vertex vtx : nodes) {
			VtxProto.Vtx node = VtxProto.Vtx.newBuilder()
					.setLat(vtx.getCoordinates().latitude)
					.setLng(vtx.getCoordinates().longitude)
					.build();
			offsetVertex = node.getSerializedSize();
			streamVertex.write(offsetVertex.byteValue());
			streamVertex.write(node.toByteArray());
		}
		for (Vertex vtx : readNodes) {
			if (!nodeMapping.containsKey(vtx.getCoordinates())) {
				VtxProto.Vtx node = VtxProto.Vtx.newBuilder()
						.setLat(vtx.getCoordinates().latitude)
						.setLng(vtx.getCoordinates().longitude)
						.build();
				offsetVertex = node.getSerializedSize();
				streamVertex.write(offsetVertex.byteValue());
				streamVertex.write(node.toByteArray());
			}
		}
		streamVertex.close();

		File fileEdge = new File(path, "edges_final.ser");
		FileOutputStream streamEdge = new FileOutputStream(fileEdge);
		Integer offsetEdge;
		for (Edge edge : edges) {
			EdgProto.Edg.Vtx source = EdgProto.Edg.Vtx.newBuilder()
					.setLat(edge.getSource().getCoordinates().latitude)
					.setLng(edge.getSource().getCoordinates().longitude)
					.build();
			EdgProto.Edg.Vtx destination = EdgProto.Edg.Vtx.newBuilder()
					.setLat(edge.getDestination().getCoordinates().latitude)
					.setLng(edge.getDestination().getCoordinates().longitude)
					.build();
			EdgProto.Edg edg = EdgProto.Edg.newBuilder()
					.setSource(source)
					.setDestination(destination)
					.setDistance(edge.getDistance())
					.build();
			offsetEdge = edg.getSerializedSize();
			streamEdge.write(offsetEdge.byteValue());
			streamEdge.write(edg.toByteArray());
		}
		for (Edge edge : readEdges) {
			if (!edgeMapping.containsKey(new Vertex[]{edge.getSource(), edge.getDestination()})
					&&  !edgeMapping.containsKey(new Vertex[]{edge.getDestination(), edge.getSource()})) {
				EdgProto.Edg.Vtx source = EdgProto.Edg.Vtx.newBuilder()
						.setLat(edge.getSource().getCoordinates().latitude)
						.setLng(edge.getSource().getCoordinates().longitude)
						.build();
				EdgProto.Edg.Vtx destination = EdgProto.Edg.Vtx.newBuilder()
						.setLat(edge.getDestination().getCoordinates().latitude)
						.setLng(edge.getDestination().getCoordinates().longitude)
						.build();
				EdgProto.Edg edg = EdgProto.Edg.newBuilder()
						.setSource(source)
						.setDestination(destination)
						.setDistance(edge.getDistance())
						.build();
				offsetEdge = edg.getSerializedSize();
				streamEdge.write(offsetEdge.byteValue());
				streamEdge.write(edg.toByteArray());
			}
		}
		streamEdge.close();
	}

	public static Graph readGraph() throws IOException {
		File fileVertex = new File(path, "vertexes.ser");
		FileInputStream streamVertex = new FileInputStream(fileVertex);
		Integer fileVertexSize = (int) fileVertex.length();
		byte[] BufferVertex = new byte[fileVertexSize];
		Integer offsetVertex = 0, currentVertex = 0;
		streamVertex.read(BufferVertex);
		while (currentVertex < fileVertexSize){
			Byte length = BufferVertex[currentVertex];
			offsetVertex = length.intValue();
			currentVertex += 1;
			ByteBuffer tempBuffer = ByteBuffer.allocate(offsetVertex);
			tempBuffer.put(BufferVertex, currentVertex, offsetVertex);
			VtxProto.Vtx node = VtxProto.Vtx.parseFrom(tempBuffer.array());
			LatLng ltemp = new LatLng(node.getLat(), node.getLng());
			Vertex lvertex = new Vertex(ltemp);
			readNodeMapping.put(ltemp, lvertex);
			currentVertex += offsetVertex;
		}
		streamVertex.close();

		File fileEdge = new File(path, "edges.ser");
		FileInputStream streamEdge = new FileInputStream(fileEdge);
		Integer offsetEdge = 0, currentEdge = 0;
		Integer fileEdgeSize = (int) fileEdge.length();
		byte[] BufferEdge = new byte[fileEdgeSize];
		streamEdge.read(BufferEdge);
		while (currentEdge < fileEdgeSize){
			Byte length = BufferEdge[currentEdge];
			offsetEdge = length.intValue();
			currentEdge += 1;
			ByteBuffer tempBuffer = ByteBuffer.allocate(offsetEdge);
			tempBuffer.put(BufferEdge, currentEdge, offsetEdge);
			EdgProto.Edg edg = EdgProto.Edg.parseFrom(tempBuffer.array());
			EdgProto.Edg.Vtx source = edg.getSource();
			LatLng stemp = new LatLng(source.getLat(), source.getLng());
			Vertex svertex = readNodeMapping.get(stemp);
			EdgProto.Edg.Vtx destination = edg.getDestination();
			LatLng dtemp = new LatLng(destination.getLat(), destination.getLng());
			Vertex dvertex = readNodeMapping.get(dtemp);
			Edge edge = new Edge(svertex, dvertex);
			readEdges.add(edge);
			currentEdge += offsetEdge;
		}
		streamEdge.close();
		readNodes = new ArrayList<>(readNodeMapping.values());

		return new Graph(readNodes, readEdges);
	}
}
