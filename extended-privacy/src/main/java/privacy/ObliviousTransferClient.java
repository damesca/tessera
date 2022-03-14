package extended.privacy;

import java.net.*;
import java.io.*;

public class ObliviousTransferClient {
    
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        System.out.println("OTClient started...");
        try{
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch(IOException e){
            System.out.println(e);
        }
    }

    public String sendMessage(String msg) {
        try{
            out.println(msg);
            String resp = in.readLine();
            return resp;
        } catch(IOException e){
            System.out.println(e);
            return null;
        }
    }

    public void stopConnection() {
        try{
            in.close();
            out.close();
            clientSocket.close();
            System.out.println("OTClient closed...");
        } catch(IOException e){
            System.out.println(e);
        }
    }

}
