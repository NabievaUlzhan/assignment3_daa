package algorithms;

import graphs.Edge;
import graphs.Graph;
import metrics.Metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kruskal {
    public static class Result {
        public final List<Edge> mst;
        public final int cost;
        public final Metrics M;
        public Result(List<Edge> mst, int cost, Metrics m) {
            this.mst = mst;
            this.cost = cost;
            this.M = m;
        }
    }

    public Result run(Graph g) {
        Metrics M = new Metrics(); M.start();
        List<Edge> edges = new ArrayList<>(g.edges());
        Collections.sort(edges, (a, b) -> {
            M.comparisons++;
            return Integer.compare(a.w, b.w);
        });

        UnionFind uf = new UnionFind(g.n(), M);
        List<Edge> mst = new ArrayList<>();
        int cost = 0;

        for (Edge e : edges) {
            if (uf.union(e.u, e.v)) {
                mst.add(e);
                cost += e.w;
                if (mst.size() == g.n() - 1) break;
            }
        }
        M.stop();
        return new Result(mst, cost, M);
    }
}
