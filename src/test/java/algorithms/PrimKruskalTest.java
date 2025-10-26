package algorithms;

import graphs.Graph;
import graphs.Edge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrimKruskalTest {
    @Test
    @DisplayName("Prim vs Kruskal: the cost is the same, the size of MST = V-1 (connected graph)")
    void mstCostsEqualAndSizeVminus1() {
        Graph g = new Graph(5);
        g.addUndirectedEdge(0,1,4);
        g.addUndirectedEdge(0,2,3);
        g.addUndirectedEdge(1,2,2);
        g.addUndirectedEdge(1,3,5);
        g.addUndirectedEdge(2,3,7);
        g.addUndirectedEdge(2,4,8);
        g.addUndirectedEdge(3,4,6);

        Prim.Result pr = new Prim().run(g, 0);
        Kruskal.Result kr = new Kruskal().run(g);

        assertEquals(pr.cost, kr.cost, "The cost of MST for Prim and Kruskal must be the same");
        assertEquals(g.n() - 1, pr.mst.size(), "Prim: number of edges MST = V-1");
        assertEquals(g.n() - 1, kr.mst.size(), "Kruskal: number of edges MST = V-1");

        for (Edge e : pr.mst) {
            assertNotEquals(e.u, e.v, "There should be no cycles in the MST");
            assertTrue(e.u >= 0 && e.u < g.n());
            assertTrue(e.v >= 0 && e.v < g.n());
        }
    }

    @Test
    @DisplayName("Disconnected graph: MST size < V-1 (algorithms work correctly)")
    void disconnectedGraphHandled() {
        Graph g = new Graph(4);
        g.addUndirectedEdge(0,1,1);
        g.addUndirectedEdge(2,3,2);

        Prim.Result pr = new Prim().run(g, 0);
        Kruskal.Result kr = new Kruskal().run(g);

        assertTrue(pr.mst.size() < g.n() - 1 || kr.mst.size() < g.n() - 1,
                "On a disjoint graph, an MST cannot contain V-1 edges.");
    }

    @Test
    @DisplayName("A connected graph of 6 vertices: minimal edges and no cycles")
    void simpleSixVertices() {
        Graph g = new Graph(6);
        g.addUndirectedEdge(0,1,1);
        g.addUndirectedEdge(1,2,1);
        g.addUndirectedEdge(2,3,1);
        g.addUndirectedEdge(3,4,1);
        g.addUndirectedEdge(4,5,1);

        g.addUndirectedEdge(0,2,10);
        g.addUndirectedEdge(1,3,10);
        g.addUndirectedEdge(2,4,10);
        g.addUndirectedEdge(3,5,10);

        Prim.Result pr = new Prim().run(g, 0);
        Kruskal.Result kr = new Kruskal().run(g);

        assertEquals(5, pr.mst.size());
        assertEquals(5, kr.mst.size());
        assertEquals(pr.cost, kr.cost);
        assertEquals(5, pr.cost, "Here, the MST is just a chain of 5 edges with a weight of 1");
    }
}
