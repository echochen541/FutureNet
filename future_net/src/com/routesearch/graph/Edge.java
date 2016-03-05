package com.routesearch.graph;

/**
 * @author EchoChen
 *
 */
public class Edge {
	private int edgeID;
	private int sourceID;
	private int destinationID;
	private int weight;
	
	public Edge(int edgeID, int sourceID, int destinationID, int weight) {
		this.edgeID = edgeID;
		this.sourceID = sourceID;
		this.destinationID = destinationID;
		this.weight = weight;
	}
	
	/** Compare two edges on weights */
	public int comparaTo(Edge e) {
		if (weight > e.getWeight())
			return 1;
		else if (weight == e.getWeight())
			return 0;
		else
			return -1;
	}	
	
	public int getEdgeID() {
		return edgeID;
	}

	public void setEdgeID(int edgeID) {
		this.edgeID = edgeID;
	}

	public int getSourceID() {
		return sourceID;
	}

	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}

	public int getDestinationID() {
		return destinationID;
	}

	public void setDestinationID(int destinationID) {
		this.destinationID = destinationID;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
