import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//----Author: Radhika Arava----

public class CDMain {			
	public static void main (String[] args) throws Exception {		
		String filename = args[0];
		System.out.println(filename);
		int numVertices = Integer.parseInt (args[1]);				 
		final int NUMITERATIONS = 100;
		long start = System.currentTimeMillis();
		ConstructGraph object = new ConstructGraph (filename, numVertices);
		Graph g = object.constructGraph();				
		FirstPhase fp = new FirstPhase (g, NUMITERATIONS);
		fp.run ();
		long end = System.currentTimeMillis();	
		System.out.println ("\n Took : " + ((end - start) / 1000));
	}
}
