package extended.privacy;

import java.net.*;
import java.io.*;

import java.util.ArrayList;
import java.util.List;

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
            // Send own messages to server
            out.println(msg);
            String received = in.readLine().substring(2);   // Delete the '0x' prefix

            // Fix own messages
            String own = msg.substring(2);   // Delete the '0x' prefix
            int size = 64;
            List<String> clientMessages = new ArrayList<String>((own.length() + size - 1) / size);
            for(int start = 0; start < own.length(); start += size) {
                clientMessages.add(own.substring(start, Math.min(own.length(), start + size)));
            }

            int clientSetLength = Integer.valueOf(clientMessages.get(1));
            List<String> clientSet = new ArrayList<String>(clientSetLength);
            for(int i = 2; i < 2 + clientSetLength; i++) {
                clientSet.add(clientMessages.get(i));
            }

            // Fix server messages
            List<String> serverMessages = new ArrayList<String>((received.length() + size - 1) / size);
            for(int start = 0; start < received.length(); start += size) {
                serverMessages.add(received.substring(start, Math.min(received.length(), start + size)));
            }

            int serverSetLength = Integer.valueOf(serverMessages.get(4));
            List<String> serverSet = new ArrayList<String>(serverSetLength);
            for(int i = 5; i < 5 + serverSetLength; i++) {
                serverSet.add(serverMessages.get(i));
            }

            /*LOG*/System.out.println(" >>> [OTC] client");
            for (String string : clientSet) {
                /*LOG*/System.out.println(string);
            }

            /*LOG*/System.out.println(" >>> [OTC] server");
            for (String string : serverSet) {
                /*LOG*/System.out.println(string);
            }

            // Get intersection
            List<String> setIntersection = new ArrayList<String>();
            for (String clientItem : clientSet) {
                for (String serverItem : serverSet) {
                    if(clientItem.compareTo(serverItem) == 0) {
                        setIntersection.add(clientItem);
                        /*LOG*/System.out.println(" >>> [PSI] add: " + clientItem);
                    }
                }
            }
            String intersection = "0x";
            for(String item : setIntersection) {
                intersection = intersection + item;
            }

            return intersection;
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
