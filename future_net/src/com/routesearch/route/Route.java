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
	public static String searchRoute(String graphContent, String condition) {
		String resultStr = "hello world!";

		/** map of vertice's to indices,thus each vertex is represented by index */
		Map<Integer, Integer> vertexID2Index = new HashMap<Integer, Integer>();
		/**
		 * neighbors[i] stores the neighbors of the vertex indexed as i, and i's
		 * neighbors are indices of the vertices
		 */
		List<List<Integer>> neighbors = new ArrayList<List<Integer>>();
		/**
		 * if there exists a edge from the vertex indexed as i to the vertex
		 * indexed as j, edgeIDs[i][j] store the edge's ID
		 */
		int[][] edgeIDs = new int[600][600];
		/**
		 * if there exists a edge from the vertex indexed as i to the vertex
		 * indexed as j, edgeIDs[i][j] store the edge's weight
		 */
		int[][] edgeWeights = new int[600][600];
		/** IncludingSet stores the indices of the vertices in V' */
		List<Integer> IncludingSet = new ArrayList<Integer>();
		int sourceIndex = 0, destinationIndex = 0;

		String[] lines = graphContent.split("\\n");
		int index = -1;
		for (int i = 0; i < lines.length; i++) {
			String[] line = lines[i].split(",");
			int edgeID = Integer.parseInt(line[0]);
			int sID = Integer.parseInt(line[1]);
			int dID = Integer.parseInt(line[2]);
			int weight = Integer.parseInt(line[3]);

			if (!vertexID2Index.containsKey(sID)) {
				neighbors.add(new ArrayList<Integer>());
				vertexID2Index.put(sID, ++index);
			}
			if (!vertexID2Index.containsKey(dID)) {
				neighbors.add(new ArrayList<Integer>());
				vertexID2Index.put(dID, ++index);
			}

			int sIndex = vertexID2Index.get(sID);
			int dIndex = vertexID2Index.get(dID);

			if (edgeWeights[sIndex][dIndex] == 0
					|| edgeWeights[sIndex][dIndex] < weight) {
				neighbors.get(sIndex).add(dIndex);
				edgeIDs[sIndex][dIndex] = edgeID;
				edgeWeights[sIndex][dIndex] = weight;
			}
		}

		String[] demand = condition.split(",|\\n");
		sourceIndex = vertexID2Index.get(Integer.parseInt(demand[0]));
		destinationIndex = vertexID2Index.get(Integer.parseInt(demand[1]));
		String[] V = (demand[2]).split("\\|");

		for (String v : V) {
			IncludingSet.add(vertexID2Index.get(Integer.parseInt(v)));
		}

		int n = neighbors.size();
		/** print the neighbors of each vertex */
		for (int i = 0; i < n; i++) {
			System.out.println(neighbors.get(i));
		}

		/** print the matrix of edges' IDs */
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(edgeIDs[i][j] + "  ");
			}
			System.out.println();
		}

		/** print the matrix of edges' weights */
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(edgeWeights[i][j] + "  ");
			}
			System.out.println();
		}

		/** print s, d and includingSet*/
		System.out.println(sourceIndex);
		System.out.println(destinationIndex);
		System.out.println(IncludingSet);
		
		return resultStr;
	}
}