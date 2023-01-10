import java.net.*;
import java.util.*;
import java.io.*;

public class Node extends Thread {
    private int id;
    public ArrayList<Pair> neighbors;
    public ArrayList<Pair> servers;
    public ArrayList<Pair> clients;
    public int num_of_nodes;
    public Double[][] matrix;

    /**
     * Constructor
     *
     * @param num_of_nodes
     * @param line         initialize num_of_nodes and neighbors matrix
     *                     calling update_matrix function
     */
    public Node(int num_of_nodes, String line) {
        this.num_of_nodes = num_of_nodes;
        matrix = new Double[num_of_nodes][num_of_nodes];
        for (int i = 0; i < num_of_nodes; i++) {
            for (int j = 0; j < num_of_nodes; j++) {
                matrix[i][j] = -1.0;
            }
        }
        neighbors_initialize(line);
        servers = new ArrayList<>(neighbors.size());
        clients = new ArrayList<>(neighbors.size());
        servers_initialize();
//        clients_initialize();
    }

    public int get_id() {
        return this.id;
    }

    /**
     * @param line Creates Pairs object
     *             updates neighbor list with new neighbor
     *             updates neighbors matrix of node
     */
    public void neighbors_initialize(String line) {
        String[] orders = line.split(" ");
        neighbors = new ArrayList<>((orders.length - 1) / 4);
        this.id = Integer.parseInt(orders[0]);
        for (int i = 1; i < orders.length; i += 4) {
            Map<String, Double> neighbor_data = new HashMap<>();
            neighbor_data.put("weight", Double.parseDouble(orders[i + 1]));
            neighbor_data.put("send", Double.parseDouble(orders[i + 2]));
            neighbor_data.put("listen", Double.parseDouble(orders[i + 3]));

            Pair<Integer, Map> edge = new Pair<>(Integer.parseInt(orders[i]), neighbor_data);
            neighbors.add(edge);
            update_matrix(edge);
        }
    }

    private void servers_initialize() {
        for(Pair<Integer, Map> neighbor : neighbors){
            try {
                int port = ((Double) neighbor.getValue().get("listen")).intValue();
                ServerSocket serverSocket = new ServerSocket(port);
//                System.out.println(id +" is setting:" + port+ " has its server");
                Server s = new Server(serverSocket, id);
                Thread t = new Thread(s);
                t.start();
                servers.add(new Pair(neighbor.getKey(), s));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void clients_initialize(){
        for(Pair<Integer, Map> neighbor : neighbors){
            try {
                int port = ((Double) neighbor.getValue().get("send")).intValue();
//                System.out.println(id +" is setting:" + port+ " has its client");
                Socket socket = new Socket("localhost", port);
                Client c = new Client(socket, id);
                Thread t = new Thread(c);
                t.start();
                clients.add(new Pair(neighbor.getKey(), c));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

// 4 1 8.9 13821 6060 2 1.0 17757 28236 5 1.5 1603 24233 3 6.6 27781 1213
//    key: 1
//    value: dictionary = {weight: 8.9,
//                        port_origin: 13821,
//                        port_dest: 6060}

    /**
     * @param edge updates neighbor edge values in neighbors matrix
     */
    public void update_matrix(Pair<Integer, Map> edge) {
        Map<String, Double> temp = edge.getValue();
        this.matrix[this.id - 1][edge.getKey() - 1] = temp.get("weight");
    }

    public void edge_update(int id, double weight) {
        Integer temp = id;
        for (int i = 0; i < neighbors.size(); i++) {
            if (neighbors.get(i).getKey() == temp) {
                Map<String, Double> map = (Map<String, Double>) neighbors.get(i).getValue();
                map.put("weight", weight);
                update_matrix(neighbors.get(i));
            }
        }
    }

    public void sendMessage(Pair<Integer[], Double[]> lv) {
//      broadcast message to neighbors (clients)
        for(Pair<Integer, Client> neighbor : clients){
            if (lv.getKey()[0].intValue() == neighbor.getKey().intValue())
                continue;
            lv.setKey(new Integer[]{lv.getKey()[0], neighbor.getKey()});
            neighbor.getValue().sendMessage(lv);
        }
    }

    public ArrayList<Pair> getMessages(){
        ArrayList<Pair> neighbors_lv = new ArrayList<>(neighbors.size());
        while (!neighbors_lv.contains(null)) {
            for (Pair<Integer, Client> c : clients) {
                Pair<Integer[], Double[]> lv = c.getValue().lv_to_return;
                if (lv != null) {
                    if (lv.getKey()[0].intValue() == id)
                        continue;
                    neighbors_lv.add(lv);
                }
            }
        }
        return neighbors_lv;
    }



    @Override
    public void run() {
        clients_initialize();
        Map<Integer, Boolean> updated = new HashMap<>();
        for (int i = 0; i < num_of_nodes+1; i++)
            updated.put(i, false);
        updated.put(id, true);
        updated.put(0, true);

        Pair<Integer[], Double[]> lv = new Pair<>(new Integer[]{id, 0}, matrix[id - 1]);
        sendMessage(lv);
//      ArrayList<Pair<Integer[], Double[]>> responses = getMessages();
        ArrayList<Pair> responses = getMessages();
        System.out.println();




////      lv = <[origin, destination], value matrix of origin>
//
////      LISTENING
//        Client[] listeningClients = new Client[neighbors.size()];
////      WRITING
//        Client[] writingClients = new Client[neighbors.size()];
//        for (int i = 0; i < neighbors.size(); i++) {
//            Map<String, Double> neighbor = (Map<String, Double>) neighbors.get(i).getValue();
//            int port_origin = neighbor.get("port_origin").intValue();
//            int port_dest = neighbor.get("port_dest").intValue();
//            try {
//                Socket socket1 = new Socket("localhost", port_origin);
//                Client c = new Client(socket1, id);
//                listeningClients[i] = c;
//                c.listenToMessage();
//                Socket socket2 = new Socket("localhost", port_dest);
//                Client c2 = new Client(socket2, id);
//                writingClients[i] = c2;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        Pair<Integer[], Double[]> lv = new Pair<>(new Integer[]{id, 0}, matrix[id - 1]);
//        Pair<Integer[], Double[]> input = sendMessage(lv, writingClients, listeningClients);
//
//        while (updated.containsValue(false)){
//            updated.put(input.getKey()[0].intValue(), true);
////          sends gotten lv to neighbors
//            for (int i = 0; i < neighbors.size(); i++) {
//                input = sendMessage(input, writingClients, listeningClients);
//            }
//        }


//        Pair<Integer[], double[]> lv = new Pair<>(new Integer[] {id, 0}, matrix[id-1]);
////
//////      for every neighbor send my lv
////        for (int j = 0; j < neighbors.size(); j++){
////            Map<String, Double> neighbor = (Map<String, Double>) neighbors.get(j).getValue();
////            int port_destination = neighbor.get("port_dest").intValue();
////            try {
////                Socket socket = new Socket("localhost", port_destination);
////                Client client = new Client(socket, lv, id);
////                client.sendMessage();
//////                new Thread(client).start();
////            } catch (IOException e) {
////            }
////        }
//

////
//


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
    }

//    todo: how should the printing graph look like? only relevant vector or all matrix?
        public void print_graph () {

        }

}
