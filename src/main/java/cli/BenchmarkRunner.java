package cli;

import algorithms.Kruskal;
import algorithms.Prim;
import graphs.Edge;
import graphs.Graph;
import inpout.JSONWriter;
import inpout.JSONReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.*;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {
        String inputPath  = args.length > 0 ? args[0] : "input/assign_3_input.json";
        String outputPath = args.length > 1 ? args[1] : "output.json";
        String csvPath    = args.length > 2 ? args[2] : "results.csv";

        JSONReader.AllDatasets all = inpout.JSONReader.readAll(inputPath);
        JSONWriter.ResultsFile out = new JSONWriter.ResultsFile();
        out.results = new ArrayList<>();

        List<String> csv = new ArrayList<>();
        csv.add(String.join(" | ",
                "Category", "Graph ID", "Graph Name",
                "Vertices", "Edges",
                "Prim Cost", "Kruskal Cost",
                "Prim Ops", "Kruskal Ops",
                "Prim Time (ms)", "Kruskal Time (ms)",
                "Cost Diff", "Efficiency Ratio (Kruskal/Prim)"
        ));

        DecimalFormat df2 = new DecimalFormat("#0.00");
        int gid = 1;

        for (JSONReader.Dataset ds : all.datasets) {
            for (JSONReader.GraphJson gj : ds.graphs) {
                int n = (gj.nodes != null && !gj.nodes.isEmpty()) ? gj.nodes.size() : gj.vertices;
                Map<String, Integer> idx = buildIndex(gj, n);
                Graph g = new Graph(n);
                for (JSONReader.EdgeJson e : gj.edges) {
                    int u = idx.get(e.from);
                    int v = idx.get(e.to);
                    g.addUndirectedEdge(u, v, e.weight);
                }

                Prim.Result pr = new Prim().run(g, 0);
                Kruskal.Result kr = new Kruskal().run(g);

                JSONWriter.ResultItem item = new JSONWriter.ResultItem();
                item.graph_id = gid;
                item.id = gj.id;
                item.category = ds.category;

                JSONWriter.InputStats st = new JSONWriter.InputStats();
                st.vertices = g.n();
                st.edges = g.edges().size();
                item.input_stats = st;

                item.prim = toAlgoResult(pr, gj);
                item.kruskal = toAlgoResult(kr, gj);

                out.results.add(item);

                double costDiff = Math.abs(pr.cost - kr.cost);
                double effRatio = (pr.M.timeMs() > 0)
                        ? kr.M.timeMs() / pr.M.timeMs()
                        : 0.0;

                String row = String.join(" | ",
                        ds.category,
                        String.valueOf(gid),
                        gj.id,
                        String.valueOf(st.vertices),
                        String.valueOf(st.edges),
                        String.valueOf(pr.cost),
                        String.valueOf(kr.cost),
                        String.valueOf(pr.M.primOps()),
                        String.valueOf(kr.M.kruskalOps()),
                        df2.format(pr.M.timeMs()),
                        df2.format(kr.M.timeMs()),
                        df2.format(costDiff),
                        df2.format(effRatio)
                );
                csv.add(row);

                gid++;
                if (gj.id.equals("S1")) {
                    GraphVisualizer.show(g, pr.mst);
                }
            }
        }

        inpout.JSONWriter.writeResults(outputPath, out);
        writeCsv(csvPath, csv);

        System.out.println("JSON written to: " + outputPath);
        System.out.println("CSV  written to: " + csvPath);
    }

    private static Map<String,Integer> buildIndex(JSONReader.GraphJson gj, int n){
        Map<String,Integer> map = new HashMap<>();
        if (gj.nodes != null && !gj.nodes.isEmpty()) {
            for (int i = 0; i < gj.nodes.size(); i++) map.put(gj.nodes.get(i), i);
        } else {
            for (int i = 0; i < n; i++) map.put("N"+(i+1), i);
        }
        return map;
    }

    private static JSONWriter.AlgoResult toAlgoResult(Object res, JSONReader.GraphJson gj) {
        List<String> names = gj.nodes;
        if (names == null || names.isEmpty()) {
            names = new ArrayList<>();
            for (int i = 1; i <= gj.vertices; i++) names.add("N"+i);
        }

        JSONWriter.AlgoResult a = new JSONWriter.AlgoResult();
        if (res instanceof Prim.Result pr) {
            a.total_cost = pr.cost;
            a.execution_time_ms = round2(pr.M.timeMs());
            a.operations_count = pr.M.primOps();
            a.mst_edges = mapEdges(pr.mst, names);
        } else if (res instanceof Kruskal.Result kr) {
            a.total_cost = kr.cost;
            a.execution_time_ms = round2(kr.M.timeMs());
            a.operations_count = kr.M.kruskalOps();
            a.mst_edges = mapEdges(kr.mst, names);
        }
        return a;
    }

    private static List<JSONWriter.EdgeOut> mapEdges(List<Edge> list, List<String> names) {
        List<JSONWriter.EdgeOut> out = new ArrayList<>();
        for (Edge e: list) {
            out.add(new JSONWriter.EdgeOut(names.get(e.u), names.get(e.v), e.w));
        }
        return out;
    }

    private static double round2(double x){
        return Math.round(x * 100.0) / 100.0;
    }

    private static void writeCsv(String path, List<String> lines) throws Exception {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            for (String s : lines) {
                w.write(s);
                w.newLine();
            }
        }
    }
}
