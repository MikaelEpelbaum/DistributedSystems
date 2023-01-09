import java.io.*;
import java.net.*;
import java.util.*;

public class ExManager {
    private String path;
    private int num_of_nodes;
    private Scanner initial_data;
    private Node[] network;
    private Server[] servers;

    public ExManager(String path) throws FileNotFoundException {
        this.path = path;
        initial_data = new Scanner(new File(path));
    }

    //    public Node getNode(int id){return network[id];}
    public Node get_node(int i) {
        return network[i - 1];
    }

    public int getNum_of_nodes() {
        return this.num_of_nodes;
    }

    public void update_edge(int id1, int id2, double weight) {
        network[id1 - 1].edge_update(id2, weight);
        network[id2 - 1].edge_update(id1, weight);
    }

    public void read_txt() {
        num_of_nodes = Integer.parseInt(initial_data.nextLine());
        this.network = new Node[num_of_nodes];
        this.servers = new Server[2*num_of_nodes];
        String line;
        while (initial_data.hasNextLine()) {
            line = initial_data.nextLine();
            if (line.equals("stop"))
                break;
            Node n = new Node(num_of_nodes, line);
            network[n.get_id() - 1] = n;
        }
    }

//  link state routing
    public void start() {
//      creating and lunching a server for every edge connection
        int cnt = 0;
        for (int i = 0; i < network.length; i++) {
            ArrayList<Pair> neighbors = (ArrayList<Pair>) network[i].neighbors;
            for (int j = 0; j < neighbors.size(); j++) {
                Pair<Integer, Map> edge = (Pair<Integer, Map>) neighbors.get(j);
                Double port_origin = (Double) edge.getValue().get("port_origin");
                try {
                    ServerSocket serverSocket = new ServerSocket(port_origin.intValue());
                    Server server = new Server(serverSocket, network[i].get_id());

                    servers[cnt] = server;
                    cnt++;

                    Thread thread = new Thread(server);
                    thread.start();
                } catch (IOException e) {
                }
            }
        }

//      nodes send lv
        for (int i = 0; i < network.length; i++) {
            network[i].sendMessage();
        }

        cnt = 0;
        for (int i = 0; i < network.length; i++) {
            ArrayList<Pair> neighbors = (ArrayList<Pair>) network[i].neighbors;
            for (int j = 0; j < neighbors.size(); j++) {
                Pair<Integer[], double[]> lv = servers[cnt].getValue();
                cnt++;
                try{
                    String str = "";
                    for (int k = 0; k < lv.getValue().length; k++)
                        str +=  String.valueOf(lv.getValue()[k]) + ", ";
//                    System.out.println(lv.getKey().intValue()+ " :" + str);

                } catch (NullPointerException e){
//                    System.out.println(lv.getKey().intValue());
                }
            }
        }
    }
}
