import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * To create the auxiliary graph G* = (E*, V*) from graph G =(E, V), where E =
 * set of all edges in G and V is the set of all vertices in G: Set V* = V ¡È
 * {s}, where s is a new vertex not in V. Note: V* is the vertex set of G*. Set
 * E* = E ¡È {s->v | v ¦Å V}, where E* is the edge set of G* and s is the vertex
 * described above.
 * 
 * @author modified by SephyZhou
 * 
 */

/**
 * cat lecturegraph.txt | java CreateAuxiliaryGraph 
 * | java ApplyBellmanFord 6 | java ApplyDijkstra 0
 * @author modified by SephyZhou
 *
 */


public class CreateAuxiliaryGraph {
	public final static boolean PIPELINE = true;
	
	public static void main(String[] args) throws IOException{
		
		if(PIPELINE){
			CreateAuxiliaryGraph G = new CreateAuxiliaryGraph();
			StdOut.println(G);
		} else {
		}
	}
	
	

    private final int V;
    private int E;
    private Bag<DirectedEdge>[] adj;
    
    /**
     * Initializes an empty edge-weighted digraph with <tt>V</tt> vertices and 0 edges.
     * param V the number of vertices
     * @throws java.lang.IllegalArgumentException if <tt>V</tt> < 0
     */
    public CreateAuxiliaryGraph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
        this.V = (V);
        this.E = 0;
        adj = (Bag<DirectedEdge>[]) new Bag[this.V];
        for (int v = 0; v < this.V; v++)
            adj[v] = new Bag<DirectedEdge>();
    }

    /**
     * Initializes a random edge-weighted digraph with <tt>V</tt> vertices and <em>E</em> edges.
     * param V the number of vertices
     * param E the number of edges
     * @throws java.lang.IllegalArgumentException if <tt>V</tt> < 0
     * @throws java.lang.IllegalArgumentException if <tt>E</tt> < 0
     */
    public CreateAuxiliaryGraph(int V, int E) {
        this(V);
        if (E < 0) throw new IllegalArgumentException("Number of edges in a Digraph must be nonnegative");
        for (int i = 0; i < E; i++) {
            int v = (int) (Math.random() * V);
            int w = (int) (Math.random() * V);
            double weight = Math.round(100 * Math.random()) / 100.0;
            DirectedEdge e = new DirectedEdge(v, w, weight);
            addEdge(e);
        }
    }

    /**  
     * Initializes an edge-weighted digraph from an input stream.
     * The format is the number of vertices <em>V</em>,
     * followed by the number of edges <em>E</em>,
     * followed by <em>E</em> pairs of vertices and edge weights,
     * with each entry separated by whitespace.
     * @param in the input stream
     * @throws java.lang.IndexOutOfBoundsException if the endpoints of any edge are not in prescribed range
     * @throws java.lang.IllegalArgumentException if the number of vertices or edges is negative
     */
    
    public CreateAuxiliaryGraph(In in) {
        this(in.readInt()+1);
        int E = in.readInt();
        if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
        for (int i = 0; i < E; i++) {
            int v = in.readInt();
            int w = in.readInt();
            if (v < 0 || v >= V) throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
            if (w < 0 || w >= V) throw new IndexOutOfBoundsException("vertex " + w + " is not between 0 and " + (V-1));
            double weight = in.readDouble();
            addEdge(new DirectedEdge(v, w, weight));
        }
        addFinalVertex();
    }
    
    public CreateAuxiliaryGraph() {
    	this(StdIn.readInt()+1);
    	 int E = StdIn.readInt();
         if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
         for (int i = 0; i < E; i++) {
             int v = StdIn.readInt();
             int w = StdIn.readInt();
             if (v < 0 || v >= V) throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
             if (w < 0 || w >= V) throw new IndexOutOfBoundsException("vertex " + w + " is not between 0 and " + (V-1));
             double weight = StdIn.readDouble();
             addEdge(new DirectedEdge(v, w, weight));
         }
         addFinalVertex();
 	}
    public void addFinalVertex(){
    	int v = V-1;
    	for(int i = 0; i< V-1; i++){
    		double weight = 0.0;
    		addEdge(new DirectedEdge(v, i, weight));
    	}
    }
    /**
     * Initializes a new edge-weighted digraph that is a deep copy of <tt>G</tt>.
     * @param G the edge-weighted graph to copy
     */
    public CreateAuxiliaryGraph(CreateAuxiliaryGraph G) {
        this(G.V());
        this.E = G.E();
        for (int v = 0; v < G.V(); v++) {
            // reverse so that adjacency list is in same order as original
            Stack<DirectedEdge> reverse = new Stack<DirectedEdge>();
            for (DirectedEdge e : G.adj[v]) {
                reverse.push(e);
            }
            for (DirectedEdge e : reverse) {
                adj[v].add(e);
            }
        }
    }

 
	public CreateAuxiliaryGraph(String string, int theS) {
		this(StdIn.readInt());
   	 	int E = StdIn.readInt();
        if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
        for (int i = 0; i < E; i++) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            if (v < 0 || v >= V) throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
            if (w < 0 || w >= V) throw new IndexOutOfBoundsException("vertex " + w + " is not between 0 and " + (V-1));
            double weight = StdIn.readDouble();
            addEdge(new DirectedEdge(v, w, weight));
        }
	}

	/**
     * Returns the number of vertices in the edge-weighted digraph.
     * @return the number of vertices in the edge-weighted digraph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in the edge-weighted digraph.
     * @return the number of edges in the edge-weighted digraph
     */
    public int E() {
        return E;
    }

    /**
     * Adds the directed edge <tt>e</tt> to the edge-weighted digraph.
     * @param e the edge
     */
    public void addEdge(DirectedEdge e) {
        int v = e.from();
        adj[v].add(e);
        E++;
    }


    /**
     * Returns the directed edges incident from vertex <tt>v</tt>.
     * @return the directed edges incident from vertex <tt>v</tt> as an Iterable
     * @param v the vertex
     * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
     */
    public Iterable<DirectedEdge> adj(int v) {
        if (v < 0 || v >= V) throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
        return adj[v];
    }

    /**
     * Returns all directed edges in the edge-weighted digraph.
     * To iterate over the edges in the edge-weighted graph, use foreach notation:
     * <tt>for (DirectedEdge e : G.edges())</tt>.
     * @return all edges in the edge-weighted graph as an Iterable.
     */
    public Iterable<DirectedEdge> edges() {
        Bag<DirectedEdge> list = new Bag<DirectedEdge>();
        for (int v = 0; v < V; v++) {
            for (DirectedEdge e : adj(v)) {
                list.add(e);
            }
        }
        return list;
    } 

    /**
     * Returns the number of directed edges incident from vertex <tt>v</tt>.
     * This is known as the <em>outdegree</em> of vertex <tt>v</tt>.
     * @return the number of directed edges incident from vertex <tt>v</tt>
     * @param v the vertex
     * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
     */
    public int outdegree(int v) {
        if (v < 0 || v >= V) throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
        return adj[v].size();
    }

    /**
     * Returns a string representation of the edge-weighted digraph.
     * This method takes time proportional to <em>E</em> + <em>V</em>.
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *   followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            //s.append(v + ": ");
            for (DirectedEdge e : adj[v]) {
                s.append(e.from() + " "+e.to()+" "+e.weight()+"\n");
            }
        }
        return s.toString();
    }

}
