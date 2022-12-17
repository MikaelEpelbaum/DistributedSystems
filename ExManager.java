import java.io.*;
import java.util.*;

public class ExManager {
    private String path;
    private int num_of_nodes;
    private Scanner initial_data;
    private Node[] network;

    public ExManager(String path) throws FileNotFoundException{
        this.path = path;
        initial_data = new Scanner(new File(path));
        read_txt();
    }

//  todo: which one of the functions to keep? weight for response
    public Node getNode(int id){
        return network[id];
    }
    public Node get_node(int i) {
        return network[i];
    }

    public int getNum_of_nodes() {
        return this.num_of_nodes;
    }

    public void update_edge(int id1, int id2, double weight){
        //your code here
    }

    public void read_txt() {
        num_of_nodes = Integer.parseInt(initial_data.nextLine());
        this.network = new Node[num_of_nodes];

        while (initial_data.hasNextLine()) {
            String line = initial_data.nextLine();
            if (line.equals("stop"))
                break;
            Node n = new Node(num_of_nodes, line);
            network[n.get_id()] = n;
        }
    }

    public void start(){
        // your code here
    }
}
