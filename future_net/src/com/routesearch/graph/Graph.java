package com.routesearch.graph;

import java.util.*;

/**
 * @author EchoChen
 *
 */
public interface Graph {
	public List<Vertex> getVertices();
	public List<Edge> getEdges();
	
	public Vertex getVertex(int index);
	public int getVertexIndex(Vertex v);

	public Edge getEdge(int index);
	public int getEdgeIndex(Edge e);
	
	public List<Integer> getNeighbors(int index);
	public int[][] getAdjacencyMatrix();
}
