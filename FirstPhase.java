import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.*;

import java.util.*;


public class FirstPhase {	
	int numVertices;	
	int numIterations;
	int numStrategies;	
	HashSet<Edge> edgeTable;
	HashSet<Integer>[] adjacencyList;
	Hashtable<Edge, Double> edgeWeightTable;
	Hashtable<Edge, Double> edgeStrengthTable;
	Hashtable<Edge, Double> edgeProbTable;
	NumberFormat df ;	
	Graph g;
	double threshold;
	double threshold1;	
	
	public FirstPhase (Graph g, int numIterations) throws IOException {
		this.g = g ; 
		this.numVertices = g.getNumVertices();				
		this.numIterations = numIterations;
		this.edgeProbTable = new Hashtable<Edge, Double>();
		this.adjacencyList = g.getAdjacencyList();
		this.edgeTable = g.getEdgeTable();
		this.edgeStrengthTable = g.getEdgeStrengthTable(); 
		this.numStrategies = 2;
				
		this.df = DecimalFormat.getInstance();
		this.df.setMinimumFractionDigits(2);
		this.df.setMaximumFractionDigits(4);
		this.df.setRoundingMode(RoundingMode.DOWN);		
	}	
	
	// Initial Strategy Assignment
	public  int[] setRandomStrategies () throws IOException{		
		int[] s = new int [numVertices+1];
		for (int i = 1; i <= numVertices; i++) {
			s[i] = (int) Math.floor(Math.random() * numStrategies);		
		}				
		return s;
	}
	
	public void initializeEdgeProbabilities() {
		for (Edge edge: edgeTable) {
			edgeProbTable.put(edge, (double) 0);
			edgeProbTable.put(new Edge(edge.v, edge.u), (double)0);
		}
	}
	
	public void computeEdgeProbabilitiesFinal() throws IOException {				
		FileWriter fw = new FileWriter("probnash.txt");
		BufferedWriter probOut = new BufferedWriter(fw);		
		for (Edge edge: edgeTable) {
			double value = edgeProbTable.get(edge)/numIterations;
			edgeProbTable.put(edge, value);
			edgeProbTable.put(new Edge(edge.v, edge.u), value);
			probOut.write("("+edge.u+","+edge.v+")\t"+ df.format(edgeProbTable.get(edge))+"\n");
		}
		probOut.close();		
	}	
	
	public void run() throws Exception {
		System.out.println("EdgeTable size:"+edgeTable.size());
		System.out.println("EdgeStrengthTable size:"+edgeStrengthTable.size());
		
		initializeEdgeProbabilities();
		System.out.println("EdgeProbTable size:"+edgeProbTable.size());
		int roundnum = 1;
		for (int iter = 1; iter <= numIterations; iter++) {
			int[] s = setRandomStrategies();
			boolean isChanged = true;
			roundnum = 1;
			while (isChanged) {
				isChanged = false;
				int count = 1;
				for (int i = iter; i <= numVertices; i = (i % numVertices) + 1) {
					if (count++ > numVertices) {
						break;
					}
					double utility = 0, sum = 0;
					for (int j : g.getAdjacencyList(i)) {
						if (s[i] == s[j]) {
							utility = utility + edgeStrengthTable.get(new Edge(i,j));
						}
						sum = sum + edgeStrengthTable.get(new Edge(i,j));
					}
					if (utility/sum < 0.5) {
						isChanged = true;
						s[i] = (1 + s[i]) % 2;
					}
					roundnum++;
				}
			}
			for (Edge edge: edgeTable) {
				if (s[edge.u] == s[edge.v]){
					double value = edgeProbTable.get(edge) + 1;
					edgeProbTable.put(edge, value);
					edgeProbTable.put(new Edge(edge.v, edge.u), value);
				}			
			}
			// System.out.println("Number of rounds:"+roundnum);
		} // end of for numIterations
		//************************************************************************
		computeEdgeProbabilitiesFinal();
		System.out.println("Number of rounds:"+roundnum);
		g.setEdgeProbTable(edgeProbTable);
		SecondPhase sp = new SecondPhase(g);
		sp.run();
	}
}
