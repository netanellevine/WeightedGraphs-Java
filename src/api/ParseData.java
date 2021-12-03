package api;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;


public class ParseData {
    private HashMap<Integer, Node> Nodes;
    private HashMap<Double, Edge> Edges;
    private Parser p;

    public ParseData(String file_name){
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file_name)) {
            this.p = gson.fromJson(reader, Parser.class);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("\nJson file wasn't found!");
        }
        if(this.p != null) {
            this.Nodes = makeNodes(p.getNodes());
            this.Edges = makeEdges(p.getEdges(), this.Nodes);
        }
    }

    /**
     *
     * @param Nodes
     * @return
     */
    public HashMap<Integer, Node> makeNodes(HashMap<String, String>[] Nodes){
        if(Nodes != null) {
            HashMap<Integer, Node> nodes = new HashMap<>();
            String[] keys;
            for (HashMap<String, String> node : Nodes) {
                keys = node.keySet().toArray(new String[0]);
                double x = 0;
                double y = 0;
                int id = 0;
                for (String key : keys) {
                    if (key.equals("pos")) {
                        String[] coordinates = node.get(key).split(",");
                        x = Double.parseDouble(coordinates[0]);
                        y = Double.parseDouble(coordinates[1]);
                    } else { // key == "id"
                        id = Integer.parseInt(node.get(key));
                    }
                }
                Node Node = new Node(x, y, id);
                nodes.put(id, Node);
            }
            return nodes;
        }
        return  null;
    }

    /**
     *
     * @param Edges
     * @param nodes
     * @return
     */
    public HashMap<Double, Edge> makeEdges(HashMap<String, String>[] Edges, HashMap<Integer, Node> nodes){
        if(Edges != null) {
            HashMap<Double, Edge> edges = new HashMap<>();
            String[] keys;
            for(HashMap<String, String> edge: Edges){
                keys = edge.keySet().toArray(new String[0]);
                int src = 0;
                int dest = 0;
                double weight = 0;
                for(String key: keys){
                    if(key.equals("src")){
                        src = Integer.parseInt(edge.get(key));
                    }
                    else if(key.equals("w")){
                        weight = Double.parseDouble(edge.get(key));
                    }
                    else{ // key == dest
                        dest = Integer.parseInt(edge.get(key));
                    }
                }
                Edge Edge = new Edge(src, dest, weight);
                double key = src + dest * MyGraph.BIGNUMBER;
                nodes.get(src).addEdge(Edge);
                edges.put(key, Edge);
            }
            return edges;
        }
        return null;
    }


    public HashMap<Integer, Node> getNodes() {
        return this.Nodes;
    }

    public HashMap<Double, Edge> getEdges() {
        return this.Edges;
    }

    public static void main(String[]args){
        ParseData pd = new ParseData("C:\\Users\\netan\\IdeaProjects\\Weighted_Graph_Algorithms\\data\\G1.json");
        System.out.println(pd.getEdges().values());
        System.out.println();
        System.out.println(pd.getNodes().values());
        DirectedWeightedGraph g = new MyGraph(pd.getNodes(), pd.getEdges());
        System.out.println();
    }
}


class Parser {
    private HashMap<String, String>[] Edges;
    private HashMap<String, String>[] Nodes;
    public Parser(){}
    public HashMap<String, String>[] getEdges() {
        return this.Edges;
    }
    public HashMap<String, String>[] getNodes() {
        return this.Nodes;
    }

}


