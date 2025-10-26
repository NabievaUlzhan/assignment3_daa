package cli;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.*;

public class GraphGenerator {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private static final long SEED = 2429L;

    public static void main(String[] args) throws Exception {
        ensureDir("input");

        int[] SMALL  = {10, 15, 20, 25, 30};
        int[] MEDIUM = {50, 75, 100, 125, 150, 175, 200, 225, 250, 300};
        int[] LARGE  = {400, 450, 500, 550, 600, 650, 700, 800, 900, 1000};
        int[] EXTRA  = {1300, 1600, 2000, 2300, 2600, 3000};

        Dataset small  = buildDataset("small",  SMALL,  'S');
        Dataset medium = buildDataset("medium", MEDIUM, 'M');
        Dataset large  = buildDataset("large",  LARGE,  'L');
        Dataset extra  = buildDataset("extra",  EXTRA,  'E');

        MAPPER.writeValue(new File("input/small_graphs.json"),  wrap(small));
        MAPPER.writeValue(new File("input/medium_graphs.json"), wrap(medium));
        MAPPER.writeValue(new File("input/large_graphs.json"),  wrap(large));
        MAPPER.writeValue(new File("input/extra_graphs.json"),  wrap(extra));

        AllDatasets all = new AllDatasets();
        all.datasets = Arrays.asList(small, medium, large, extra);
        MAPPER.writeValue(new File("input/assign_3_input.json"), all);

        System.out.println("Generated all datasets into /input");
    }

    private static Dataset buildDataset(String category, int[] sizes, char prefix) {
        Dataset ds = new Dataset();
        ds.category = category;
        ds.graphs = new ArrayList<>();
        Random rnd = new Random(SEED + category.hashCode());

        for (int i = 0; i < sizes.length; i++) {
            int n = sizes[i];
            double density = pickDensity(category, n, rnd);
            GraphJson g = makeConnected(prefix + String.valueOf(i + 1), n, density, rnd);
            ds.graphs.add(g);

            double maxE = n * (n - 1) / 2.0;
            double real = 100.0 * g.edges.size() / maxE;
            System.out.printf("[%s %s] V=%d E=%d (target≈%.1f%%, actual=%.1f%%)%n",
                    category, g.id, n, g.edges.size(), density, real);
        }
        return ds;
    }

    private static double pickDensity(String category, int n, Random rnd) {
        switch (category) {
            case "small":  return 40 + rnd.nextDouble() * 20;
            case "medium": return 20 + rnd.nextDouble() * 20;
            case "large":  return 7  + rnd.nextDouble() * 10;
            case "extra":  return 2.5 + rnd.nextDouble() * 4;
            default:       return 20;
        }
    }

    private static GraphJson makeConnected(String id, int n, double densityPercent, Random rnd) {
        GraphJson g = new GraphJson();
        g.id = id; g.directed = false; g.vertices = n;
        g.nodes = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) g.nodes.add("N" + i);

        long maxEdges = (long) n * (n - 1) / 2;
        long target = Math.max(n - 1, Math.min(maxEdges, Math.round(maxEdges * densityPercent / 100.0)));

        Set<Long> used = new HashSet<>();
        List<Integer> connected = new ArrayList<>();
        List<Integer> unconnected = new ArrayList<>();
        connected.add(1);
        for (int i = 2; i <= n; i++) unconnected.add(i);

        g.edges = new ArrayList<>();

        while (!unconnected.isEmpty()) {
            int u = connected.get(rnd.nextInt(connected.size()));
            int idx = rnd.nextInt(unconnected.size());
            int v = unconnected.remove(idx);
            addEdgeIfNew(g, used, u, v, 1 + rnd.nextInt(100), n);
            connected.add(v);
        }

        int attempts = 0, limit = (int) (20L * n);
        while (g.edges.size() < target && attempts < limit) {
            int u = 1 + rnd.nextInt(n);
            int v = 1 + rnd.nextInt(n);
            if (u == v) { attempts++; continue; }
            int a = Math.min(u, v), b = Math.max(u, v);
            long code = pairCode(a, b, n);
            if (used.contains(code)) { attempts++; continue; }
            addEdgeIfNew(g, used, a, b, 1 + rnd.nextInt(100), n);
        }
        return g;
    }

    private static void addEdgeIfNew(GraphJson g, Set<Long> used, int a, int b, int w, int n) {
        int u = Math.min(a, b), v = Math.max(a, b);
        long code = pairCode(u, v, n);
        if (used.add(code)) g.edges.add(new EdgeJson("N" + u, "N" + v, w));
    }

    private static long pairCode(int u, int v, int n) {
        return ((long)u) * (n + 1L) + v;
    }

    private static void ensureDir(String path) {
        File d = new File(path);
        if (!d.exists()) d.mkdirs();
    }

    public static class AllDatasets {
        public List<Dataset> datasets;
    }
    public static class Dataset {
        public String category;
        public List<GraphJson> graphs;
    }
    public static class GraphJson {
        public String id;
        public boolean directed;
        public int vertices;
        public List<String> nodes;
        public List<EdgeJson> edges;
    }
    public static class EdgeJson {
        public String from;
        public String to;
        public int weight;
        public EdgeJson() {}
        public EdgeJson(String f, String t, int w){
            from=f; to=t; weight=w;
        }
    }
    private static OneDataset wrap(Dataset d){
        OneDataset w=new OneDataset();
        w.dataset=d; return w;
    }
    public static class OneDataset{
        public Dataset dataset;
    }
}
