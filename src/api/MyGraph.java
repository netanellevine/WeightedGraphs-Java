package api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyGraph implements DirectedWeightedGraph {
    public static double BIGNUMBER = (double) Integer.MAX_VALUE * Integer.MAX_VALUE + 1.0;
    private final HashMap<Integer, Node> nodes;
    private final HashMap<Double, Edge> edges;
    private int MC;

    public MyGraph(HashMap<Integer, Node> Nodes, HashMap<Double, Edge> Edges) {
        this.nodes = Nodes;
        this.edges = Edges;
        this.MC = 0;
    }

    @Override
    public NodeData getNode(int key) {
        return nodes.get(key);
    }

    // src+dest*bignumber
    // src = (src+dest*bignumber)%x
    // dest = (src+dest*bignumber)/x
    @Override
    public EdgeData getEdge(int src, int dest) {
        return edges.get(src + dest * BIGNUMBER);
    }

    @Override
    public void connect(int src, int dest, double w) {
        double key = src + dest * BIGNUMBER;
        if (!this.edges.containsKey(key)) {
            this.edges.put(key, new Edge(src, dest, w));
            this.MC++;
        }
    }

    @Override
    public Iterator<NodeData> nodeIter() {

        Iterator<NodeData> iter= new Iterator<NodeData>() {
            Iterator<Node> it = nodes.values().iterator();
            int m = MC;
            @Override
            public boolean hasNext() {
                if(m!=MC)
                    throw new RuntimeException("MC counter was changed");
                return it.hasNext();
            }

            @Override
            public NodeData next() {
                if(m!=MC)
                    throw new RuntimeException("MC counter was changed");
                return it.next();
            }

            @Override
            public void remove() {
                if(m!=MC)
                    throw new RuntimeException("MC counter was changed");
                it.remove();
            }
        };
        return iter;
    }

    @Override
    public Iterator<EdgeData> edgeIter() {
        Iterator<EdgeData> iter= new Iterator<EdgeData>() {
            Iterator<Edge> it = edges.values().iterator();
            int m = MC;
            @Override
            public boolean hasNext() {
                if(m!=MC)
                    throw new RuntimeException("MC counter was changed");
                return it.hasNext();
            }

            @Override
            public EdgeData next() {
                if(m!=MC)
                    throw new RuntimeException("MC counter was changed");
                return it.next();
            }

            @Override
            public void remove() {
                if(m!=MC)
                    throw new RuntimeException("MC counter was changed");
                it.remove();
            }
        };
        return iter;
    }

    @Override
    public Iterator<EdgeData> edgeIter(int node_id) {
        Node x = nodes.get(node_id);
        HashMap<Double, Edge> E = x.getEdges();
        Iterator<EdgeData> iter= new Iterator<EdgeData>() {

            Iterator<Edge> it = E.values().iterator();
            int m = MC;

            @Override
            public boolean hasNext() {
                if(m!=MC)
                    throw new RuntimeException("MC counter was changed");
                return it.hasNext();
            }

            @Override
            public EdgeData next() {
                if(m!=MC)
                    throw new RuntimeException("MC counter was changed");
                return it.next();
            }

            @Override
            public void remove() {
                if(m!=MC)
                    throw new RuntimeException("MC counter was changed");
                it.remove();
            }
        };
        return iter;
    }



    //MC++
    @Override
    public NodeData removeNode(int key) {
        removeRelatedEdges(this.nodes.get(key));
        return this.nodes.remove(key);
    }

    private void removeRelatedEdges(Node n) {
        for (double i : n.getEdges().keySet()) {
            edges.remove(i);
        }
    }

    @Override
    public EdgeData removeEdge(int src, int dest) {
        double key = src + dest * BIGNUMBER;
        this.nodes.get(src).removeEdge(key);
        return this.edges.remove(key);
    }

    @Override
    public int nodeSize() {
        return nodes.size();
    }

    @Override
    public int edgeSize() {
        return edges.size();
    }

    @Override
    public int getMC() {
        return this.MC;
    }

    @Override
    public void addNode(NodeData n) {
    }

    public static void main(String[] args) {
        System.out.println(BIGNUMBER);

    }
}