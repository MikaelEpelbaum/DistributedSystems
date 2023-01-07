import java.net.*;
import java.util.*;
import java.io.*;

public class Server {
    private ServerSocket ServerSocket;

    public Server(ServerSocket serverSocket){
        this.ServerSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (ServerSocket.isClosed()){
                Socket socket = ServerSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        catch (IOException e){

        }
    }
    public void closeServerSocket(){
        try{
            if (ServerSocket != null){
                ServerSocket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // maybe should be in the node?
    public static void main (String[] args) throws IOException{
        //needs to put our ports
        SeverSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(ServerSocket);
        server.startServer();
    }


}