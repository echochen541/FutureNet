/**
 * 实现代码文件
 * 
 * @author echochen
 * @since 2016-3-4
 * @version V1
 */
package com.routesearch.route;

import java.util.*;

import com.filetool.util.FileUtil;

public final class Route {
	// attributes of graph
	private static Map<Integer, Integer> vertexID2Index = new HashMap<Integer, Integer>();
	private static List<List<Integer>> neighbors = new ArrayList<List<Integer>>();
	private static int[][] edgeIDs = new int[600][600];
	private static int[][] edgeWeights = new int[600][600];

	// conditions
	private static int sourceIndex = 0;
	private static int destinationIndex = 0;
	private static List<Integer> includingSet = new ArrayList<Integer>();

	// information of search
	private static boolean[] visited;
	private static List<Integer> minPath = new ArrayList<Integer>();
	private static int minCost = Integer.MAX_VALUE;

	public static String searchRoute(String graphContent, String condition, String resultFilePath) {
		StringBuffer resultSb = new StringBuffer();

		// Step 1: Construct the weighted directed graph
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
			}
			if (!vertexID2Index.containsKey(dID)) {
				index++;
				neighbors.add(new ArrayList<Integer>());
				vertexID2Index.put(dID, index);
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

		// Step 2: Extract s, d and includingSet
		String[] demand = condition.split(",|\\n");
		sourceIndex = vertexID2Index.get(Integer.parseInt(demand[0]));
		destinationIndex = vertexID2Index.get(Integer.parseInt(demand[1]));

		String[] V = (demand[2]).split("\\|");
		for (String v : V) {
			includingSet.add(vertexID2Index.get(Integer.parseInt(v)));
		}

		// visited[i] represents the vertex i has been visited or not
		int n = neighbors.size();
		visited = new boolean[n];

		// Step 3: Search
		FileUtil.write(resultFilePath, "NA", false);
		List<Integer> path = new ArrayList<Integer>();
		System.out.println(sourceIndex + "," + includingSet + "," + destinationIndex);
		dfsSearchPath(sourceIndex, destinationIndex, path, 0);
		System.out.println(sourceIndex + "," + minPath + "," + destinationIndex);

		// Step 4: form result
		int pre = sourceIndex;
		if (minPath.size() != 0) {
			for (Integer i : minPath) {
				resultSb.append(edgeIDs[pre][i] + "|");
				pre = i;
			}
			resultSb.append(edgeIDs[pre][destinationIndex]);
			System.out.println(resultSb.toString());
			return resultSb.toString();
		}
		System.out.println("NA");
		return "NA";
	}

	private static void dfsSearchPath(int s, int d, List<Integer> path, int cost) {
		visited[s] = true;
		boolean removed = false;
		for (Integer i : neighbors.get(s)) {
			int weight = edgeWeights[s][i];
			if (weight == 0 || visited[i]) {
				continue;
			}

			// pruning
			if (cost + weight >= minCost)
				continue;

			if (i == d) {
				// System.out.println(path);
				if (includingSet.size() == 0) {
					cost += weight;
					if (cost < minCost) {
						minCost = cost;
						minPath = new ArrayList<Integer>(path);
						// System.out.println("minPath is " + minPath);
						// System.out.println("minCost is " + minCost);
						// optimize path
					}
				}
				continue;
			}

			path.add(i);
			removed = includingSet.remove(i);
			dfsSearchPath(i, d, path, cost + weight);
			path.remove(i);
			visited[i] = false;
			if (removed) {
				includingSet.add(i);
			}
		}
	}
}
