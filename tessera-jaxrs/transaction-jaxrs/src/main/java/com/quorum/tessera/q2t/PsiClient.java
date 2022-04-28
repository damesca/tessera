package com.quorum.tessera.q2t;

import java.net.*;
import java.io.*;

public class PsiClient {
    
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        while(true){
            try{
                clientSocket = new Socket(ip, port);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch(IOException e){
                /*LOG*/System.out.println(e);
                //Thread.sleep(1000);
            }
        }
    }

    public void sendMessage() {
        out.write("test");
    }

}
