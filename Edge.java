public class Edge{
	
	public int u,v,weight;
	public String color;
	public double prob;
	
	public Edge(int s, int t){
		this.u = s;
		this.v = t;
		this.weight = 1;
		this.color = "";
		this.prob = 0.0;		
	}
	
	public Edge(int s, int t, int w){
		this.u = s;
		this.v = t;
		this.weight = w;
		this.prob = 0.0;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Edge){
			Edge e = (Edge)obj;
			if (e.u == this.u && e.v == this.v)
				return true;
		}
		return false;
	}
	
	public int hashCode(){
		return u*1000000+v;
	}
	
}
