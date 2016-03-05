/**
 * 实现代码文件
 * 
 * @author XXX
 * @since 2016-3-4
 * @version V1.0
 */
package com.routesearch.route;

import java.util.*;

import com.routesearch.graph.Edge;
import com.routesearch.graph.Vertex;
import com.routesearch.graph.WeightedDirectedGraph;

public final class Route {
	/**
	 * 你需要完成功能的入口
	 * 
	 * @author XXX
	 * @since 2016-3-4
	 * @version V1
	 */
	public static String searchRoute(String graphContent, String condition) {
		List<Vertex> vertices =  new ArrayList<Vertex>();
		List<Edge> edges = new ArrayList<Edge>();
		constructVerticesAndEges(vertices,edges,graphContent);
		
		WeightedDirectedGraph wdg = new WeightedDirectedGraph(vertices, edges);
		
		
		return "hello world!";
	}

	private static void constructVerticesAndEges(List<Vertex> vertices, List<Edge> edges, String graphContent) {
		// TODO Auto-generated method stub
		
	}

}