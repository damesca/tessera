package extended.privacy;

import java.net.*;
import java.io.*;

import org.apache.tuweni.bytes.Bytes;

public class ObliviousTransferServer implements Runnable {
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int listeningPort;
    private Bytes messages;

    public ObliviousTransferServer(int listeningPort, Bytes messages){
        this.listeningPort = listeningPort;
        this.messages = messages;
    }

    private void start() {
        System.out.println("OTServer started...");
        try{
            serverSocket = new ServerSocket(this.listeningPort);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            String msg = in.readLine();
            System.out.println(msg);
            out.println(" >> Hello!");

        }catch(IOException e){
            System.out.println(e);
        }
    }

    private void stop() {
        try{
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
            System.out.println("OTServer closed...");
        } catch(IOException e){
            System.out.println(e);
        }
    }

    public void run(){
        this.start();
        this.stop();
    }

    public int getListeningPort(){
        return this.listeningPort;
    }

}
