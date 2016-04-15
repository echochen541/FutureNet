/**
 * 实现代码文件
 * 
 * @author echochen
 * @since 2016-4-14
 * @version V1
 */
package com.routesearch.route;

import java.util.*;

public final class AdvancedRoute {
	// attributes of max graph
	private static int MAX_VERTICES = 2000;
	private static int MAX_WEIGHT = 100;
	private static int INFINITE = MAX_VERTICES * MAX_WEIGHT;

	// information of graph
	private static int[][] edgeIDs = new int[MAX_VERTICES][MAX_VERTICES];
	private static int[][] edgeWeights = new int[MAX_VERTICES][MAX_VERTICES];
	// distances[i][j] means the cost of the shortest path from i to j
	private static int[][] distances = new int[MAX_VERTICES][MAX_VERTICES];
	// paths[i][j] means the intermediate node in the shortest path from i to j
	private static int[][] paths = new int[MAX_VERTICES][MAX_VERTICES];
	private static int numOfVertices = 0;
	private static int numOfEdges = 0;

	// conditions
	private static int s = 0;
	private static int t = 0;
	private static List<Integer> specifiedSet = new ArrayList<Integer>();
	// private static List<Integer> specifiedSet2 = new ArrayList<Integer>();

	public static void searchRoute(String graphContent, String condition) {
		// Step 1: construct the weighted directed graph
		constructGraph(graphContent);

		// Step 2: extract s, t and specifiedSet
		extractCondition(condition);

		// Step 3: remove edges entering s and leaving t
		removeEdges();

		// Step 4: floydWarshall
		floydWarshall();

		// Test
		for (int i = 0; i < numOfVertices; i++) {
			for (int j = 0; j < numOfVertices; j++) {
				if (distances[i][j] != INFINITE) {
					// System.out.println("cost = " + distances[i][j]);
					// get the shortest path from i to j
					// List<Integer> path = getShortestPath(i, j);
					// System.out.println(i + "-->" + j);
					// System.out.println(path);
				}
			}
		}

		// Step 5: ACO todo by yangjiacheng
		List<Integer> path = getShortestPath(s, t);
		System.out.println(distances[s][t]);
		System.out.println(s + "-->" + t + path);
		return;
	}

	/** construct the weighted directed graph */
	public static void constructGraph(String graphContent) {
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

		String[] lines = graphContent.split("\\n");
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

	/** extract s, t and specifiedSet */
	public static void extractCondition(String condition) {
		String[] demand = condition.split(",|\\n");
		s = Integer.parseInt(demand[0]);
		t = Integer.parseInt(demand[1]);
		String[] V = (demand[2]).split("\\|");
		for (String v : V) {
			specifiedSet.add(Integer.parseInt(v));
		}
	}

	/** remove edges entering s and leaving t */
	public static void removeEdges() {
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
	public static void floydWarshall() {
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
	public static List<Integer> getShortestPath(int s, int t) {
		List<Integer> path = new ArrayList<Integer>();
		path.add(s);
		getShortestPath2(s, t, path);
		path.add(t);
		return path;
	}

	/** helper method of getShortestPath */
	public static void getShortestPath2(int i, int j, List<Integer> path) {
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