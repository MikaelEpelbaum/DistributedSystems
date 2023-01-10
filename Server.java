import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server  extends Thread{
    private ServerSocket serverSocket;
    public Pair<Integer[], Double[]>  lv;
    private int id;

    public Server(ServerSocket serverSocket, int id){
        this.serverSocket = serverSocket;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
                lv = clientHandler.get_lv_recieved();
//                serverSocket.close();
            }
        } catch (IOException e) {
        }
    }

    public void sendMessage(Pair<Integer[], Double[]> lv_to_send){
        try{
            Socket socket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socket);
            clientHandler.sendMessage(lv_to_send);
        } catch (IOException e){}
    }

}