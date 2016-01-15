


import java.util.HashSet;
import java.util.Hashtable;



public class Graph {
	
	HashSet <Integer>[] adjacencyList;
	HashSet <Edge> edgeTable;
	Hashtable <Edge,Double> edgeWeightTable;
	Hashtable <Edge,Double> edgeStrengthTable;
	Hashtable <Edge,Double> edgeProbTable;
	int numVertices;
	
	public void setNumVertices(int numVertices) {
		this.numVertices = numVertices;
	}
	public int getNumVertices() {
		return numVertices;
	}
	public void setAdjacencyList(HashSet<Integer>[] adjacencyList) {
		this.adjacencyList = adjacencyList;
	}
	
	public void setEdgeTable(HashSet<Edge> edgeTable) {
		this.edgeTable = edgeTable;
	}
	
	public void setEdgeWeightTable(Hashtable<Edge,Double> edgeWeightTable) {
		this.edgeWeightTable = edgeWeightTable;		
	}
	
	public void setEdgeStrengthTable(Hashtable<Edge,Double> edgeStrengthTable) {
		this.edgeStrengthTable = edgeStrengthTable;		
	}
	
	public void setEdgeProbTable(Hashtable<Edge,Double> edgeProbTable) {
		this.edgeProbTable = edgeProbTable;		
	}
	
	public HashSet<Integer>[] getAdjacencyList() {
		return adjacencyList;
	}
	
	public Hashtable<Edge,Double> getEdgeWeightTable() {
		return edgeWeightTable;
	}
	
	public Hashtable<Edge,Double> getEdgeStrengthTable() {
		return edgeStrengthTable;
	}
	
	public Hashtable<Edge,Double> getEdgeProbTable() {
		return edgeProbTable;
	}
	
	
	public HashSet<Edge> getEdgeTable() {
		return edgeTable;
	}
	
	public HashSet<Integer> getAdjacencyList(int v) {
		return adjacencyList[v];
	}

	
}
