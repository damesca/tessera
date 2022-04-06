package extended.privacy;

import java.net.*;
import java.io.*;

import org.apache.tuweni.bytes.Bytes;
import java.util.ArrayList;
import java.util.List;

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
            
            // TODO: perform a real PSI protocol

            // Receive messages from client
            String received = in.readLine().substring(2);   // Delete the '0x' prefix

            // Send the server messages
            String toSend = this.messages.toHexString();
            out.println(toSend);

            /*
            // Fix data
            int size = 64;
            List<String> clientMessages = new ArrayList<String>((received.length() + size - 1) / size);
            for(int start = 0; start < received.length(); start += size) {
                clientMessages.add(received.substring(start, Math.min(received.length(), start + size)));
            }

            int setLength = Integer.valueOf(clientMessages.get(1));
            List<String> clientSet = new ArrayList<String>(setLength);
            for(int i = 2; i < 2 + setLength; i++) {
                clientSet.add(clientMessages.get(i));
            }

            // TODO: Fix data and compare sets
            */

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
