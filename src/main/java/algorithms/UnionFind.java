package algorithms;

import metrics.Metrics;

public class UnionFind {
    private final int[] p;
    private final int[] r;
    private final Metrics M;

    public UnionFind(int n, Metrics M) {
        this.M = M;
        p = new int[n];
        r = new int[n];
        for (int i = 0; i < n; i++) p[i] = i;
    }

    public int find(int x) {
        M.finds++;
        if (p[x] == x) return x;
        p[x] = find(p[x]);
        return p[x];
    }

    public boolean union(int a, int b) {
        M.unions++;
        a = find(a); b = find(b);
        if (a == b) return false;
        if (r[a] < r[b]) {
            int t = a; a = b; b = t;
        }
        p[b] = a;
        if (r[a] == r[b]) r[a]++;
        return true;
    }
}
