package inpout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.List;

public class JSONWriter {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static void writeResults(String path, ResultsFile data) throws Exception {
        MAPPER.writeValue(new File(path), data);
    }

    public static class ResultsFile {
        public List<ResultItem> results;
    }

    public static class ResultItem {
        public int graph_id;
        public String id;
        public String category;
        public InputStats input_stats;
        public AlgoResult prim;
        public AlgoResult kruskal;
    }

    public static class InputStats {
        public int vertices; public int edges;
    }

    public static class AlgoResult {
        public List<EdgeOut> mst_edges;
        public int total_cost;
        public long operations_count;
        public double execution_time_ms;
    }

    public static class EdgeOut {
        public String from;
        public String to;
        public int weight;
        public EdgeOut() {}
        public EdgeOut(String f, String t, int w){
            from=f;
            to=t;
            weight=w;
        }
    }
}
