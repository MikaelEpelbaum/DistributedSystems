import java.io.*;
import java.net.*;
import java.util.*;

public class ExManager {
    private String path;
    private int num_of_nodes;
    private Scanner initial_data;
    private Node[] network;

    public ExManager(String path) throws FileNotFoundException{
        this.path = path;
        initial_data = new Scanner(new File(path));
    }

//  todo: which one of the functions to keep? wait for response

//    public Node getNode(int id){return network[id];}
    public Node get_node(int i) {
        return network[i];
    }

    public int getNum_of_nodes() {
        return this.num_of_nodes;
    }

    public void update_edge(int id1, int id2, double weight){
        network[id1-1].edge_update(id2, weight);
        network[id2-1].edge_update(id1, weight);
    }

    public void read_txt() {
        num_of_nodes = Integer.parseInt(initial_data.nextLine());
        this.network = new Node[num_of_nodes];
        String line;
        while (initial_data.hasNextLine()) {
             line = initial_data.nextLine();
            if (line.equals("stop"))
                break;
            Node n = new Node(num_of_nodes, line);
            network[n.get_id()-1] = n;
        }
    }



    public void start() throws IOException {
        ServerSocket listener = new ServerSocket("6060");
        Socket node = listener.accept();

        PrintWriter out = new PrintWriter(node.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(node.getInputStream()));

//        Map<Integer, Boolean> updated = new HashMap<>();
//        for (int i = 0; i < num_of_nodes; i++)
//            updated.put(i, false);

        for (int i = 0; i < num_of_nodes; i++)
            network[i].run();

        System.out.println("");
//        try {
//            if (updated.containsValue(false)) {
//                throw new Exception();
//            }
//        }
//        catch (Exception e){
//            System.out.println("problem");
//        }
    }
}
