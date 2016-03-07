package com.routesearch.graph;

import java.util.*;

/**
 * @author EchoChen
 *
 */
public class WeightedDirectedGraph {
	/** map of vertice's to indices,thus each vertex is represented by a index */
	private Map<Integer, Integer> vertexID2Index = new HashMap<Integer, Integer>();
	/**
	 * neighbors[i] stores the neighbors of the vertex indexed as i, and i's
	 * neighbors are indices of the vertices
	 */
	private List<List<Integer>> neighbors = new ArrayList<List<Integer>>();
	/**
	 * if there exists a edge from the vertex indexed as i to the vertex indexed
	 * as j, edgeIDs[i][j] stores the edge's ID
	 */
	private int[][] edgeIDs = new int[600][600];
	/**
	 * if there exists a edge from the vertex indexed as i to the vertex indexed
	 * as j, edgeIDs[i][j] stores the edge's weight
	 */
	private int[][] edgeWeights = new int[600][600];

	public WeightedDirectedGraph(Map<Integer, Integer> vertexID2Index,
			List<List<Integer>> neighbors, int[][] edgeIDs, int[][] edgeWeights) {
		this.vertexID2Index = vertexID2Index;
		this.neighbors = neighbors;
		this.edgeIDs = edgeIDs;
		this.edgeWeights = edgeWeights;
	}
}
