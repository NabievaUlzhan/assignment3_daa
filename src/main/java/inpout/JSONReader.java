package inpout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.List;

public class JSONReader {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static AllDatasets readAll(String path) throws Exception {
        return MAPPER.readValue(new File(path), AllDatasets.class);
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
    }
}
