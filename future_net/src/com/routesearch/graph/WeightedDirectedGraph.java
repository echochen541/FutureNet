package com.routesearch.graph;

import java.util.*;

/**
 * @author EchoChen
 *
 */
public class WeightedDirectedGraph {
	private List<Vertex> vertices =  new ArrayList<Vertex>();
	private List<Edge> edges = new ArrayList<Edge>();
	private Map<Integer, Integer> vertexIDToIndex = new HashMap<Integer, Integer>();
	private int[][] weightedAdjacencyMatrix;
	
	public WeightedDirectedGraph(List<Vertex> vertices, List<Edge> edges) {
		this.vertices = vertices;
		this.edges = edges;
		constructVertexIDToIndex();
		constructWeightedAdjacencyMatrix();
	}

	private void constructWeightedAdjacencyMatrix() {
		// TODO Auto-generated method stub
		
	}

	private void constructVertexIDToIndex() {
		// TODO Auto-generated method stub
		
	}
}
