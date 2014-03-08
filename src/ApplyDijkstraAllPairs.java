
/**
 * ApplyDijkstraAllPairs takes the graph G** and finds the shortest path between
 * every vertex and every other vertex using DijkstraAllPairsSP's algorithm. The
 * edges of G** are reverse-calibrated to recover the edge-weights in the
 * original graph G. Now use these edge-weights to determine the shortest paths
 * in the original graph G.
 * 
 * @author modified by SephyZhou
 * 
 */
public class ApplyDijkstraAllPairs {
	private ApplyDijkstra[] all;
	public boolean PIPELINE = CreateAuxiliaryGraph.PIPELINE;

	public ApplyDijkstraAllPairs(In in) {

	}

	public ApplyDijkstraAllPairs(CreateAuxiliaryGraph G) {
		all = new ApplyDijkstra[G.V()];
		for (int v = 0; v < G.V(); v++)
			all[v] = new ApplyDijkstra(G, v);
	}

	public Iterable<DirectedEdge> path(int s, int t) {
		return all[s].pathTo(t);
	}

	public double dist(int s, int t) {
		return all[s].distTo(t);
	}

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

	static int V;

	static double pointWeight[];

	public static void main(String[] args) {
		readGraphFromBF();
		CreateAuxiliaryGraph G = new CreateAuxiliaryGraph(
				"ApplyDijsktraAllPairs", 0);
		ApplyDijkstraAllPairs allpairs = new ApplyDijkstraAllPairs(G);

		double totalDistance[][] = new double[V][V];

		for (int s = 0; s < G.V(); s++) {
			for (int t = 0; t < G.V(); t++) {
				if (allpairs.path(s, t) != null) {
					for (DirectedEdge e : allpairs.path(s, t)) {
						totalDistance[s][t] += e.weight() - pointWeight[e.from()]
								+ pointWeight[e.to()];

					}
				}
			}
		}
		for (int s = 0; s < G.V(); s++) {
			for (int t = 0; t < G.V(); t++) {
				if (allpairs.path(s, t) != null) {
					if(totalDistance[s][t]<0){
					StdOut.printf("%d to %d (%.2f)  ", s, t,
							totalDistance[s][t]);
					} else {
						StdOut.printf("%d to %d ( %.2f)  ", s, t,
								totalDistance[s][t]);
					}
					if (allpairs.path(s, t) != null) {
						for (DirectedEdge e : allpairs.path(s, t)) {
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
					StdOut.printf("%d to %d\tno path\n", s, t);
				}
			}
			StdOut.println();
		}
	}
}
