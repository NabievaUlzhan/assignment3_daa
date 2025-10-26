package graphs;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int n;
    private final List<List<Edge>> adj;
    private final List<Edge> edges;

    public Graph(int n) {
        this.n = n;
        this.adj = new ArrayList<>(n);
        this.edges = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }

    public int n() {
        return n;
    }
    public List<List<Edge>> adj() {
        return adj;
    }
    public List<Edge> edges() {
        return edges;
    }

    public void addUndirectedEdge(int u, int v, int w) {
        edges.add(new Edge(u, v, w));
        adj.get(u).add(new Edge(u, v, w));
        adj.get(v).add(new Edge(v, u, w));
    }
}
