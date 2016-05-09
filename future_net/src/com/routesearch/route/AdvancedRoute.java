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

	/* parameters used for ant colony optimization */
	// original amount of trail
	private static double c = 1.0;
	// constant of evaporation
	private static double evaporation = 0.5;
	// pheromone importance constant
	private static double alpha = 1;
	// city distance importance constant
	private static double beta = 5;
	// pheromone quantity constant
	private static double Q = 500;
	// probability of pure random selection of the next town
	private static double pr = 0.1;
	// number of vertex(for this problem, n is the number of elements in the
	// specifiedSet).
	private static int n;
	// number of ants
	private static int m;

	private static double antNumFactor = 5;
	private static int maxIteration = 500;
	private static int iteration = 0;
	private static double trails[][];
	private static Ant[] ants = null;
	private static int currentIndex = 0;
	private static Random random = new Random();

	private static List<Integer> shortestPath;
	private static int shortestPathLength = 0;

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
		antColony();
		// System.out.println(s + "-->" + t + shortestPath);
		StringBuffer sb = new StringBuffer();
		// StringBuffer sb2 = new StringBuffer();

		for (int i = 0; i < shortestPath.size() - 1; i++) {
			sb.append(edgeIDs[shortestPath.get(i)][shortestPath.get(i + 1)] + "|");
		}

		System.out.println(shortestPathLength + ": " + sb.deleteCharAt(sb.length() - 1).toString());
		// System.out.println(sb2.toString());
		// System.out.println("Shortest path length is: " + shortestPathLength);
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
		List<Integer> path = new LinkedList<Integer>();
		path.add(s);
		getShortestPath2(s, t, path);
		// path.add(t);
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

	/**
	 * Get the shortest path from s to t using ant colony algorithm.
	 * 
	 */
	public static void antColony() {
		// Initialization
		n = specifiedSet.size();
		m = (int) (n * antNumFactor);
		System.out.println("n:" + n + " m:" + m);
		ants = new Ant[m];
		shortestPath = new LinkedList<Integer>();
		trails = new double[n][n];

		// for(int a = 0; a< n;a++){
		// for (int b = 0; b < n; b++) {
		// System.out.print(distances[specifiedSet.get(a)][specifiedSet.get(b)]+",
		// ");
		// }
		// System.out.println();
		// }

		for (int i = 0; i < n; i++) {
			int realI = specifiedSet.get(i);
			for (int j = 0; j < n; j++) {
				if (i == j) {
					trails[i][j] = 0;
				} else if (distances[realI][specifiedSet.get(j)] != INFINITE) {
					trails[i][j] = c;
				} else {
					trails[i][j] = 0;
				}
			}
		}
		iteration = 0;
		while (iteration < maxIteration) {
			setupAnts();
			moveAnts();
			updateTrails();
			updateShortest();
			iteration++;
		}
	}

	public static class Ant {
		public int tour[];
		public boolean visited[];
		public double probs[];
		public int tourLength;

		public Ant() {
			tour = new int[n];
			visited = new boolean[n];
			probs = new double[n];
			tourLength = 0;
		}

		public void visitVertex(int vertex) {
			tour[currentIndex + 1] = vertex;
			visited[vertex] = true;
		}

		public void calTourLength() {
			tourLength = distances[s][specifiedSet.get(tour[0])];
			int last = distances[specifiedSet.get(tour[n - 1])][t];
			if (tourLength == INFINITE || last == INFINITE) {
				tourLength = -1;
			}
			for (int i = 0; i < n - 1; i++) {
				tourLength += distances[specifiedSet.get(tour[i])][specifiedSet.get(tour[i + 1])];
			}
			tourLength += last;
		}
	}

	/**
	 * Set each ants with a random start city.
	 */
	private static void setupAnts() {
		currentIndex = -1;
		for (int i = 0; i < m; i++) {
			ants[i] = new Ant();
			ants[i].visitVertex(random.nextInt(n));
		}
		currentIndex++;
	}

	/**
	 * Move ants to next vertex.
	 */
	private static void moveAnts() {
		while (currentIndex < n - 1) {
			List<Integer> toBeRemoved = new LinkedList<Integer>();
			int i = 0;

			for (Ant a : ants) {
				if (a != null) {
					int next = selectNextVertex(a);
					if (next < 0) {
						toBeRemoved.add(i);
					} else {
						a.visitVertex(next);
					}
				}
				i++;
			}
			for (int j : toBeRemoved) {
				ants[j] = null;
			}
			toBeRemoved.clear();
			currentIndex++;
		}
	}

	/**
	 * Select next vertex for the given ant a to move to.
	 * 
	 * @param a
	 * @return next vertex; -1 if cannot find such a next vertex.
	 */
	private static int selectNextVertex(Ant a) {
		if (random.nextDouble() < pr) {
			int t = random.nextInt(n - currentIndex);
			int j = -1;
			int realCur = specifiedSet.get(a.tour[currentIndex]);
			for (int i = 0; i < n; i++) {
				int dis = distances[realCur][specifiedSet.get(i)];
				if (!a.visited[i] && dis != INFINITE && dis > 0) {
					j++;
				}
				if (j == t) {
					return i;
				}
			}
		}

		probTo(a);
		double r = random.nextDouble();
		double total = 0;
		for (int k = 0; k < n; k++) {
			total += a.probs[k];
			if (total >= r) {
				return k;
			}
		}
		return -1;
	}

	/**
	 * Calculate the probability of the given ant moving to each town and stores
	 * it in to the probs array.
	 * 
	 * @param ant
	 */
	private static void probTo(Ant ant) {
		int i = ant.tour[currentIndex];
		double denom = 0.0;
		double[] numerators = new double[n];
		int realI = specifiedSet.get(i);

		for (int k = 0; k < n; k++) {
			int realK = specifiedSet.get(k);
			if (!ant.visited[k] && distances[realI][realK] != INFINITE) {
				numerators[k] = Math.pow(trails[i][k], alpha) * Math.pow(1.0 / distances[realI][realK], beta);
				denom += numerators[k];
			}
		}

		for (int j = 0; j < n; j++) {
			if (numerators[j] > 0) {
				ant.probs[j] = numerators[j] / denom;
			} else {
				ant.probs[j] = 0.0;
			}
		}
	}

	/**
	 * Update the trails information.
	 */
	private static void updateTrails() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				trails[i][j] *= evaporation;
			}
		}

		for (Ant ant : ants) {
			if (ant != null) {
				ant.calTourLength();
				if (ant.tourLength <= 0) {
					continue;
				}

				double contribution = Q / ant.tourLength;
				for (int i = 0; i < n - 1; i++) {
					trails[ant.tour[i]][ant.tour[i + 1]] += contribution;
				}
			}
		}
	}

	private static void updateShortest() {
		for (Ant ant : ants) {
			if (ant != null) {
				if ((shortestPathLength == 0 && ant.tourLength > 0)
						|| (shortestPathLength > 0 && ant.tourLength < shortestPathLength)) {
					updateShortest(ant);
				}

			}
		}
	}

	private static void updateShortest(Ant ant) {
		shortestPath.clear();
		int[] tour = ant.tour;

		// System.out.println(iteration + ": " + ant.tourLength + ":");
		// for (int i = 0; i < tour.length; i++) {
		// System.out.print(specifiedSet.get(tour[i]) + ", ");
		// }
		// System.out.println();

		shortestPath.addAll(getShortestPath(s, specifiedSet.get(tour[0])));
		int len = tour.length - 1;
		for (int i = 0; i < len; i++) {
			shortestPath.addAll(getShortestPath(specifiedSet.get(tour[i]), specifiedSet.get(tour[i + 1])));
		}
		shortestPath.addAll(getShortestPath(specifiedSet.get(tour[n - 1]), t));
		shortestPath.add(t);
		shortestPathLength = ant.tourLength;
	}
}