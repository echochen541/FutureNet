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
		/** a map from vertices' IDs to vertices' indices*/
		Map<Integer, Integer> vertexID2Index = new HashMap<Integer, Integer>();
	
		/** adjacent matrices of edges' IDs and weights*/
		int n = 0;
		int[][] edgeIDs = new int[n][n];
		int[][] edgeWeights = new int[n][n];

		/** neighbors of each vertex*/
		List[] neighbors = new ArrayList[n];
		
		
		
		return "hello world!";
	}
	
	
}