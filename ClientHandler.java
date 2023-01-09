import java.net.*;
import java.util.*;
import java.io.*;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private int id;
    public Pair<Integer[], double[]> gotten_lv;

    public ClientHandler(Socket socket, int id){
        this.id = id;
        try{
            this.socket = socket;
            OutputStream outputStream = socket.getOutputStream();
            this.objectOutputStream = new ObjectOutputStream(outputStream);
            InputStream inputStream = socket.getInputStream();
            this.objectInputStream = new ObjectInputStream(inputStream);
            clientHandlers.add(this);
        }catch (IOException e){
            closeEverything();
        }

    }

    @Override
    public void run(){
        Pair<Integer[], double[]> lv;
        while(socket.isConnected()){
            try {
                lv = (Pair<Integer[], double[]>) objectInputStream.readObject();
                gotten_lv = lv;
                String str = "";
                for (int k = 0; k < lv.getValue().length; k++)
                    str +=  String.valueOf(lv.getValue()[k]) + ", ";
                System.out.println("Origin: " + lv.getKey()[0].intValue() + " Destination: " + lv.getKey()[1].intValue()+ ": " + str);

                socket.close();
//                broadcastMessage(lv);
            } catch(IOException | ClassNotFoundException e) {
                closeEverything();
                break;
            }
        }
    }

    public Pair<Integer[], double[]> getValue() {return gotten_lv;}

//    public void broadcastMessage(Pair<Integer, double[]> messageToSend){
//        for (ClientHandler clientHandler : clientHandlers){
//            try{
//                if (this.id != messageToSend.getKey().intValue()) {
//                    clientHandler.objectOutputStream.writeObject(messageToSend);
//                    clientHandler.objectOutputStream.flush();
//                }
//            } catch (IOException e){
//                closeEverything();
//            }
//        }
//    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
    }

    public void closeEverything(){
        removeClientHandler();
        try{
            if (this.objectInputStream != null)
                this.objectInputStream.close();
            if (this.objectOutputStream != null)
                this.objectOutputStream.close();
            if (this.socket != null)
                this.socket.close();
        } catch (IOException e) {
        }
    }
}
