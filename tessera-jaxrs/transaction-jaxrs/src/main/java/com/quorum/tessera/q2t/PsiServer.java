package com.quorum.tessera.q2t;

import java.net.*;
import java.io.*;

public class PsiServer {
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int port;
    private byte[] messages;
    private String result;

    public PsiServer(int port, byte[] messages) {
        this.port = port;
        this.messages = messages;
    }

    public void waitForConnection() {

        try {
            serverSocket = new ServerSocket(this.port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            this.result = in.readLine();
        } catch(IOException e) {
            /*LOG*/System.out.println(e);
        }

    }

    public String getResult() {
        return this.result;
    }

}
