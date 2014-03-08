import static org.junit.Assert.*;

import org.junit.Test;


public class test {
	String o1 = "1.out";
	String o2 = "2.out";
	@Test
	public void test() {
		In in = new In("lecturegraph.txt");
		CreateAuxiliaryGraph G = new CreateAuxiliaryGraph(in);
		StdOut.println(G);
		StdOut.println("----------------------");
		Out out = new Out(o1);
		out.print(G);
		out.close();
		//In in2 = new In(o1);
		//StdOut.println(in2.readAll());
		
		
		In in2 = new In("lecturegraph.txt");
		ApplyBellmanFord AB = new ApplyBellmanFord(in2);
		StdOut.println(AB);
		Out out2 = new Out(o2);
		out2.print(AB);
		out2.close();
		
		
		In in3 = new In(o2);
		
	}

}
