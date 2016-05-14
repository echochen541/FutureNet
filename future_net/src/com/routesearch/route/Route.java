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
		AntColony ac = new AntColony(g, s, t, specifiedSet);
		List<Integer> path1 = new ArrayList<Integer>();
		List<Integer> path2 = new ArrayList<Integer>();

		if (specifiedSet.size() == 0) {
			path1 = g.getShortestPath(s, t);
			path1.add(t);
		} else {
			ac = new AntColony(g, s, t, specifiedSet);
			ac.init();
			path1 = AntColony.shortestPath;
		}
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < path1.size() - 1; i++) {
			sb1.append(g.edgeIDs[path1.get(i)][path1.get(i + 1)] + "|");
		}

		if (specifiedSet2.size() == 0) {
			path2 = g.getShortestPath(s, t);
			path2.add(t);
		} else {
			ac = new AntColony(g, s, t, specifiedSet2);
			ac.init();
			path2 = AntColony.shortestPath;
		}
		StringBuffer sb2 = new StringBuffer();
		for (int i = 0; i < path2.size() - 1; i++) {
			sb2.append(g.edgeIDs[path2.get(i)][path2.get(i + 1)] + "|");
		}

		path1.retainAll(path2);
		System.out.println(path1);
		
		String[] result = new String[2];
		result[0] = sb1.deleteCharAt(sb1.length() - 1).toString();
		result[1] = sb2.deleteCharAt(sb2.length() - 1).toString();
		System.out.println(result[0]);
		System.out.println(result[1]);
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