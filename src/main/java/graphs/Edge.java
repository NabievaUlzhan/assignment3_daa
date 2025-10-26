package graphs;

public class Edge implements Comparable<Edge> {
    public final int u;
    public final int v;
    public final int w;

    public Edge(int u, int v, int w) {
        this.u = u; this.v = v; this.w = w;
    }

    @Override
    public int compareTo(Edge o) {
        return Integer.compare(this.w, o.w);
    }
}
