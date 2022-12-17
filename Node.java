import java.util.*;

public class Node extends Thread {
    private int id;
    private ArrayList<Pair> neighbors;
    private int num_of_nodes;
    private double[][] matrix;

    /**
     * Constructor
     * @param num_of_nodes
     * @param line
     * initialize num_of_nodes and neighbors matrix
     * calling update_matrix function
     */
    public Node(int num_of_nodes, String line){
        this.num_of_nodes = num_of_nodes;
        matrix = new double[num_of_nodes][num_of_nodes];
        for(int i = 0; i < num_of_nodes; i++){
            for (int j = 0; j < num_of_nodes; j++){
                matrix[i][j] = -1;
            }
        }
        update_node(line);
    }
    public int get_id(){ return this.id; }

    /**
     *
     * @param line
     * Creates Pairs object
     * updates neighbor list with new neighbor
     * updates neighbors matrix of node
     */
    public void update_node(String line){
        String[] orders = line.split(" ");
        neighbors = new ArrayList<>((orders.length -1)/4);
        this.id  = Integer.parseInt(orders[0]);
        for(int i = 1; i < orders.length; i+=4){
            Map<String, Double> neighbor_data = new HashMap<>();
            neighbor_data.put("weight", Double.parseDouble(orders[i+1]));
            neighbor_data.put("port_origin", Double.parseDouble(orders[i+2]));
            neighbor_data.put("port_dest", Double.parseDouble(orders[i+3]));

            Pair<Integer, Map> edge = new Pair<>(Integer.parseInt(orders[i]), neighbor_data);
            neighbors.add(edge);
            update_matrix(edge);
        }
    }
// 4 1 8.9 13821 6060 2 1.0 17757 28236 5 1.5 1603 24233 3 6.6 27781 1213
//    key: 1
//    value: dictionary = {weight: 8.9,
//                        port_origin: 13821,
//                        port_dest: 6060}

    /**
     *
     * @param edge
     * updates neighbor edge values in neighbors matrix
     */
//    todo: arrange matrix indexes from 1 instead of 0
    public void update_matrix(Pair<Integer, Map> edge){
        Map<String, Double> temp = edge.getValue();
        this.matrix[this.id][edge.getKey()] = temp.get("weight");
    }

    public void print_graph(){

    }

}
