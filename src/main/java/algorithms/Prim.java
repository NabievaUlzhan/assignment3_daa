package algorithms;

import graphs.Edge;
import graphs.Graph;
import metrics.Metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {
    public static class Result {
        public final List<Edge> mst;
        public final int cost;
        public final Metrics M;
        public Result(List<Edge> mst, int cost, Metrics m) {
            this.mst = mst; this.cost = cost; this.M = m;
        }
    }

    public Result run(Graph g, int start) {
        Metrics M = new Metrics(); M.start();
        int n = g.n();
        boolean[] used = new boolean[n];

        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> {
            M.comparisons++;
            return Integer.compare(a.w, b.w);
        });

        List<Edge> mst = new ArrayList<>();
        int cost = 0;

        used[start] = true;
        for (Edge e : g.adj().get(start)) {
            pq.add(e); M.heapOps++;
        }

        while (!pq.isEmpty() && mst.size() < n - 1) {
            Edge e = pq.poll(); M.heapOps++;
            if (used[e.v]) continue;
            used[e.v] = true;
            mst.add(e);
            cost += e.w;
            for (Edge ne : g.adj().get(e.v)) {
                if (!used[ne.v]) {
                    pq.add(ne); M.heapOps++;
                }
            }
        }
        M.stop();
        return new Result(mst, cost, M);
    }
}
