/**
 * 实现代码文件
 * 
 * @author XXX
 * @since 2016-3-4
 * @version V1.0
 */
package com.routesearch.route;

import java.util.*;

public final class Route {
	/**
	 * 你需要完成功能的入口
	 * 
	 * @author echochen
	 * @since 2016-3-4
	 * @version V1
	 */
	private static Map<Integer, Integer> vertexID2Index = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> index2VertexID = new HashMap<Integer, Integer>();
	private static List<List<Integer>> neighbors = new ArrayList<List<Integer>>();
	private static int[][] edgeIDs = new int[600][600];
	private static int[][] edgeWeights = new int[600][600];
	private static int sourceIndex = 0;
	private static int destinationIndex = 0;
	private static List<Integer> includingSet = new ArrayList<Integer>();
	private static boolean[] visited;
	private static List<Integer> minPath = new ArrayList<Integer>();
	private static int minCost = Integer.MAX_VALUE;

	public static String searchRoute(String graphContent, String condition) {
		String resultStr = "hello world!";
		String[] lines = graphContent.split("\\n");
		int index = -1;
		for (int i = 0; i < lines.length; i++) {
			String[] line = lines[i].split(",");
			int edgeID = Integer.parseInt(line[0]);
			int sID = Integer.parseInt(line[1]);
			int dID = Integer.parseInt(line[2]);
			int weight = Integer.parseInt(line[3]);

			if (!vertexID2Index.containsKey(sID)) {
				index++;
				neighbors.add(new ArrayList<Integer>());
				vertexID2Index.put(sID, index);
				index2VertexID.put(index, sID);
			}
			if (!vertexID2Index.containsKey(dID)) {
				index++;
				neighbors.add(new ArrayList<Integer>());
				vertexID2Index.put(dID, index);
				index2VertexID.put(index, dID);
			}

			int sIndex = vertexID2Index.get(sID);
			int dIndex = vertexID2Index.get(dID);

			if (edgeWeights[sIndex][dIndex] == 0) {
				neighbors.get(sIndex).add(dIndex);
				edgeIDs[sIndex][dIndex] = edgeID;
				edgeWeights[sIndex][dIndex] = weight;
			}

			if (edgeWeights[sIndex][dIndex] != 0 && edgeWeights[sIndex][dIndex] > weight) {
				edgeIDs[sIndex][dIndex] = edgeID;
				edgeWeights[sIndex][dIndex] = weight;
			}
		}
		String[] demand = condition.split(",|\\n");
		sourceIndex = vertexID2Index.get(Integer.parseInt(demand[0]));
		destinationIndex = vertexID2Index.get(Integer.parseInt(demand[1]));
		String[] V = (demand[2]).split("\\|");

		for (String v : V) {
			includingSet.add(vertexID2Index.get(Integer.parseInt(v)));
		}

		int n = neighbors.size();
		/** print the neighbors of each vertex */
		for (int i = 0; i < n; i++) {
			System.out.print("vertex ID is " + index2VertexID.get(i) + ",vertex index is " + i + ":");
			System.out.println(neighbors.get(i));
		}

		/** print the matrix of edges' IDs */
		System.out.println("matrix of edges' IDs");
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(edgeIDs[i][j] + "  ");
			}
			System.out.println();
		}

		/** print the matrix of edges' weights */
		System.out.println("matrix of edges' weights");
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(edgeWeights[i][j] + "  ");
			}
			System.out.println();
		}

		/** print s, d and includingSet */
		System.out.println(sourceIndex);
		System.out.println(destinationIndex);
		System.out.println(includingSet);

		/**
		 * visited[i] == false represents the vertex i hasn't been visited, vice
		 * versa
		 */
		visited = new boolean[n];

		List<Integer> path = new ArrayList<Integer>();		
		searchPath(sourceIndex, destinationIndex, path, 0);
		return resultStr;
	}

	private static void searchPath(int s, int d, List<Integer> path, int cost) {
		visited[s] = true;
		boolean removed = false;
		for (Integer i : neighbors.get(s)) {
			int weight = edgeWeights[s][i];
			// System.out.println("from " + s + " to " + i);
			if (weight == 0 || visited[i]) {
				// System.out.println("no edge or " + i + " has been visited!");
				continue;
			}
			if (i == d) {
				// System.out.println("Reach " + d);
				System.out.println(path +"," +includingSet);
				if (includingSet.size() == 0) {
					cost += weight;
//					System.out.println("Find a path: " + path + " cost: " + cost + " minCost: " + minCost);
					if (cost < minCost) {
						minCost = cost;
						minPath = new ArrayList<Integer>(path);
//						System.out.println("hahaha minPath is" + minPath);
					}
				}
				continue;
			}
			path.add(i);
			// System.out.println("add " + i + " to path, path now is " + path);
			if (includingSet.contains(i)) {
				includingSet.remove(i);
				removed = true;
				// System.out.println("remove " + i + " from includingSet,
				// includingSet now is " + includingSet);
			}
			// System.out.println("Search " + i + " to " + d + " cost is " +
			// (cost + weight));
			searchPath(i, d, path, cost + weight);
			path.remove(i);
			visited[i] = false;
			if (removed) {
				includingSet.add(i);
				// System.out.println("add " + i + " to includingSet,
				// includingSet now is " + includingSet);
			}
		}
	}
}