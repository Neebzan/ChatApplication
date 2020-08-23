package com.neebz.chatserver;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import com.google.gson.Gson;
import com.neebz.chatserver.com.neebz.chatserver.models.*;

public class ClientInstance implements Runnable {

    Socket socket;
    OutputStream outputStream;
    InputStream inputStream;
    boolean isConnected = true;
    int bufferSize = 4096;

    private User user;

    public User ConnectedUser() {
        return user;
    }

    private boolean autenticated = true;

    public boolean Autenticated() {
        return autenticated;
    }

    public ClientInstance(Socket _socket) throws IOException {
        socket = _socket;
        outputStream = socket.getOutputStream();
        inputStream = _socket.getInputStream();
    }

    public synchronized void WriteToConnectedClient(byte[] bytes) {
        try {
            System.out.println("[THREAD " + Thread.currentThread().getName() + "] Writing message to client");
            outputStream.write(bytes);
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.err.println("[ERROR] " + e.getStackTrace());
        }
    }

    public void MessageReceived(NetworkMessage networkMessage){

    }

    public void run() {
        SendWelcome("Hello, and welcome to the chat server");
        try {
            while (isConnected) {
                System.out.println("[THREAD " + Thread.currentThread().getName() + "] Waiting for messages");


                // Read first four bytes - the size of the packet
                int packetLength = ReadInteger(inputStream);
                System.out.println("[THREAD" + Thread.currentThread().getName() + "] Package size: " + packetLength);

                // Read next four bytes - the type of the packet
                int messageTypeInt = ReadInteger(inputStream);
                System.out.println("[THREAD" + Thread.currentThread().getName() + "] Package type int: " + messageTypeInt);

                // Read packet content - subtract 4 as we've already read the the package type
                byte[] receivedBytes = new byte[packetLength - 4];
                // Read from stream;
                int messageBytesRead = inputStream.read(receivedBytes, 0, receivedBytes.length);

                System.out.println("[THREAD" + Thread.currentThread().getName() + "] Package bytes read: " + messageBytesRead);

                // String json = String.valueOf(receivedBytes);
                String json = new String(receivedBytes, StandardCharsets.UTF_8);
                // Read as JSON
                // String json = new String(receivedBytes, 0, receivedBytes.length);

                // Remove extra symbol infront of json string
                // json = json.substring(1,json.length());


                System.out.println("[THREAD" + Thread.currentThread().getName() + "] Result: " + json);

                MessageType messageType = MessageType.values()[messageTypeInt];

                Gson gson = new Gson();
                    switch (messageType) {
                        case Authentication -> {
                            AuthenticationRequestReceived(json);
                        }
                        case ChatMessage -> {
                            ChatMessage message = gson.fromJson(json, ChatMessage.class);
                            ChatMessageReceived(message);
                        }
                        case FriendRequest -> {
                            // Not implemented yet
                        }
                    }

            }

        } catch (IOException e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.err.println("[ERROR] " + e.getStackTrace());
        } finally {
            System.out.println("[THREAD " + Thread.currentThread().getName() + "] Client disconnected");
            isConnected = false;
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.connectedClients.remove(this);
        }
    }

    private void SendWelcome(String _content) {
        System.out.println("[THREAD " + Thread.currentThread().getName() + "] Generating welcome message");
        ChatMessage message = new ChatMessage();
        User sender = new User();
        sender.Name = "Server";
        message.Sender = sender;
        message.Content = _content;
        message.Date = Calendar.getInstance().getTime().toString();
        message.MessageType = MessageType.ChatMessage;

        byte[] bytes = message.GetMessageBytes();

        System.out.println("[THREAD " + Thread.currentThread().getName() + "] Sending welcome message");
        System.out.println("[THREAD " + Thread.currentThread().getName() + "] Welcome message length: " + bytes.length);
        WriteToConnectedClient(bytes);
    }

    private void ChatMessageReceived(ChatMessage _message) {
            System.out.println("[THREAD" + Thread.currentThread().getName() + "] Initiate message broadcast");

            _message.Date = Calendar.getInstance().getTime().toString();
            _message.MessageType = MessageType.ChatMessage;

            BroadcastMessage(user, _message);
    }

    private void AuthenticationRequestReceived(String json) {
        autenticated = true;
    }

    private void BroadcastMessage(User _sender, MessageBase _message) {
        if (Server.connectedClients.size() > 0) {
            for (ClientInstance _client : Server.connectedClients) {
                byte[] bytes = _message.GetMessageBytes();
                System.out.println("[THREAD" + Thread.currentThread().getName() + "] message bytes " + bytes.toString());
                _client.WriteToConnectedClient(bytes);
            }
        }
    }

    private static int ReadInteger(InputStream stream) throws IOException {
        byte[] packetLengthBuffer = new byte[4];
        int value = 0;

        stream.read(packetLengthBuffer, 0, 4);
        System.out.print("[THREAD" + Thread.currentThread().getName() + "] Converting 4 bytes to int: ");
        System.out.print("Byte 1: " + packetLengthBuffer[3]);
        System.out.print(", Byte 2: " + packetLengthBuffer[2]);
        System.out.print(", Byte 3: " + packetLengthBuffer[1]);
        System.out.println(", Byte 4: " + packetLengthBuffer[0]);
        // Converts the bytes from signed (bytes are signed in Java), to unsigned, as used in .NET
        value = (((packetLengthBuffer[3] & 0xff) << 24) | ((packetLengthBuffer[2] & 0xff) << 16) |
                ((packetLengthBuffer[1] & 0xff) << 8) | (packetLengthBuffer[0] & 0xff));

        return value;

    }
}
