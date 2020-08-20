package com.neebz.chatserver;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Server {

    public static ArrayList<ClientInstance> connectedClients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(64);

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(5000);

        while(true){
            System.out.println("[SERVER] Waiting for connections");
            Socket s = ss.accept();
            System.out.println("[SERVER] Client connected" + s.getRemoteSocketAddress().toString());
            ClientInstance clientThread = new ClientInstance(s);
            connectedClients.add(clientThread);

            pool.execute(clientThread);
        }
    }
}
