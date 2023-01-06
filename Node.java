import java.sql.Struct;
import java.util.*;
import java.net.*;
import java.util.*;
import java.io.*;

public class Node extends Thread {
    private int id;
    public ArrayList<Pair> neighbors;
    public int num_of_nodes;
    public double[][] matrix;

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
        neighbors_initialize(line);
    }
    public int get_id(){ return this.id; }

    /**
     *
     * @param line
     * Creates Pairs object
     * updates neighbor list with new neighbor
     * updates neighbors matrix of node
     */
    public void neighbors_initialize(String line){
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
    public void update_matrix(Pair<Integer, Map> edge){
        Map<String, Double> temp = edge.getValue();
        this.matrix[this.id-1][edge.getKey()-1] = temp.get("weight");
    }

    public void edge_update(int id, double weight){
        Integer temp = id;
        for(int i = 0; i < neighbors.size(); i++){
            if (neighbors.get(i).getKey() == temp){
                Map<String, Double> map = (Map<String, Double>) neighbors.get(i).getValue();
                map.put("weight", weight);
                update_matrix(neighbors.get(i));
            }
        }
    }

    @Override
    public void run(){
        Pair<Integer, double[]> lv = new Pair<>(id, matrix[id-1]);

        Map<Integer, Boolean> updated = new HashMap<>();
        for (int i = 0; i < num_of_nodes; i++)
            updated.put(i, false);
        updated.put(id-1, true);


//      sends my lv to all my neighbors
        for (int i = 0; i < neighbors.size(); i++) {
            Map<String, Double> neighbor =  (Map<String, Double>)neighbors.get(i).getValue();
            send_data(neighbor, lv);
        }

        while (updated.containsValue(false)) {
            for (int i = 0; i < neighbors.size(); i++) {
                Map<String, Double> neighbor =  (Map<String, Double>)neighbors.get(i).getValue();
                int port_origin = neighbor.get("port_origin").intValue();
                Pair<Integer, double[]> neighbor_update = new Pair<>(-1, new double[0]);
                try {
                    Socket client = new Socket("localhost", port_origin);
                    InputStream inputToServer = client.getInputStream();
                    ObjectInputStream input = new ObjectInputStream(inputToServer);
                    neighbor_update = (Pair<Integer, double[]>)input.readObject();
                    client.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

//              todo: updates updates vector, matrix, broadcast to other neighbor what we got
                int neighbor_id = neighbor_update.getKey();
                if (neighbor_id == -1 || updated.get(neighbor_id))
                    continue;
                else {
                    updated.put(neighbor_id, true);
                    this.matrix[neighbor_id-1] = neighbor_update.getValue();
                    for (int j = 0; j < neighbors.size(); j++){
//                      we send to the other neighbors the lv from the one we just got
                        if (i != j){
                            Map<String, Double> neighbor_to_broadcast =  (Map<String, Double>)neighbors.get(i).getValue();
                            send_data(neighbor_to_broadcast, neighbor_update);
                        }
                    }
                }
            }
        }
//        returns true because finished all updates
//        return true;
    }


    public void send_data(Map<String, Double> neighbor, Pair<Integer, double[]> lv){
        int port_dest = neighbor.get("port_dest").intValue();
        try {
            Socket client = new Socket("localhost", port_dest);
            OutputStream outToServer = client.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToServer);
            out.writeObject(lv);
            client.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

//    todo: how should the printing graph look like? only relevant vector or all matrix?
    public void print_graph(){

    }

}
