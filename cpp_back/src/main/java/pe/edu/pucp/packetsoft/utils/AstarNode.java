package pe.edu.pucp.packetsoft.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AstarNode implements Comparable<AstarNode> {
    
    // Id for readability of result purposes
    private static int idCounter = 0;
    public int id;

    // Parent in the path
    public AstarNode parent = null;

    public List<Edge> neighbors;

    // Evaluation functions
    public double f = Double.MAX_VALUE;
    public double g = Double.MAX_VALUE;

    // heuristica en duro
    public double h; 

    AstarNode(double h){
        this.h = h;
        this.id = idCounter++;
        this.neighbors = new ArrayList<>();
    }

    @Override
    public int compareTo(AstarNode n) {
          return Double.compare(this.f, n.f);
    }

    public static class Edge {
        Edge(int weight, AstarNode node){
            this.weight = weight;
            this.node = node;
        }

        public int weight;
        public AstarNode node;
    }

    public void addBranch(int weight, AstarNode node){
        Edge newEdge = new Edge(weight, node);
        neighbors.add(newEdge);
    }

    public double calculateHeuristic(AstarNode target){

        // modificar heuristica
        return this.h;
    }
}
