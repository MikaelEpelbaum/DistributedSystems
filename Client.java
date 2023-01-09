import java.net.*;
import java.util.*;
import java.io.*;

public class Client extends Thread{

    private Socket socket;
    private Pair<Integer[], double[]> lv_to_send;
    public Pair<Integer, double[]> lv_to_return;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private int id;

    public Client(Socket socket, Pair<Integer[], double[]> lv, int id) throws IOException {
        this.socket = socket;
        this.lv_to_send = lv;
        this.lv_to_return = null;
        this.id = id;
        OutputStream outputStream = socket.getOutputStream();
        this.objectOutputStream = new ObjectOutputStream(outputStream);
    }
    public void sendMessage(){
        try {
            if(socket.isConnected() && objectOutputStream != null) {
                    objectOutputStream.writeObject(lv_to_send);
                    objectOutputStream.flush();
            }
        } catch (IOException e){
            closeEverything(socket, objectOutputStream, objectInputStream);
        }
    }

    public  void listenToMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = socket.getInputStream();
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    while (socket.isConnected() && objectInputStream != null) {
                        try {
                            lv_to_return = (Pair<Integer, double[]>) objectInputStream.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            closeEverything(socket, objectOutputStream, objectInputStream);
                        }
                    }
                } catch (IOException e) {}
            }
        }).start();
    }

private void closeEverything(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
    try {
        if (socket != null) {
            socket.close();
        }
        if (objectOutputStream != null) {
            objectOutputStream.close();
        }
        if (objectInputStream != null) {
            objectInputStream.close();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @Override
    public void run(){
        try {
            OutputStream outputStream = socket.getOutputStream();
            this.objectOutputStream = new ObjectOutputStream(outputStream);
            listenToMessage();
            sendMessage();
        } catch (IOException e){}
    }
}
