package api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Algorithms implements DirectedWeightedGraphAlgorithms {
    private DirectedWeightedGraph graph;
    private double minWeight = Double.MAX_VALUE;
    private List<NodeData> path;
    private int curr_src = -1;
    private int curr_dest = -1;
    private int connected =-1;//-1 unknown, 0 no, 1 yes
    private int mc;

    /**
     * Choose the graph you will perform your algorithms on
     */
    @Override
    public void init(DirectedWeightedGraph g) {
        graph = g;
        mc=graph.getMC();
    }

    /**
     * Returns the graph you are working on
     * @return
     */
    @Override
    public DirectedWeightedGraph getGraph() {
        return graph;
    }

    /**
     * Returns a deep copy of the current graph you are working on
     * @return
     */
    @Override
    public DirectedWeightedGraph copy() {
        return new MyGraph(graph);
    }

    @Override
    /** Checks if there is a route between every pair of vertexes
     * source https://www.techiedelight.com/check-given-graph-strongly-connected-not/
     * @return true if so false otherwise.
     */
    public boolean isConnected() {
        if(mc==graph.getMC() && connected!=-1)
        {if(connected==1)
                return true;
            else
                return false;
        }
        mc= graph.getMC();
        boolean con = isStronglyConnected();
        if(con)
            connected = 1;
        else
            connected = 0;
        return con;
    }

    /**
     * This is a simple DFS algorithm on the graph
     * @param key Starting vertex
     * @param visited hashmap of the visitor vertexes
     * @param gra on which graph to perform it
     */
    private void DFS(Queue<Integer> keys,int key, HashSet<Integer> visited, DirectedWeightedGraph gra) {
        // mark current node as visited
        while(!keys.isEmpty() && visited.size()!=gra.nodeSize())
        {
            visit(keys,keys.poll(),visited,gra);
        }
    }
    private void visit(Queue keys,int key, HashSet<Integer> visited, DirectedWeightedGraph gra){
        visited.add(key);
        Iterator<EdgeData> iter = gra.edgeIter(key);
        // do for every edge (v, u)
        while (iter.hasNext()) {
            int dest = iter.next().getDest();
            if (!visited.contains(dest)) {
                keys.add(dest);
            }
        }
    }

    /**
     * This function switch the source and destination of all the edges and returns a new graph.
     * @return
     */
    private DirectedWeightedGraph reverse() {
        HashMap<Integer, Node> Nodes = new HashMap<Integer, Node>();
        HashMap<String, Edge> Edges = new HashMap<String, Edge>();
        Iterator<NodeData> iterNodes = graph.nodeIter();
        while (iterNodes.hasNext()) {
            NodeData n = iterNodes.next();
            Nodes.put(n.getKey(), new Node(n.getLocation().x(), n.getLocation().y(), n.getKey()));
        }
        Iterator<EdgeData> iterEdges = graph.edgeIter();
        while (iterEdges.hasNext()) {

            EdgeData e = iterEdges.next();
            Edge en = new Edge(e.getDest(), e.getSrc(), e.getWeight());
            String key = e.getDest() + "-" + e.getSrc();
            Nodes.get(e.getDest()).getEdges().put(key, en);
            Edges.put(key, en);
        }
        return new MyGraph(Nodes, Edges);

    }

    /** Checks if there is a route between every pair of vertexes.
     *
     * @return
     */
    private boolean isStronglyConnected() {
        HashSet<Integer> visited = new HashSet<>();
        Queue<Integer> keys = new LinkedList<Integer>();
        int key = -1;
        Iterator<NodeData> iter = graph.nodeIter();
        if (iter.hasNext()) {
            key = iter.next().getKey();
        }
        if (key == -1)
            return false;
        keys.add(key);
        // run a DFS starting at `v`
        this.DFS(keys,key, visited, graph);

        // If DFS traversal doesn't visit all vertices,
        // then the graph is not strongly connected
        if(visited.size()!=graph.nodeSize())
            return false;
        visited = new HashSet<>();

        // Reverse the direction of all edges in the directed graph
        DirectedWeightedGraph g = this.reverse();
        // create a graph from reversed edges

        // Again run a DFS starting at `v`
        keys = new LinkedList<Integer>();
        keys.add(key);
        DFS(keys,key, visited, g);

        // If DFS traversal doesn't visit all vertices,
        // then the graph is not strongly connected
        if(visited.size()!=graph.nodeSize())
            return false;
        // if a graph "passes" both DFSs, it is strongly connected
        return true;
    }


    @Override
    public double shortestPathDist(int src, int dest) {
        /*if(this.curr_src == src && this.curr_dest == dest){
            return this.minWeight;
        }
        findShortestPath(src, dest);
        return this.minWeight;*/
        return djikstra(src,dest);
    }

    @Override
    public List<NodeData> shortestPath(int src, int dest) {
        if(this.curr_src == src && this.curr_dest == dest){
            return this.path;
        }
        findShortestPath(src, dest);
        return this.path;
    }

    /**
     * This method finds the shortest path between @src and @dest.<p>
     * The shortest path is the path that has the minimum total weight from all other paths if there are.<p>
     * The idea of this method is based on <b>Dijkstra Algorithm</b> (which uses BFS Algorithm).<p>
     * The method is implemented with a PriorityQueue and the <b>key of the priority</b> is the weights of the
     * Nodes by ascending order.<p>
     * <b>Steps of the Algorithm:</b><ul>
     * 1) initialize the PriorityQueue and the priority.<p>
     * 2) add the @src Node to the queue.<p>
     * 3) while the queue is not empty:<p>
     * 4) poll() - remove the first Node and keep it in a temporary Node @currNode.<p>
     * 5) if @currNode isn't null and @currNode has children and @currNode is "WHITE":<p>
     * 5.1) call the updateNodes(nodes, currNode, children) method.
     *      <li> @nodes = the Priority queue
     *      <li> @currNode = the Node that is the father.
     *      <li> @an array of all the children that @currNode has (as a keys of the Edges HashMap of @currNode).<p>
     * 5.2) set the color of @currNode to be black -> so we won't add him again to the queue.<p>
     * 6) go back to step <b>3</b>.<p>
     * 7) check if we found a path.<p>
     * 8) if found, load it to @this.path and set @this.minWeight to be the desired weight.<p>
     * 9) call resetWeightsInfoFather() to resets weights/Info/Father.<p>
     * @param src - the source Node that we start from.
     * @param dest - the destination Node that we want to reach.
     * @see   <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm">Dijkstra Algorithm</a>
     * @see   <a href="https://en.wikipedia.org/wiki/Breadth-first_search">Breadth-first search(BFS) Algorithm</a>
     */
    private void findShortestPath(int src, int dest){
        this.path = new ArrayList<>();
        this.minWeight = Double.MAX_VALUE;
        this.curr_dest = dest;
        this.curr_src = src;
        PriorityQueue<NodeData> nodes = new PriorityQueue<>((a,b)-> (int) (a.getWeight() - b.getWeight()));
        Iterator<NodeData> iter = graph.nodeIter();
        while (iter.hasNext()) {
            NodeData node = iter.next();
            if(node.getKey() != src){
                node.setWeight(Double.MAX_VALUE);
            }
            else{
                node.setWeight(0);
                nodes.add(node);
            }
            node.setInfo("WHITE");
        }
        //STEPS:
        // 1 -> while is not empty
        while(!nodes.isEmpty()){
            // 2 -> go to the first node, the one with the least weight
            Node currNode = (Node) nodes.poll();
            // 3 -> go over all his children(through Edges)
            if(currNode != null && currNode.getEdges() != null && currNode.getInfo().equals("WHITE")) {
                String[] children = currNode.getEdges().keySet().toArray(new String[0]);
                // 4 -> update their info and the weight if needed.
                updateNodes(nodes, currNode, children);
                // 5 -> set node to be black
                currNode.setInfo("BLACK");
            }
            // 6 -> go overt to 1.
        }
        // check if we found a path.
        if(this.graph.getNode(dest) == null || this.graph.getNode(dest).getWeight() == Double.MAX_VALUE){
            this.minWeight = -1;
        }
        else { // load the shortest path.
            this.path = new ArrayList<>();
            this.path.add(this.graph.getNode(dest));
            while (this.path.get(0).getKey() != src) {
                NodeData currNode = this.path.get(0);
                this.path.add(0, this.graph.getNode(currNode.getTag()));
            }
            this.minWeight = this.graph.getNode(dest).getWeight();
        }
        resetWeightsInfoFather();
    }



    /**
     * This method is being called by findShortestPath each time findShortestPath pops out a Node.<p>
     * The purpose of this method is to update the weights of all the children Nodes and to update their father pointer.<p>
     * It iterates through all the @keys(children) and updates only the Nodes that the update of the weight will
     * be smaller than the original weight.<p>
     * It updates the weight and the father of a Node.
     * @param nodes - the Priority Queue that contains all the nodes that needs to be checked.
     * @param parent - the Node that is a father of all the nodes in @keys.
     * @param keys - an array of keys(Double) represents all the children of the @parent.
     */
    private void updateNodes(PriorityQueue<NodeData> nodes, Node parent, String[] keys){
        double parentWeight = parent.getWeight();
        for(String k: keys){
            EdgeData currEdge = parent.getEdges().get(k);
            double edgeWeight = currEdge.getWeight();
            int dest = currEdge.getDest();
            NodeData child = this.graph.getNode(dest);
            double childWeight = child.getWeight();
            if(parentWeight + edgeWeight < childWeight){
                child.setWeight(parentWeight + edgeWeight);
                setFather(child, parent.getId());
            }
            nodes.add(child);
        }
    }


    /**
     * This method recevies NodaData @node and int @father and sets the Tag of @node to be @father.
     * @param node - The NodeData that's need to be set.
     * @param father - the id of the NodeData that represents the father.
     */
    private void setFather(NodeData node, int father){
        node.setTag(father);
    }


    /**
     * This method is activated after the ShortestPath method is finished.<p>
     * It iterates through all the nodes of the graph and resets the data that ShortestPath changed.<p>
     * The method resets:
     * <li>The Weight of each node to -> very big number.
     * <li>The Info of each node to -> "".
     * <li>The father of each node to -> -1.
     */
    private void resetWeightsInfoFather(){
        Iterator<NodeData> iter = this.graph.nodeIter();
        while (iter.hasNext()) {
            NodeData node = iter.next();
            node.setWeight(Double.MAX_VALUE);
            node.setInfo("");
            setFather(node, -1);
        }
    }


    @Override
    public NodeData center() {
            Iterator<NodeData> iter = graph.nodeIter();
            double max = Double.MAX_VALUE;
            int id = 0;
            while (iter.hasNext())
            {
                int key = iter.next().getKey();
                double weight = djikstra(key);
                if(weight<max) {
                    max = weight;
                    id=key;
                }
            }
            return graph.getNode(id);

    }
    public class trio {
        int from;
        int to;
        double weight;

        public trio(int f,int t, double w){
            from=f;
            to=t;
            weight=w;
        }

    }
    public class father {
        int prev;
        double weight;

        public father(int t, double w){
            prev=t;
            weight=w;
        }

    }

    public double djikstra(int src){
        HashSet<Integer> g =new HashSet<>();
        PriorityQueue<father> prio = new PriorityQueue<>((o1, o2) -> {
            if (o1.weight == o2.weight)
                return 0;
            else if (o1.weight > o2.weight)
                return 1;
            else
                return -1;
        });
        prio.add(new father(src,0));
        double max =0;
        int count=0;
        Iterator<NodeData> iter1 = graph.nodeIter();
        while (iter1.hasNext())
            iter1.next().setTag(0);
        while (g.size()<graph.nodeSize() && !prio.isEmpty()) {
            father f = prio.poll();
            int dest = f.prev;
//            NodeData d = graph.getNode(dest);
//            if (d.getTag() != 1) {
            if(!g.contains(dest)){
                Iterator<EdgeData> iter = graph.edgeIter(dest);
                g.add(dest);
//                d.setTag(1);
                if(f.weight> max){
                    max=f.weight;
                }
                while (iter.hasNext()) {
                    EdgeData e = iter.next();
                    double weight =f.weight+ e.getWeight();
                    prio.add(new father(e.getDest(),weight));
                }
            }
        }
        return max;
    }


    public double djikstra(int src,int des) {
        HashMap<Integer, father> s = new HashMap<>();
//        HashSet<Integer> s = new HashSet<>();
        PriorityQueue<trio> prio = new PriorityQueue<>((o1, o2) -> {
            if (o1.weight == o2.weight)
                return 0;
            else if (o1.weight > o2.weight)
                return 1;
            else
                return -1;
        });
        prio.add(new trio(src, src, 0));
//        while (iter1.hasNext())
//            iter1.next().setTag(0);
        while (s.size() < graph.nodeSize() && !prio.isEmpty()) {
            trio t = prio.poll();
            int dest = t.to;
//            NodeData d = graph.getNode(dest);
//            if (d.getTag() != 1) {
            if (!s.containsKey(dest)) {
                Iterator<EdgeData> iter = graph.edgeIter(dest);
//                s.add(dest);
//                graph.getNode(dest).setTag(1);
                s.put(dest, new father(src, t.weight));
                if (s.containsKey(des)) {
                    return t.weight;
                }
                double weight = t.weight;
                while (iter.hasNext()) {
                    EdgeData e = iter.next();
                    weight += e.getWeight();
                    prio.add(new trio(e.getSrc(), e.getDest(), weight));
                }
            }
        }
        return -1;
    }







    @Override
    public List<NodeData> tsp(List<NodeData> cities) {
        return null;
    }

    /**
     * Save the current graph to a file
     * Used https://www.w3schools.com/java/java_files_create.asp
     * @param file - the file name (may include a relative path).
     * @return
     */
    @Override
    public boolean save(String file) {
        try {
            File myObj = new File(file);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }
        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(graph.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean load(String file) {
        boolean hasLoaded = false;
        try{
            ParseToGraph ptg = new ParseToGraph(file);
            this.graph = new MyGraph(ptg.getNodes(), ptg.getEdges());
            hasLoaded = true;
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            System.out.println("\nJson file wasn't found!");
        }
        return hasLoaded;
    }

    public static void main(String[]args){
        ParseToGraph pd = new ParseToGraph();
        try {
            pd = new ParseToGraph("C:\\Users\\yanir\\IdeaProjects\\Weighted_Graph_Algorithms\\data\\Test1.json");
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            System.out.println();
        }
        DirectedWeightedGraph g = new MyGraph(pd.getNodes(), pd.getEdges());
        DirectedWeightedGraphAlgorithms algo = new Algorithms();
        algo.init(g);
        long b = System.currentTimeMillis();
        System.out.println(algo.center());
        System.out.println(System.currentTimeMillis()-b);
//        String f = g.toString();
//        algo.save("test");
//        algo.load("test");
//        String f1 = g.toString();
//        System.out.println(f.equals(f1));
//        long a = System.currentTimeMillis();
//        System.out.println(algo.center());
//        System.out.println(System.currentTimeMillis()-a);
////        algo.init(g);
////        System.out.println(g.toStringEdges());
//       // System.out.println(algo.center().toString());
////        System.out.println(g.toStringNodes());
////        System.out.println(algo.shortestPathDist(1,7));
////        System.out.println(algo.shortestPath(1,7));
////        System.out.println(algo.isConnected());
//        long a = System.currentTimeMillis();
//        System.out.println(algo.isConnected());
//        long b = System.currentTimeMillis();
//        System.out.println(b-a);
//        algo.getGraph().addNode(new Node(500,500,500));
//        a=System.currentTimeMillis();
//        System.out.println(algo.isConnected());
//        b=System.currentTimeMillis();
//        System.out.println(b-a);
    }
}
