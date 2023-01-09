import java.net.*;
import java.io.*;

public class Server  extends Thread{
    private ServerSocket serverSocket;
    private int id;
    public Pair<Integer[], double[]>  lv;

    public Server(ServerSocket serverSocket, int id){
        this.serverSocket = serverSocket;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, id);

                Thread thread = new Thread(clientHandler);
                thread.start();
                thread.join();
                lv = clientHandler.getValue();
//                serverSocket.close();
            }
        } catch (IOException | InterruptedException e) {
        }
    }
    public Pair<Integer[], double[]> getValue(){ return lv;}
}