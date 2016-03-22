/**
 * 实现代码文件
 * 
 * @author echochen
 * @since 2016-3-4
 * @version V1
 */
package com.routesearch.route;

import java.util.*;

import org.gnu.glpk.*;

import com.filetool.util.FileUtil;

/** cost represents the cost from source to v */
class CostV implements Comparable<CostV> {
	int v;
	double cost;

	public CostV(int v, double cost) {
		this.v = v;
		this.cost = cost;
	}

	@Override
	public int compareTo(CostV cv) {
		if (this.cost < cv.cost)
			return -1;
		else if (this.cost == cv.cost)
			return 0;
		else
			return (int) (this.cost - cv.cost);
	}
}

public final class Route {
	// attributes of graph
	private static Map<Integer, Integer> vertexID2Index = new HashMap<Integer, Integer>();
	private static List<List<Integer>> neighbors = new ArrayList<List<Integer>>();
	private static int[][] edgeIDs = new int[600][600];
	private static int[][] edgeWeights = new int[600][600];
	private static int numOfEdges = 0;

	// conditions
	private static int sourceIndex = 0;
	private static int destinationIndex = 0;
	private static List<Integer> includingSet = new ArrayList<Integer>();
	private static List<Integer> includingSet2 = new ArrayList<Integer>();

	// information of search
	private static boolean[] visited;
	private static List<Integer> minPath = new ArrayList<Integer>();
	private static int minCost = Integer.MAX_VALUE;
	private static String resultFilePath;

	// path of model and data
	private static String fname;
	private static String fdata;

	public static String searchRoute(String graphContent, String condition,
			String filePath) {
		// Step 1: Construct the weighted directed graph
		String[] lines = graphContent.split("\\n");
		int index = -1;
		for (int i = 0; i < lines.length; i++) {
			String[] line = lines[i].split(",");
			int edgeID = Integer.parseInt(line[0]);
			int sID = Integer.parseInt(line[1]);
			int dID = Integer.parseInt(line[2]);
			int weight = Integer.parseInt(line[3]);

			// remove self-loop
			if (sID == dID) {
				if (!vertexID2Index.containsKey(sID)) {
					index++;
					neighbors.add(new ArrayList<Integer>());
					vertexID2Index.put(sID, index);
				}
			} else {
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
					numOfEdges++;
				}
				if (edgeWeights[sIndex][dIndex] != 0
						&& edgeWeights[sIndex][dIndex] > weight) {
					edgeIDs[sIndex][dIndex] = edgeID;
					edgeWeights[sIndex][dIndex] = weight;
				}
			}
		}

		// Step 2: Extract s, d and includingSet
		String[] demand = condition.split(",|\\n");
		sourceIndex = vertexID2Index.get(Integer.parseInt(demand[0]));
		destinationIndex = vertexID2Index.get(Integer.parseInt(demand[1]));

		String[] V = (demand[2]).split("\\|");
		for (String v : V) {
			includingSet.add(vertexID2Index.get(Integer.parseInt(v)));
			includingSet2.add(vertexID2Index.get(Integer.parseInt(v)));
		}

		// Firstly, write NA to result.csv
		resultFilePath = filePath;
		FileUtil.write(resultFilePath, "NA", false);

		// if the graph is small, call dsfSearch
		int n = neighbors.size();
		if (n <= 40 && includingSet.size() <= 6) {
			// visited[i] represents the vertex i has been visited or not
			visited = new boolean[n];

			// Step 3: Search
			List<Integer> path = new ArrayList<Integer>();
			System.out.println(sourceIndex + "," + includingSet + ","
					+ destinationIndex);
			dfsSearchPath(sourceIndex, destinationIndex, path, 0);
			System.out.println(sourceIndex + "," + minPath + ","
					+ destinationIndex);
			// Step 4: form result
			if (minPath.size() != 0) {
				System.out.print(minCost + ":");
				return formResult();
			}
			System.out.println("NA");
			return "NA";
		}

		// if graph is large call glpk solver
		fname = "mod/ktsp.mod";
		fdata = "mod/data.dat";

		// write data.bat
		String text = "data;\n\n";
		FileUtil.write(fdata, text, false);

		text = "param n := " + n + ";\n";
		FileUtil.write(fdata, text, true);

		text = "param orig := " + sourceIndex + ";\n";
		FileUtil.write(fdata, text, true);

		text = "param dest := " + destinationIndex + ";\n";
		FileUtil.write(fdata, text, true);

		text = "set P :=";
		for (int v : includingSet) {
			text += (" " + v);
		}
		text += ";\n";
		FileUtil.write(fdata, text, true);

		text = "param : A : c :=\n";
		FileUtil.write(fdata, text, true);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (edgeWeights[i][j] > 0) {
					text = i + " " + j + " " + edgeWeights[i][j] + "\n";
					FileUtil.write(fdata, text, true);
				}
			}
		}

		text = ";\n\nend;\n";
		FileUtil.write(fdata, text, true);

		// Step3: call glpk solver
		return mipSolver();
	}

	private static String mipSolver() {
		String result = "NA";
		glp_prob lp;
		glp_tran tran;
		glp_iocp iocp;
		int skip = 0;
		int ret;

		try {
			// create problem
			lp = GLPK.glp_create_prob();
			// allocate workspace
			tran = GLPK.glp_mpl_alloc_wksp();
			// read model
			ret = GLPK.glp_mpl_read_model(tran, fname, skip);
			// read data
			ret = GLPK.glp_mpl_read_data(tran, fdata);
			// generate model
			ret = GLPK.glp_mpl_generate(tran, null);
			// build model
			GLPK.glp_mpl_build_prob(tran, lp);

			// solve model
			iocp = new glp_iocp();
			GLPK.glp_init_iocp(iocp);
			iocp.setPresolve(1);
			ret = GLPK.glp_intopt(lp, iocp);

			// retrieve result
			if (ret == 0) {
				result = write_mip_solution(lp);
			}

			// free memory
			GLPK.glp_mpl_free_wksp(tran);
			GLPK.glp_delete_prob(lp);

		} catch (org.gnu.glpk.GlpkException e) {
			System.err.println("An error inside the GLPK library occured.");
			System.err.println(e.getMessage());
		} catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	private static String write_mip_solution(glp_prob lp) {
		int i;
		int n;
		String name;
		double val;
		double cost;
		List<CostV> cvs = new ArrayList<CostV>();

		name = GLPK.glp_get_obj_name(lp);
		val = GLPK.glp_mip_obj_val(lp);
		System.out.print(name);
		System.out.print(" = ");
		System.out.println(val);
		cost = val;
		n = GLPK.glp_get_num_cols(lp);
		for (i = numOfEdges + 1; i <= n; i++) {
			name = GLPK.glp_get_col_name(lp, i);
			val = GLPK.glp_mip_col_val(lp, i);
			// System.out.println(name + "=" + val);
			if (val > 0 && val <= cost + 1) {
				int v = Integer.parseInt(name.substring(name.indexOf("[") + 1,
						name.indexOf("]")));
				cvs.add(new CostV(v, val));
			}
		}

		Collections.sort(cvs);
		StringBuffer resultSb = new StringBuffer();
		int pre = sourceIndex;
		for (CostV cv : cvs) {
			if (edgeWeights[pre][cv.v] > 0) {
				resultSb.append(edgeIDs[pre][cv.v] + "|");
			}
			pre = cv.v;
		}

		System.out.println(resultSb.deleteCharAt(resultSb.length() - 1)
				.toString());
		return resultSb.toString();
	}

	// dfs search
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
				if (includingSet.size() == 0) {
					cost += weight;
					if (cost < minCost) {
						minCost = cost;
						minPath = new ArrayList<Integer>(path);
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

	// form result
	private static String formResult() {
		StringBuffer resultSb = new StringBuffer();
		int pre = sourceIndex;
		for (Integer i : minPath) {
			resultSb.append(edgeIDs[pre][i] + "|");
			pre = i;
		}
		resultSb.append(edgeIDs[pre][destinationIndex]);
		System.out.println(resultSb.toString());
		return resultSb.toString();
	}

	@SuppressWarnings("unused")
	private static void optimizePath(List<Integer> path) {
		int pre = -1, post = -1, preIndex = -1, weight = 0;
		List<List<Integer>> optimizedSubPaths = new LinkedList<List<Integer>>();
		List<Integer> prevs = new ArrayList<Integer>();
		List<Integer> posts = new ArrayList<Integer>();

		// Find the opportunity of optimization in the minPath.
		// pre and post is the start and end vertex of a optimization
		// opportunity, respectively.
		int i = 0;
		int presentV = -1, prePresentV = -1;
		for (Iterator<Integer> it = path.iterator(); it.hasNext();) {
			presentV = it.next();
			if (pre == -1 && includingSet2.contains(presentV)) {
				// determine the start vertex
				pre = presentV;
				preIndex = i;
			} else if (pre != -1 && includingSet2.contains(presentV)
					&& preIndex == (i - 1)) {
				// change the start point to this vertex
				pre = presentV;
				preIndex = i;
			} else if (pre != -1 && includingSet2.contains(presentV)
					&& preIndex != (i - 1)) {
				// determine the end vertex
				post = presentV;
				weight += edgeWeights[prePresentV][presentV];
			} else if (pre != -1) {
				weight += edgeWeights[prePresentV][presentV];
			}

			if (pre != -1 && post != -1) {
				// Optimize the sub-path
				System.out.println(pre + "," + post + "," + weight);
				List<Integer> sub = optimizeSubPath(pre, post, weight);
				System.out.println(sub);
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

		for (List<Integer> subPath : optimizedSubPaths) {
			System.out.println(subPath);
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
				path.remove(startIndex++);
				tmp = path.get(startIndex);
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
	private static List<Integer> optimizeSubPath(int start, int end,
			int maxWeight) {
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
			for (Iterator<Integer> iterator = neighb.iterator(); iterator
					.hasNext();) {
				int tmpV = iterator.next();
				int tmpDis = prevDistance + edgeWeights[prev][tmpV];
				if (tmpDis >= maxWeight || tmpDis >= distance[tmpV]) {
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
			for (Iterator<Integer> it = aboutToVisit.iterator(); it.hasNext();) {
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
			path.add(0, prev);
			prev = prevVertex[prev];
		} while (prev != start);

		return path;
	}
}