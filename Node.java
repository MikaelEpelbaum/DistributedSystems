import java.net.*;
import java.util.*;
import java.io.*;

public class Node {
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

    public void sendMessage(){
//      broadcast to neighbor everything you get beside if its from them (transfer)
//      lv = <[origin, destination], value matrix of origin>
        Pair<Integer[], double[]> lv = new Pair<>(new Integer[]{id, 0}, matrix[id-1]);
//      for every neighbor send my lv
        for (int j = 0; j < neighbors.size(); j++){
            Map<String, Double> neighbor = (Map<String, Double>) neighbors.get(j).getValue();
            int port_destination = neighbor.get("port_dest").intValue();
            Integer destination_id = ((Integer) neighbors.get(j).getKey()).intValue();
            lv.setKey(new Integer[]{lv.getKey()[0], destination_id});
            try {
                Socket socket = new Socket("localhost", port_destination);
                Client client = new Client(socket, lv, id);
                client.sendMessage();
            } catch (IOException e) {
            }
        }
    }


//    @Override
//    public void run(){
////      broadcast to neighbor everything you get beside if its from them (transfer)
//
//        Pair<Integer, double[]> lv = new Pair<>(id, matrix[id-1]);
//
////      for every neighbor send my lv
//        for (int j = 0; j < neighbors.size(); j++){
//            Map<String, Double> neighbor = (Map<String, Double>) neighbors.get(j).getValue();
//            int port_destination = neighbor.get("port_dest").intValue();
//            try {
//                Socket socket = new Socket("localhost", port_destination);
//                Client client = new Client(socket, lv, id);
//                client.sendMessage();
////                new Thread(client).start();
//            } catch (IOException e) {
//            }
//        }

//        Map<Integer, Boolean> updated = new HashMap<>();
//        for (int i = 0; i < num_of_nodes; i++)
//            updated.put(i, false);
//        updated.put(id-1, true);
//
//        for (int j = 0; j < neighbors.size(); j++){
//            Map<String, Double> neighbor = (Map<String, Double>) neighbors.get(j).getValue();
//            int port_destination = neighbor.get("port_dest").intValue();
//            try {
//                Socket socket = new Socket("localhost", port_destination);
//                Client client = new Client(socket, lv, id);
//                new Thread(client).start();
////                client.sendMessage();
//                client.listenToMessage();
////                while (client.lv_to_return == null){
////                }
////                System.out.println(client.lv_to_return);
//            } catch (IOException e) {
//            }
//        }
//
////      sends my lv to all my neighbors
//        for (int i = 0; i < neighbors.size(); i++) {
//            Map<String, Double> neighbor =  (Map<String, Double>)neighbors.get(i).getValue();
////            send_data(neighbor, lv);
//        }

//
//        while (updated.containsValue(false)) {
//            for (int i = 0; i < neighbors.size(); i++) {
//                Map<String, Double> neighbor =  (Map<String, Double>)neighbors.get(i).getValue();
//                int port_origin = neighbor.get("port_origin").intValue();
//                Pair<Integer, double[]> neighbor_update = new Pair<>(-1, new double[0]);
//                try {
//                    Socket client = new Socket("localhost", port_origin);
//                    InputStream inputToServer = client.getInputStream();
//                    ObjectInputStream input = new ObjectInputStream(inputToServer);
//                    neighbor_update = (Pair<Integer, double[]>)input.readObject();
//                    client.close();
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//
////              todo: updates updates vector, matrix, broadcast to other neighbor what we got
//                int neighbor_id = neighbor_update.getKey();
//                if (neighbor_id == -1 || updated.get(neighbor_id))
//                    continue;
//                else {
//                    updated.put(neighbor_id, true);
//                    this.matrix[neighbor_id-1] = neighbor_update.getValue();
//                    for (int j = 0; j < neighbors.size(); j++){
////                      we send to the other neighbors the lv from the one we just got
//                        if (i != j){
//                            Map<String, Double> neighbor_to_broadcast =  (Map<String, Double>)neighbors.get(i).getValue();
////                            send_data(neighbor_to_broadcast, neighbor_update);
//                        }
//                    }
//                }
//            }
//        }
////        returns true because finished all updates
////        return true;
//    }

//    todo: how should the printing graph look like? only relevant vector or all matrix?
    public void print_graph(){

    }

}
