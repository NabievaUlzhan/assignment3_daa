package metrics;

public class Metrics {
    public long comparisons = 0;  // comparisons
    public long heapOps = 0;      // Prim
    public long unions = 0;       // union Kruskal
    public long finds = 0;        // find Kruskal
    private long startNs, endNs;

    public void start() {
        startNs = System.nanoTime();
    }
    public void stop()  {
        endNs = System.nanoTime();
    }
    public double timeMs() {
        return (endNs - startNs) / 1_000_000.0;
    }

    public long primOps() {
        return comparisons + heapOps;
    }
    public long kruskalOps() {
        return comparisons + unions + finds;
    }
}
