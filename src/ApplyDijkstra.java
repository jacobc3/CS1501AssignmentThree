/**
 * ApplyDijkstra takes the graph G** and finds the shortest path between a
 * specified vertex (in the example above this vertex is 0) and every other
 * vertex using Dijkstra's algorithm. The edges of G** are reverse-calibrated to
 * recover the edge-weights in the original graph G. Now use these edge-weights
 * to determine the shortest paths in the original graph G.
 * 
 * @author SephyZhou
 * 
 */

public class ApplyDijkstra {

	private double[] distTo; // distTo[v] = distance of shortest s->v path
	private DirectedEdge[] edgeTo; // edgeTo[v] = last edge on shortest s->v
									// path
	private IndexMinPQ<Double> pq; // priority queue of vertices
	public static boolean PIPELINE = CreateAuxiliaryGraph.PIPELINE;

	public ApplyDijkstra(CreateAuxiliaryGraph g, int s) {
		for (DirectedEdge e : g.edges()) {
			if (e.weight() < 0)
				throw new IllegalArgumentException("edge " + e
						+ " has negative weight");
		}

		distTo = new double[g.V()];
		edgeTo = new DirectedEdge[g.V()];
		for (int v = 0; v < g.V(); v++)
			distTo[v] = Double.POSITIVE_INFINITY;
		distTo[s] = 0.0;

		// relax vertices in order of distance from s
		pq = new IndexMinPQ<Double>(g.V());
		pq.insert(s, distTo[s]);
		while (!pq.isEmpty()) {
			int v = pq.delMin();
			for (DirectedEdge e : g.adj(v))
				relax(e);
		}

		// check optimality conditions
		assert check(g, s);
	}

	// relax edge e and update pq if changed
	private void relax(DirectedEdge e) {
		int v = e.from(), w = e.to();
		if (distTo[w] > distTo[v] + e.weight()) {
			distTo[w] = distTo[v] + e.weight();
			edgeTo[w] = e;
			if (pq.contains(w))
				pq.decreaseKey(w, distTo[w]);
			else
				pq.insert(w, distTo[w]);
		}
	}

	// length of shortest path from s to v
	public double distTo(int v) {
		return distTo[v];
	}

	// is there a path from s to v?
	public boolean hasPathTo(int v) {
		return distTo[v] < Double.POSITIVE_INFINITY;
	}

	// shortest path from s to v as an Iterable, null if no such path
	public Iterable<DirectedEdge> pathTo(int v) {
		if (!hasPathTo(v))
			return null;
		Stack<DirectedEdge> path = new Stack<DirectedEdge>();
		for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
			path.push(e);
		}
		return path;
	}

	// check optimality conditions:
	// (i) for all edges e: distTo[e.to()] <= distTo[e.from()] + e.weight()
	// (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] +
	// e.weight()
	private boolean check(CreateAuxiliaryGraph g, int s) {

		// check that edge weights are nonnegative
		for (DirectedEdge e : g.edges()) {
			if (e.weight() < 0) {
				System.err.println("negative edge weight detected");
				return false;
			}
		}

		// check that distTo[v] and edgeTo[v] are consistent
		if (distTo[s] != 0.0 || edgeTo[s] != null) {
			System.err.println("distTo[s] and edgeTo[s] inconsistent");
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

		// check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] +
		// e.weight()
		for (int w = 0; w < g.V(); w++) {
			if (edgeTo[w] == null)
				continue;
			DirectedEdge e = edgeTo[w];
			int v = e.from();
			if (w != e.to())
				return false;
			if (distTo[v] + e.weight() != distTo[w]) {
				System.err.println("edge " + e + " on shortest path not tight");
				return false;
			}
		}
		return true;
	}

	static int V;

	public static void readGraphFromBF() {
		StdIn.readLine();
		V = StdIn.readInt();
		if (V < 0)
			throw new IllegalArgumentException(
					"Number of vertices in a Digraph must be nonnegative");
		pointWeight = new double[V];
		StdIn.readInt();
		for (int i = 0; i < V; i++) {
			int point = StdIn.readInt();
			double weight = StdIn.readDouble();
			// StdOut.println("point is "+point+" weight is "+weight );
			pointWeight[point] = weight;
		}
		// read pointweight finished
		StdIn.readLine();
		StdIn.readLine();
		// StdOut.println();
	}

	static int theS;
	static double pointWeight[];

	public static void main(String[] args) {
		//StdOut.println("================= This is ApplyDijkstra ===============");
		if (PIPELINE) {
			readGraphFromBF();
		} else {
		}

		// start read graph
		CreateAuxiliaryGraph G = new CreateAuxiliaryGraph("ApplyDijsktra", 0); // TODO
																				// READ
																				// FROM
																				// STDIN
		int s = Integer.parseInt(args[0]);

		// compute shortest paths
		ApplyDijkstra sp = new ApplyDijkstra(G, s);
		// StdOut.println("ApplyDijkstra's CreateAuxiliaryGraph is\n"+G);
		// StdOut.println("ApplyDijkstra's Result:");
		// print shortest path

		double totalDistance[] = new double[V];
		for (int t = 0; t < G.V(); t++) {
			if (sp.hasPathTo(t)) {
				for (DirectedEdge e : sp.pathTo(t)) {
					totalDistance[t] += e.weight() - pointWeight[e.from()]
							+ pointWeight[e.to()];

				}
			}
		}

		for (int t = 0; t < G.V(); t++) {
			if (sp.hasPathTo(t)) {
				if(totalDistance[t]<0){
					StdOut.printf("%d to %d (%.2f)  ", s, t,
							totalDistance[t]);
					} else {
						StdOut.printf("%d to %d ( %.2f)  ", s, t,
								totalDistance[t]);
					}
				if (sp.hasPathTo(t)) {
					for (DirectedEdge e : sp.pathTo(t)) {
						StdOut.print(e.from() + "->" + e.to() + " ");
						double result = e.weight() - pointWeight[e.from()]
								+ pointWeight[e.to()];
						if(result <0){
						StdOut.printf("%.2f\t",result);
						} else {
							StdOut.printf(" %.2f\t",result);
						}
					}
				}
				StdOut.println();
			} else {
				StdOut.printf("%d to %d         no path\n", s, t);
			}
		}
	}

}
