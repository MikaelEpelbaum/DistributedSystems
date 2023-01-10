import java.net.*;
import java.util.*;
import java.io.*;

public class Client extends Thread{

    private Socket socket;
    public Pair<Integer[], Double[]> lv_to_return;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private int id;


    public Client(Socket socket, int id){
        this.socket = socket;
        try {
            OutputStream outputStream = socket.getOutputStream();
            this.objectOutputStream = new ObjectOutputStream(outputStream);
            InputStream inputStream = socket.getInputStream();
            this.objectInputStream = new ObjectInputStream(inputStream);
            this.id = id;
        } catch (IOException e){ }

    }

    public void sendMessage(Pair<Integer[], Double[]> lv_to_send){
        try {
            if(socket.isConnected() && objectOutputStream != null) {
                System.out.println("SENT message on port: " + socket.getPort() + " message is: [" + lv_to_send.getValue()[0].intValue() + ", "+  lv_to_send.getValue()[1].intValue()+"]");
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
                while (socket.isConnected()) {
                    try {
                        Pair<Integer[], Double[]> temp = (Pair<Integer[], Double[]>) objectInputStream.readObject();
                        System.out.println("got message on port: " + socket.getPort() + " message is: [" + temp.getValue()[0].intValue() + ", "+  temp.getValue()[1].intValue()+"]");
                        if (temp != null){
                            lv_to_return = temp;
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        closeEverything(socket, objectOutputStream, objectInputStream);
                    }
                }
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
        listenToMessage();
    }
}
