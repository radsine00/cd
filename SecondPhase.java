

import java.io.*;
import java.math.RoundingMode;
import java.text.*;
import java.util.*;

public class SecondPhase{		
	int numVertices;	
	double threshold;		
	HashSet <Integer>[] adjacencyList;	
	HashSet <Edge> edgeTable;	
	Hashtable <Edge, Double> edgeProbTable;
	HashSet <Integer> [] communityList;	
	NumberFormat df;
	int threshold1;
	Graph g;
	@SuppressWarnings("unchecked")
	public SecondPhase(Graph g){
		this.g = g;
		this.numVertices = g.getNumVertices();
		this.threshold = 0.95;
		communityList = new HashSet [numVertices+1];	
		df = DecimalFormat.getInstance();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(4);
		df.setRoundingMode (RoundingMode.DOWN);	
	}
	
	public  void run () throws IOException {
		System.out.println("Graph is Constructed");
		adjacencyList = g.getAdjacencyList();
		edgeTable = g.getEdgeTable();
		edgeProbTable = g.getEdgeProbTable();
		ArrayList <Integer> [] newAdjacencyList = construct (edgeProbTable);
		BFS (newAdjacencyList);		

		HashSet<Integer>[] nashCommunityList = makeOldCopy();
		int threshValue = 30;
		for (; threshValue <= 71 ; threshValue = threshValue + 2 ) {
			String st = "0."+threshValue;			
			double value = Double.parseDouble(st);
			computeOverlap (value);
			for (int v = 1; v <= numVertices; v++) {
				communityList[v].clear();
				for (int k : nashCommunityList[v]) {
					communityList[v].add(k);						
				}			
			} 
		}
	}

	private  ArrayList<Integer>[] construct (Hashtable <Edge, Double> edgeProbTable2) {
		@SuppressWarnings("unchecked")
		ArrayList<Integer> [] newAdjacencyList = new ArrayList [numVertices+1];
		for (int v = 1 ; v <= numVertices; v++) {
			newAdjacencyList[v] = new ArrayList <Integer>();
			communityList[v] = new HashSet <Integer>();
		}
		for (Edge edge : edgeTable) {
			if (edgeProbTable.get(edge) >= threshold) {
				newAdjacencyList[edge.v].add(edge.u);
				newAdjacencyList[edge.u].add(edge.v);
			}
		}
		return newAdjacencyList;
	}


	public  void BFS (ArrayList<Integer>[] newAdjacencyList) {
		boolean[] processed = new boolean [numVertices+1];
		boolean[] offered = new boolean [numVertices+1];
		int tempCom = 0;
		LinkedList<Integer> Q = new LinkedList<Integer>();
		for (int v = 1; v <= numVertices; v++) {
			processed[v] = false;
			offered[v] = false;
		}		
		for (int v = 1; v <= numVertices; v++) {
			if (processed[v] == false) {
				processed[v] = true;
				offered[v] = true;
				tempCom = tempCom + 1;
				communityList[v].add(tempCom);
 				for (int u : newAdjacencyList[v]) {
 					if ((processed[u] == false) && (offered[u] == false)) {						
						Q.offer(u);
						offered[u] = true;						
					}
				}
				while (!Q.isEmpty()) {
					int v1 = Q.poll();					
					communityList[v1].add(tempCom);
					processed[v1] = true;
					for (int u : newAdjacencyList[v1]) {
						if ((processed[u] == false) && (offered[u] == false)) {							
							Q.offer(u);
							offered[u] = true;							
						}
					}
				}
			}
		}			
		System.out.println("Number of components in the intermediate partition:"+tempCom+"\n");
	}

	public  void computeOverlap (double threshold) throws IOException {		
		boolean isChanged = true;
		int roundnum = 1;
		while (isChanged) {
			isChanged = false;
			for (int v = 1; v <= numVertices; v++) {
				roundnum++;
				Hashtable <Integer, Double> affinity = new Hashtable <Integer, Double>();
				for (int u : adjacencyList[v]) {
					for (int k : communityList[u]) {
						double probValue = edgeProbTable.get (new Edge(u,v));				
						if (affinity.get(k) == null) {
							affinity.put(k, probValue);
						}
						else {
							double temp = affinity.get(k);
							temp = temp + probValue;
							affinity.put (k, temp);
						}
					}					
				}				
				HashSet<Integer> newCommunityList = new HashSet<Integer>();
				double maxValue = 0;
				for (int k : affinity.keySet()) {
					double value = affinity.get(k);
					if (maxValue <= value) {
						maxValue = value;
					}
				}				
				for (int k : affinity.keySet()) {
					if (affinity.get(k) >= threshold * maxValue) {
						newCommunityList.add(k);
					}
				}
				double oldAffinity = 0, newAffinity = 0;
				for (int c : affinity.keySet()) {
					if (newCommunityList.contains(c)) {
						newAffinity = newAffinity + affinity.get(c);
					}
					if (communityList[v].contains(c)) {
						oldAffinity = oldAffinity + affinity.get(c);
					}
				}
				// System.out.println("oldAffinity: "+oldAffinity+" New Affinity :"+newAffinity);
				if (newAffinity > oldAffinity) {
					isChanged = true;
					communityList[v].clear();
					communityList[v].addAll(newCommunityList);
				}
			}
		}
		String filename = "nashoverlaptype2" + (int)(threshold * 100) + ".dat";
		System.out.println (threshold+" :Number of rounds to reach Nash Equilibrium in second game: "+roundnum);
		write(filename);
	}
	
	public  void write (String filename) throws IOException {
		Hashtable <Integer, HashSet<Integer>> community =	new Hashtable<Integer, HashSet<Integer>>();
		HashSet<HashSet<Integer>> communitySets = new HashSet<HashSet<Integer>> ();
		FileWriter fw = new FileWriter(filename);
		BufferedWriter out = new BufferedWriter(fw);		
		for (int v = 1; v <= numVertices; v++) {
			HashSet <Integer> l ;
			if (communityList[v].size() > 0) {
				for (int k : communityList[v]) {
					if (community.containsKey(k) == false) {
						l = new HashSet<Integer>();
						l.add(v);
						community.put (k, l);						
					}
					else {
						l = community.get (k);
						l.add (v);
						community.put (k, l);
					}
				}				
			}
		}
		for (int c : community.keySet()) {
			HashSet<Integer> h = community.get(c);
			communitySets.add(h);			
		}
		Iterator<HashSet<Integer>> hIter1 = communitySets.iterator();
		while(hIter1.hasNext()) {
			HashSet<Integer> h = hIter1.next();
			out.write(Arrays.toString(h.toArray())+"\n");
		}
		out.close();	
	}

	private  HashSet <Integer>[] makeOldCopy () throws IOException {		
		@SuppressWarnings("unchecked")
		HashSet<Integer>[] oldCommunityList = new HashSet [numVertices+1];				
		for (int v = 1; v <= numVertices; v++) {
			oldCommunityList[v] = new HashSet<Integer>();
			for (int k : communityList[v]) {
				oldCommunityList[v].add(k);						
			}			
		}				
		return oldCommunityList;		
	}
}
