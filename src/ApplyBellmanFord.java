import java.util.Iterator;

/**
 * ApplyBellman takes the auxiliary graph G* and finds the shortest path from
 * vertex s to all the other vertices. These distances are the weights assigned
 * to each of the vertices in the original graph G to re-weight the edges of G
 * and are calculated by Bellman-Ford. In addition to finding the
 * vertex-weights, ApplyBellman forms a graph identical to G, call it G** with
 * newly calculated edge-weights (note these weights are now non-negative).
 * 
 * @author SephyZhou
 * 
 */
public class ApplyBellmanFord {
	private double[] distTo; // distTo[v] = distance of shortest s->v path
	private DirectedEdge[] edgeTo; // edgeTo[v] = last edge on shortest s->v
									// path
	private boolean[] onQueue; // onQueue[v] = is v currently on the queue?
	private Queue<Integer> queue; // queue of vertices to relax
	private int cost; // number of calls to relax()
	private Iterable<DirectedEdge> cycle; // negative cycle (or null if no such
											// cycle)
	public static boolean PIPELINE = CreateAuxiliaryGraph.PIPELINE;

	/**
	 * Computes a shortest paths tree from <tt>s</tt> to every other vertex in
	 * the edge-weighted digraph <tt>G</tt>.
	 * 
	 * @param g
	 *            the acyclic digraph
	 * @param s
	 *            the source vertex
	 * @throws IllegalArgumentException
	 *             unless 0 &le; <tt>s</tt> &le; <tt>V</tt> - 1
	 */
	public ApplyBellmanFord(CreateAuxiliaryGraph g, int s) {
		distTo = new double[g.V()];
		edgeTo = new DirectedEdge[g.V()];
		onQueue = new boolean[g.V()];
		for (int v = 0; v < g.V(); v++)
			distTo[v] = Double.POSITIVE_INFINITY;
		distTo[s] = 0.0;

		// Bellman-Ford algorithm
		queue = new Queue<Integer>();
		queue.enqueue(s);
		onQueue[s] = true;
		while (!queue.isEmpty() && !hasNegativeCycle()) {
			int v = queue.dequeue();
			onQueue[v] = false;
			relax(g, v);
		}

		assert check(g, s);
	}

	// relax vertex v and put other endpoints on queue if changed
	private void relax(CreateAuxiliaryGraph g, int v) {
		for (DirectedEdge e : g.adj(v)) {
			int w = e.to();
			if (distTo[w] > distTo[v] + e.weight()) {
				distTo[w] = distTo[v] + e.weight();
				edgeTo[w] = e;
				if (!onQueue[w]) {
					queue.enqueue(w);
					onQueue[w] = true;
				}
			}
			if (cost++ % g.V() == 0)
				findNegativeCycle();
		}
	}

	/**
	 * Is there a negative cycle reachable from the source vertex <tt>s</tt>?
	 * 
	 * @return <tt>true</tt> if there is a negative cycle reachable from the
	 *         source vertex <tt>s</tt>, and <tt>false</tt> otherwise
	 */
	public boolean hasNegativeCycle() {
		return cycle != null;
	}

	/**
	 * Returns a negative cycle reachable from the source vertex <tt>s</tt>, or
	 * <tt>null</tt> if there is no such cycle.
	 * 
	 * @return a negative cycle reachable from the soruce vertex <tt>s</tt> as
	 *         an iterable of edges, and <tt>null</tt> if there is no such cycle
	 */
	public Iterable<DirectedEdge> negativeCycle() {
		return cycle;
	}

	// by finding a cycle in predecessor graph
	private void findNegativeCycle() {
		int V = edgeTo.length;
		EdgeWeightedDigraph spt = new EdgeWeightedDigraph(V);
		for (int v = 0; v < V; v++)
			if (edgeTo[v] != null)
				spt.addEdge(edgeTo[v]);

		EdgeWeightedDirectedCycle finder = new EdgeWeightedDirectedCycle(spt);
		cycle = finder.cycle();
	}

	/**
	 * Returns the length of a shortest path from the source vertex <tt>s</tt>
	 * to vertex <tt>v</tt>.
	 * 
	 * @param v
	 *            the destination vertex
	 * @return the length of a shortest path from the source vertex <tt>s</tt>
	 *         to vertex <tt>v</tt>; <tt>Double.POSITIVE_INFINITY</tt> if no
	 *         such path
	 * @throws UnsupportedOperationException
	 *             if there is a negative cost cycle reachable from the source
	 *             vertex <tt>s</tt>
	 */
	public double distTo(int v) {
		if (hasNegativeCycle())
			throw new UnsupportedOperationException(
					"Negative cost cycle exists");
		return distTo[v];
	}

	/**
	 * Is there a path from the source <tt>s</tt> to vertex <tt>v</tt>?
	 * 
	 * @param v
	 *            the destination vertex
	 * @return <tt>true</tt> if there is a path from the source vertex
	 *         <tt>s</tt> to vertex <tt>v</tt>, and <tt>false</tt> otherwise
	 */
	public boolean hasPathTo(int v) {
		return distTo[v] < Double.POSITIVE_INFINITY;
	}

	/**
	 * Returns a shortest path from the source <tt>s</tt> to vertex <tt>v</tt>.
	 * 
	 * @param v
	 *            the destination vertex
	 * @return a shortest path from the source <tt>s</tt> to vertex <tt>v</tt>
	 *         as an iterable of edges, and <tt>null</tt> if no such path
	 * @throws UnsupportedOperationException
	 *             if there is a negative cost cycle reachable from the source
	 *             vertex <tt>s</tt>
	 */
	public Iterable<DirectedEdge> pathTo(int v) {
		if (hasNegativeCycle())
			throw new UnsupportedOperationException(
					"Negative cost cycle exists");
		if (!hasPathTo(v))
			return null;
		Stack<DirectedEdge> path = new Stack<DirectedEdge>();
		for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
			path.push(e);
		}
		return path;
	}

	// check optimality conditions: either
	// (i) there exists a negative cycle reacheable from s
	// or
	// (ii) for all edges e = v->w: distTo[w] <= distTo[v] + e.weight()
	// (ii') for all edges e = v->w on the SPT: distTo[w] == distTo[v] +
	// e.weight()
	private boolean check(CreateAuxiliaryGraph g, int s) {

		// has a negative cycle
		if (hasNegativeCycle()) {
			double weight = 0.0;
			for (DirectedEdge e : negativeCycle()) {
				weight += e.weight();
			}
			if (weight >= 0.0) {
				System.err.println("error: weight of negative cycle = "
						+ weight);
				return false;
			}
		}

		// no negative cycle reachable from source
		else {

			// check that distTo[v] and edgeTo[v] are consistent
			if (distTo[s] != 0.0 || edgeTo[s] != null) {
				System.err.println("distanceTo[s] and edgeTo[s] inconsistent");
				return false;
			}
			for (int v = 0; v < g.V(); v++) {
				if (v == s)
					continue;
				if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
					System.err.println("distTo[] and edgeTo[] inconsistent");
					return false;
				}
			}

			// check that all edges e = v->w satisfy distTo[w] <= distTo[v] +
			// e.weight()
			for (int v = 0; v < g.V(); v++) {
				for (DirectedEdge e : g.adj(v)) {
					int w = e.to();
					if (distTo[v] + e.weight() < distTo[w]) {
						System.err.println("edge " + e + " not relaxed");
						return false;
					}
				}
			}

			// check that all edges e = v->w on SPT satisfy distTo[w] ==
			// distTo[v] + e.weight()
			for (int w = 0; w < g.V(); w++) {
				if (edgeTo[w] == null)
					continue;
				DirectedEdge e = edgeTo[w];
				int v = e.from();
				if (w != e.to())
					return false;
				if (distTo[v] + e.weight() != distTo[w]) {
					System.err.println("edge " + e
							+ " on shortest path not tight");
					return false;
				}
			}
		}

		StdOut.println("Satisfies optimality conditions");
		StdOut.println();
		return true;
	}

	String toString = "";
	// EdgeWeightedDigraph theGraph;
	
	
	public ApplyBellmanFord(In in) {
		toString = "";
		CreateAuxiliaryGraph G = new CreateAuxiliaryGraph(in);
		CreateAuxiliaryGraph originG = new CreateAuxiliaryGraph(G);
		// theGraph = new EdgeWeightedDigraph(G);
		ApplyBellmanFord sp = new ApplyBellmanFord(G, theS);
		//StdOut.println("GRAPH IS "+ originG);
		// print negative cycle
		if (sp.hasNegativeCycle()) {
			for (DirectedEdge e : sp.negativeCycle())
				StdOut.println(e);
		}
		// print shortest paths
		else {
			toString += G.V()+"\n"+G.E()+"\n";
			double[] edgeWeight = new double[G.E()];
			for (int v = 0; v < G.V(); v++) {
				if (sp.hasPathTo(v)) {			
					toString += v+" "+ sp.distTo(v)+"\n";
					edgeWeight[v] = sp.distTo(v);
				} else {
					toString += v+" 0.0"+"\n";
					edgeWeight[v] = 0.0f;
				}
			}			
			Iterator<DirectedEdge> iterator = originG.edges().iterator();
			while (iterator.hasNext()) {
				DirectedEdge e = iterator.next();
				double fromVW = edgeWeight[e.from()];
				double toVW = edgeWeight[e.to()];
				/*
				StdOut.printf("%d->%d =  %5.2f + %5.2f - %5.2f  = %5.2f\n",
						e.from(), e.to(), e.weight(), fromVW, toVW, e.weight()
								+ fromVW - toVW);
				*/
				//toString += e.from()+"->"+e.to()+" = "+ e.weight()+" + "+ fromVW+" - "+toVW+" = "+(e.weight()+ fromVW - toVW)+"\n";
				toString += e.from()+" "+e.to()+" "+(e.weight()+ fromVW - toVW)+"\n";
			}

		}
		if(PIPELINE){
			StdOut.print(this);
		}
	}
	
	public ApplyBellmanFord() {
		
		toString = "";
		CreateAuxiliaryGraph G = new CreateAuxiliaryGraph("BellmanFord",theS); 
		CreateAuxiliaryGraph originG = new CreateAuxiliaryGraph(G); //COPY ABOVE
		//StdOut.println("@ApplyBellmanFord:Print CreateAuxiliaryGraph" + G);
		// theGraph = new EdgeWeightedDigraph(G);
		ApplyBellmanFord sp = new ApplyBellmanFord(G, theS);  
		//StdOut.println("GRAPH IS "+ originG);
		// print negative cycle
		if (sp.hasNegativeCycle()) {
			for (DirectedEdge e : sp.negativeCycle())
				StdOut.println(e);
			return;
		}
		// print shortest paths
		else {
			toString +="Point weight\n";
			toString += (G.V()-1)+" "+(G.E()-G.V()+1)+"\n";
			double[] edgeWeight = new double[G.E()];
			for (int v = 0; v < G.V()-1; v++) {
				if (sp.hasPathTo(v)) {				
					toString += v+" "+ sp.distTo(v)+"\n";
					if(v!=theS){
						edgeWeight[v] = sp.distTo(v);
					}
				} else {
					if(v!=theS){
						toString += v+" 0.0"+"\n";					
						edgeWeight[v] = 0.0f;
					}
				}
			}			
			Iterator<DirectedEdge> iterator = originG.edges().iterator();
			//StdOut.println("@originG is "+originG);
			toString +="New Edge weight\n";
			toString += (G.V()-1)+" "+(G.E()-G.V()+1)+"\n";
			while (iterator.hasNext()) {
				DirectedEdge e = iterator.next();
				double fromVW = edgeWeight[e.from()];
				double toVW = edgeWeight[e.to()];
				/*
				StdOut.printf("%d->%d =  %5.2f + %5.2f - %5.2f  = %5.2f\n",
						e.from(), e.to(), e.weight(), fromVW, toVW, e.weight()
								+ fromVW - toVW);
				*/
				//toString += e.from()+"->"+e.to()+" = "+ e.weight()+" + "+ fromVW+" - "+toVW+" = "+(e.weight()+ fromVW - toVW)+"\n";
				if(e.from() != theS){
					toString += e.from()+" "+e.to()+" "+(e.weight()+ fromVW - toVW)+"\n";
				}
			}

		}
	}
	
	public String toString(){
		return toString;
	}


	/**
	 * Unit tests the <tt>BellmanFordSP</tt> data type.
	 */
	static int theS;
	public static void main(String[] args) {
		//StdOut.println("================= This is ApplyBellmanFord ===============");
		if (PIPELINE) {
			theS = Integer.parseInt(args[0]);
			ApplyBellmanFord AP = new ApplyBellmanFord();
			
			StdOut.println(AP);
		} else {
		}
	}

}
