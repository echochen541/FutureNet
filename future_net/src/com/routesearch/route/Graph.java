package com.routesearch.route;

import java.util.LinkedList;
import java.util.List;

public class Graph {
	// attributes of max graph
	public int MAX_VERTICES = 2000;
	public int MAX_WEIGHT = 100;
	public int INFINITE = MAX_VERTICES * MAX_WEIGHT;

	// information of graph
	public int[][] edgeIDs = new int[MAX_VERTICES][MAX_VERTICES];
	public int[][] edgeWeights = new int[MAX_VERTICES][MAX_VERTICES];
	// distances[i][j] means the cost of the shortest path from i to j
	public int[][] distances = new int[MAX_VERTICES][MAX_VERTICES];
	// paths[i][j] means the intermediate node in the shortest path from i to j
	public int[][] paths = new int[MAX_VERTICES][MAX_VERTICES];
	public int numOfVertices = 0;
	public int numOfEdges = 0;

	public void constructGraph(String[] graphContent) {
		for (int i = 0; i < MAX_VERTICES; i++) {
			for (int j = 0; j < MAX_VERTICES; j++) {
				edgeWeights[i][j] = INFINITE;
				distances[i][j] = INFINITE;
				if (i == j) {
					edgeWeights[i][j] = 0;
					distances[i][j] = 0;
				}
				paths[i][j] = -1;
			}
		}

		String[] lines = graphContent;
		for (int i = 0; i < lines.length; i++) {
			String[] line = lines[i].split(",");
			int edgeID = Integer.parseInt(line[0]);
			int sID = Integer.parseInt(line[1]);
			int dID = Integer.parseInt(line[2]);
			int weight = Integer.parseInt(line[3]);

			// remove self-loop
			if (sID != dID) {
				numOfVertices = Math.max(Math.max(numOfVertices, sID + 1), dID + 1);
				if (edgeWeights[sID][dID] == INFINITE) {
					edgeIDs[sID][dID] = edgeID;
					edgeWeights[sID][dID] = weight;
					distances[sID][dID] = weight;
					numOfEdges++;
				}
				// if edge of a larger weight exists in sID and dID, update
				if (edgeWeights[sID][dID] != INFINITE && edgeWeights[sID][dID] > weight) {
					edgeIDs[sID][dID] = edgeID;
					edgeWeights[sID][dID] = weight;
					distances[sID][dID] = weight;
				}
			}
		}
	}

	/** remove edges entering s and leaving t */
	public void removeEdges(int s, int t) {
		for (int i = 0; i < numOfVertices; i++) {
			if (i != s && edgeWeights[i][s] != INFINITE) {
				edgeWeights[i][s] = INFINITE;
				distances[i][s] = INFINITE;
				numOfEdges--;
			}
			if (i != t && edgeWeights[t][i] != INFINITE) {
				edgeWeights[t][i] = INFINITE;
				distances[t][i] = INFINITE;
				numOfEdges--;
			}
		}
	}

	/** floydWarshall to compute shortest paths between each pair (i,j) */
	public void floydWarshall() {
		for (int k = 0; k < numOfVertices; k++) {
			for (int i = 0; i < numOfVertices; i++) {
				for (int j = 0; j < numOfVertices; j++) {
					if (distances[i][k] != INFINITE && distances[k][j] != INFINITE
							&& distances[i][j] > distances[i][k] + distances[k][j]) {
						distances[i][j] = distances[i][k] + distances[k][j];
						paths[i][j] = k;
					}
				}
			}
		}
	}

	/** get the shortest path from i to j */
	public List<Integer> getShortestPath(int s, int t) {
		List<Integer> path = new LinkedList<Integer>();
		path.add(s);
		getShortestPath2(s, t, path);
		// path.add(t);
		return path;
	}

	/** helper method of getShortestPath */
	public void getShortestPath2(int i, int j, List<Integer> path) {
		int k = paths[i][j];
		if (k == -1) {
			// System.out.println("no edge between " + i + " and " + j);
			return;
		} else {
			getShortestPath2(i, k, path);
			path.add(k);
			getShortestPath2(k, j, path);
		}
	}
}
