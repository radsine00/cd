import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class ConstructGraph {
	HashSet<Edge> edgeTable = new HashSet<Edge> ();
	Hashtable<Edge, Double> edgeWeightTable = new Hashtable<Edge, Double> ();
	Hashtable<Edge, Double> edgeStrengthTable = new Hashtable<Edge, Double>();
	int numVertices;	
	HashSet<Integer>[] adjacencyList;	
	int[] degree;	
	String graphDataFile;
	Graph g;	
	NumberFormat df;

	public ConstructGraph (String graphDataFile, int numVertices) throws Exception{
		this.graphDataFile = graphDataFile;
		this.numVertices = numVertices;			
		this.edgeTable.clear();
		this.g = new Graph();
		g.setNumVertices(numVertices);
		this.df = DecimalFormat.getInstance();
		this.df.setMinimumFractionDigits(2);
		this.df.setMaximumFractionDigits(4);
		this.df.setRoundingMode(RoundingMode.DOWN);		
	}

	@SuppressWarnings("unchecked")
	public Graph constructGraph() throws IOException {		
		System.out.println("\nConstructing the Graph..");
		adjacencyList = (HashSet<Integer>[]) new HashSet [numVertices+1];
		degree = new int [numVertices+1];
		for (int i = 1; i <= numVertices; i++) {			
			degree[i] = 0;
			adjacencyList[i] = new HashSet <Integer>();			
		}
		File f = new File (graphDataFile);
		BufferedReader reader = new BufferedReader (new FileReader(f));
		while (reader.ready()) {				
			String[]  s = reader.readLine().split("\t");					
			if (s.length == 2) {
				int j = Integer.parseInt (s[0]);
				int k = Integer.parseInt (s[1]);
				if ((edgeTable.contains (new Edge(j,k)) == false) &&
						(edgeTable.contains (new Edge(k,j)) == false)) {							
					edgeTable.add(new Edge(j,k));
					degree[j] = degree[j] + 1;
					degree[k] = degree[k] + 1;
					adjacencyList[j].add(k);
					adjacencyList[k].add(j);
					edgeWeightTable.put(new Edge(j,k), (double)0);
				}
				Edge edge = null;// if the edge repeats
				if (edgeTable.contains(new Edge(j,k))) {
					edge = new Edge(j,k);
				}
				else {
					edge = new Edge(k,j);
				}
				double wt = edgeWeightTable.get(edge);                    
				edgeWeightTable.put(edge, (wt + 1));                    					
			} //end if
		}
		reader.close();

		FileWriter fw = new FileWriter("edgeWeights.dat");
		BufferedWriter out = new BufferedWriter(fw);

		for (Edge e:edgeTable) {
			double w = edgeWeightTable.get(e);
			out.write(e.u+"\t"+e.v+"\t"+df.format(w)+"\n");
		}
		out.close();
		System.out.println("Computing Edge Strengths...");
		computeEdgeStrengths();

		fw = new FileWriter("edgeStrengths.dat");
		out = new BufferedWriter(fw);
		for (Edge e: edgeTable) {
			double w = edgeStrengthTable.get(e);
			out.write(e.u+"\t"+e.v+"\t"+df.format(w)+"\n");
			out.write(e.v+"\t"+e.u+"\t"+df.format(edgeStrengthTable.get(new Edge(e.v, e.u)))+"\n");
		}
		out.close ();		

		fw = new FileWriter ("adjLists.dat");
		out = new BufferedWriter (fw);
		for (int v = 1; v <= numVertices; v++) {
			out.write (v +" "+Arrays.toString(adjacencyList[v].toArray())+"\n");
		}		
		out.close();
		
		g.setAdjacencyList (adjacencyList);
		g.setEdgeTable (edgeTable);
		g.setEdgeWeightTable (edgeWeightTable);
		g.setEdgeStrengthTable(edgeStrengthTable);
		
		return g;
	}

	public void computeEdgeStrengths() throws IOException {
		for (Edge edge: edgeTable) {
			if (edge.u == edge.v) {
				edgeStrengthTable.put(edge, (double) 0);
				continue;
			}
			HashSet <Integer> commonNbrs = new HashSet<Integer>();
			for (int r : adjacencyList[edge.u]) {
				if (adjacencyList[edge.v].contains(r)) {
					commonNbrs.add(r);
				}
			}			
			// commonNbrs has the intersection of two adjacencyLists of u and v			
			double commonWt = 0;
			for (int w: commonNbrs) {
				Edge commonEdge1 = null, commonEdge2 = null;
				if (edgeTable.contains (new Edge (edge.u, w))) {
					commonEdge1 = new Edge (edge.u, w);
				}
				else {
					commonEdge1 = new Edge (w, edge.u);
				}
				if (edgeTable.contains (new Edge (edge.v, w))) {
					commonEdge2 = new Edge (edge.v, w);
				}
				else {
					commonEdge2 = new Edge (w, edge.v);
				}				
				commonWt = commonWt + edgeWeightTable.get(commonEdge1);					
				commonWt = commonWt + edgeWeightTable.get(commonEdge2);							
			}			
			commonWt = commonWt + edgeWeightTable.get(edge);
			edgeStrengthTable.put(edge, commonWt);
			edgeStrengthTable.put(new Edge(edge.v, edge.u), commonWt);
		}		
	}	
}
