import java.net.*;
import java.util.*;
import java.io.*;

public class ClientHandler implements Runnable {
    // keep track of all our clients
    //broadcast a message to all of our clients and not just one
    public static ArrayList <ClientHandler> clientHandlers = new ArrayList<>();
    //socket that has passed from our server class
    private Socket socket;
    //used to read date - massages that came from the client
    private BufferedReader bufferReader;
    //use to sent data to the client - messages that came from other clients
    private BufferedWriter bufferWriter;
    //private String clientUserName;

    //constactror
    public ClientHandler (Socket socket){
        try{
            this.socket = socket;
            //use to send things
            this.bufferWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
            //use to read things
            this.bufferReader = new BufferedReader (new InputStreamReader(socket.getInputStream()));

        }
        catch (IOException e){

            closeEverything(socket, bufferReader, bufferWriter);
        }
    }

    @Override
    public void run(){
        String messageFromClient;

        while (socket.isConnected()){
            try{
                messageFromClient = bufferReader.readLine();
                broadCastMessage(messageFromClient);
            }
            catch (IOException e){
                closeEverything(socket, bufferReader, bufferWriter);
                break;
            }
        }
    }
}