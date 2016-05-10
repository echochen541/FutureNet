package com.routesearch.route;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AntColony {
	public static Graph g;

	// conditions
	public static int s = 0;
	public static int t = 0;
	public static List<Integer> specifiedSet = new ArrayList<Integer>();

	/* parameters used for ant colony optimization */
	// original amount of trail
	public double c = 1.0;
	// constant of evaporation
	public static double evaporation = 0.5;
	// pheromone importance constant
	public static double alpha = 1;
	// city distance importance constant
	public static double beta = 5;
	// pheromone quantity constant
	public static double Q = 500;
	// probability of pure random selection of the next town
	public static double pr = 0.1;
	// number of vertex(for this problem, n is the number of elements in the
	// specifiedSet).
	public static int n;
	// number of ants
	public static int m;
	public static int[][] mVertices;

	public double antNumFactor = 5;
	public int maxIteration = 500;
	public int iteration = 0;
	public static double trails[][];
	public static Ant[] ants = null;
	public static int currentIndex = 0;
	public static Random random = new Random();

	public static ArrayList<ArrayList<Integer>> shortestPaths;
	public static ArrayList<Integer> shortestPath;
	public static int shortestPathLength = 0;

	public AntColony(Graph g, int s, int t, List<Integer> specifiedSet) {
		AntColony.g = g;
		AntColony.s = s;
		AntColony.t = t;
		AntColony.specifiedSet = specifiedSet;
		c = 1.0;
		evaporation = 0.5;
		alpha = 1;
		beta = 5;
		Q = 500;
		pr = 0.1;
		n = specifiedSet.size();
		m = (int) (n * antNumFactor);
		antNumFactor = 5;
		maxIteration = 500;
		iteration = 0;
		trails = new double[n][n];
		ants = new Ant[m];
		mVertices = new int[m][g.numOfVertices];
		currentIndex = 0;
		random = new Random();
		shortestPaths = new ArrayList<ArrayList<Integer>>();
		shortestPath = new ArrayList<Integer>();
		shortestPathLength = 0;
	}

	public void init() {
		for (int i = 0; i < n; i++) {
			int realI = specifiedSet.get(i);
			for (int j = 0; j < n; j++) {
				if (i == j) {
					trails[i][j] = 0;
				} else if (g.distances[realI][specifiedSet.get(j)] != g.INFINITE) {
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
			tourLength = g.distances[s][specifiedSet.get(tour[0])];
			int last = g.distances[specifiedSet.get(tour[n - 1])][t];
			if (tourLength == g.INFINITE || last == g.INFINITE) {
				tourLength = -1;
			}
			for (int i = 0; i < n - 1; i++) {
				tourLength += g.distances[specifiedSet.get(tour[i])][specifiedSet.get(tour[i + 1])];
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
			int next = random.nextInt(n);
			ants[i].visitVertex(next);
			mVertices[i][n] = 1;
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
				int dis = g.distances[realCur][specifiedSet.get(i)];
				if (!a.visited[i] && dis != g.INFINITE && dis > 0) {
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
			if (!ant.visited[k] && g.distances[realI][realK] != g.INFINITE) {
				numerators[k] = Math.pow(trails[i][k], alpha) * Math.pow(1.0 / g.distances[realI][realK], beta);
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

		shortestPath.addAll(g.getShortestPath(s, specifiedSet.get(tour[0])));

		int len = tour.length - 1;
		for (int i = 0; i < len; i++) {
			shortestPath.addAll(g.getShortestPath(specifiedSet.get(tour[i]), specifiedSet.get(tour[i + 1])));
		}
		shortestPath.addAll(g.getShortestPath(specifiedSet.get(tour[n - 1]), t));
		shortestPath.add(t);
		shortestPathLength = ant.tourLength;

		ArrayList<Integer> temp = new ArrayList<Integer>(shortestPath);
		shortestPaths.add(temp);
	}
}
