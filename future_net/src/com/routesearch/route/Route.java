/**
 * 实现代码文件
 * 
 * @author echochen
 * @since 2016-4-14
 * @version V1
 */
package com.routesearch.route;

import java.util.*;

public final class Route {
	private static Graph g = new Graph();

	// conditions
	private static int s = 0;
	private static int t = 0;
	private static List<Integer> specifiedSet = new ArrayList<Integer>();
	private static List<Integer> specifiedSet2 = new ArrayList<Integer>();

	public static String[] searchRoute(String[] graphContent, String[] conditionContent) {
		// Step 1: construct the weighted directed graph
		g.constructGraph(graphContent);

		// Step 2: extract s, t and specifiedSet
		extractCondition(conditionContent);

		// Step 3: remove edges entering s and leaving t
		g.removeEdges(s, t);

		// Step 4: floydWarshall
		g.floydWarshall();

		// Step 5: ACO todo by yangjiacheng
		ArrayList<String> path1 = new ArrayList<String>();
		ArrayList<String> path2 = new ArrayList<String>();
		AntColony ac1 = new AntColony(g, s, t, specifiedSet);
		ac1.init();
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < AntColony.shortestPath.size() - 1; i++) {
			sb1.append(g.edgeIDs[AntColony.shortestPath.get(i)][AntColony.shortestPath.get(i + 1)] + "|");
			path1.add(g.edgeIDs[AntColony.shortestPath.get(i)][AntColony.shortestPath.get(i + 1)] + "");
		}
		System.out.println(AntColony.shortestPathLength + ": " + sb1.deleteCharAt(sb1.length() - 1).toString());

		ac1 = new AntColony(g, s, t, specifiedSet2);
		ac1.init();
		StringBuffer sb2 = new StringBuffer();
		for (int i = 0; i < AntColony.shortestPath.size() - 1; i++) {
			sb2.append(g.edgeIDs[AntColony.shortestPath.get(i)][AntColony.shortestPath.get(i + 1)] + "|");
			path2.add(g.edgeIDs[AntColony.shortestPath.get(i)][AntColony.shortestPath.get(i + 1)] + "");
		}
		System.out.println(AntColony.shortestPathLength + ": " + sb2.deleteCharAt(sb2.length() - 1).toString());
		specifiedSet.retainAll(path1);
		specifiedSet2.retainAll(path2);
		System.out.println(specifiedSet);
		System.out.println(specifiedSet2);
		String[] result = new String[2];
		result[0] = sb1.toString();
		result[1] = sb2.toString();
		// System.out.println(result[0]);
		// System.out.println(result[1]);
		return result;
	}

	/** extract s, t and specifiedSet */
	public static void extractCondition(String[] conditionContent) {
		String[] demand0 = conditionContent[0].split(",|\\|");
		String[] demand1 = conditionContent[1].split(",|\\|");
		s = Integer.parseInt(demand0[1]);
		t = Integer.parseInt(demand0[2]);

		for (int i = 3; i < demand0.length; i++) {
			specifiedSet.add(Integer.parseInt(demand0[i]));
		}

		for (int i = 3; i < demand1.length; i++) {
			specifiedSet2.add(Integer.parseInt(demand1[i]));
		}
	}

	private static boolean isValid(ArrayList<Integer> shortestPath) {
		for (int i = 0; i < shortestPath.size() - 1; i++) {
			for (int j = i + 1; j < shortestPath.size(); j++) {
				if (shortestPath.get(i) == (shortestPath.get(j))) {
					System.out.println(i + " " + j + " " + shortestPath.get(i) + " " + shortestPath.get(j));
					System.out.println("invalid:" + shortestPath);
					return false;
				}
			}
		}
		return true;
	}
}