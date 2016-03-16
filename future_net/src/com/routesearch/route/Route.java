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
		System.out.println(minCost);

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
		List<Integer> neighbor = neighbors.get(s);

		for (Integer i : neighbor) {
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
						 System.out.println("minPath is " + minPath);
						 System.out.println("minCost is " + minCost);
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

	private static void optimizePath(List<Integer> path) {
		int pre = -1, post = -1, preIndex = -1, weight = 0;
		List<List<Integer>> optimizedSubPaths = new

		LinkedList<List<Integer>>();
		List<Integer> prevs = new ArrayList<Integer>();
		List<Integer> posts = new ArrayList<Integer>();

		// Find the opportunity of optimization in the minPath.
		// pre and post is the start and end vertex of a optimization
		// opportunity, respectively.
		int i = 0;
		int presentV = -1, prePresentV = -1;
		for (Iterator<Integer> it = path.iterator();

		it.hasNext();) {
			presentV = it.next();
			if (pre == -1 && includingSet.contains(presentV)) {
				// determine the start vertex
				pre = presentV;
				preIndex = i;
			} else if (pre != -1 && includingSet.contains(presentV) && preIndex == (i - 1)) {
				// change the start point to thisvertex
				pre = presentV;
				preIndex = i;
			} else if (pre != -1 && includingSet.contains(presentV) && preIndex != (i - 1)) {
				// determine the end vertex
				post = presentV;
				weight += edgeWeights[prePresentV][presentV];
			} else if (pre != -1) {
				weight += edgeWeights[prePresentV][presentV];
			}

			if (pre != -1 && post != -1) {
				// Optimize the sub-path
				List<Integer> sub = optimizeSubPath(pre, post, weight);
				if (sub != null) {
					optimizedSubPaths.add(sub);
					prevs.add(pre);
					posts.add(post);
				}

				pre = post;
				preIndex = i;
				post = -1;
				weight = 0;
			}

			prePresentV = presentV;
			i++;
		}

		// Substitute with the optimized sub path.
		int size = optimizedSubPaths.size();
		for (int j = 0; j < size; j++) {
			List<Integer> subPath = optimizedSubPaths.get(j);
			int end = posts.get(j);
			int startIndex = path.indexOf(prevs.get(j)) + 1;
			int tmp = path.get(startIndex);

			// Remove old sub path.
			while (tmp != end) {
				path.remove(startIndex);
			}

			// Add new sub path.
			for (Iterator<Integer> it = subPath.iterator(); it.hasNext();) {
				path.add(startIndex, it.next());
			}
		}
	}

	/**
	 * Used by optimizePath method. Based on Dijkstra algorithm.
	 */
	private static List<Integer> optimizeSubPath(int start, int end, int maxWeight) {
		int prevVertex[] = new int[600];
		int distance[] = new int[600];
		boolean[] isVisited = new boolean[600];
		List<Integer> aboutToVisit = new LinkedList<Integer>();

		for (int i = 0; i < 600; i++) {
			distance[i] = Integer.MAX_VALUE;
		}
		prevVertex[end] = -1;

		aboutToVisit.add(start);
		distance[start] = 0;
		int min = Integer.MAX_VALUE; // Minimum distance at present.
		int minVertex = start; // Vertex that has the minimum distance at
								// present.
		while (!aboutToVisit.isEmpty()) {
			// Take out the first vertex of the list.
			aboutToVisit.remove((Object) minVertex);
			int prev = minVertex;
			int prevDistance = distance[prev];
			List<Integer> neighb = neighbors.get(prev);

			// Expand the neighbors to the about to visit list.
			for (Iterator<Integer> iterator = neighb.iterator(); iterator.hasNext();) {
				int tmpV = iterator.next();
				int tmpDis = prevDistance + edgeWeights[prev][tmpV];
				if (tmpDis >= maxWeight) {
					continue;
				}
				if (tmpV == end) {
					distance[tmpV] = tmpDis;
					isVisited[tmpV] = true;
					prevVertex[tmpV] = prev;
					continue;
				}

				distance[tmpV] = tmpDis;
				prevVertex[tmpV] = prev;
				if (!isVisited[tmpV]) {
					isVisited[tmpV] = true;
					aboutToVisit.add(tmpV);
				}
			}

			// Find the vertex in the list that has the minimum distance.
			min = Integer.MAX_VALUE;
			for (Iterator<Integer> it =

					aboutToVisit.iterator(); it.hasNext();) {
				int tmp = it.next();
				if (distance[tmp] < min) {
					minVertex = tmp;
				}
			}
		}

		List<Integer> path = new LinkedList<Integer>();
		int prev = prevVertex[end];
		if (prev == -1) {
			return null;
		}
		do {
			path.add(prev);
			prev = prevVertex[prev];
		} while (prev != start);

		return path;
	}
}
