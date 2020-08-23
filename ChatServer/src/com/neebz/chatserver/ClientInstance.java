package com.neebz.chatserver;

import java.io.*;
import java.net.*;
import java.util.Calendar;

import com.google.gson.Gson;
import com.neebz.chatserver.com.neebz.chatserver.models.*;

public class ClientInstance implements Runnable, MessageHandler {

    boolean isConnected = true;
    Connection clientConnection;
    private User user;

    public User ConnectedUser() {
        return user;
    }

    private boolean autenticated = true;

    public boolean Autenticated() {
        return autenticated;
    }

    public ClientInstance(Socket _socket) throws IOException {
        clientConnection = new Connection(_socket, this);
        user = new User();
    }

    @Override
    public void run() {
        SendWelcome("Hello, and welcome to the chat server");
        try {
            while (isConnected) {
                clientConnection.ReadForData();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.err.println("[ERROR] " + e.getStackTrace());
        } finally {
            Disconnected();
            System.out.println("[THREAD " + Thread.currentThread().getName() + "] Client disconnected");
            isConnected = false;
            Server.connectedClients.remove(this);
        }
    }

    public void MessageReceived(NetworkMessage networkMessage) {
        System.out.println("[THREAD" + Thread.currentThread().getName() + "] messageReceived");
        try {
            // Read the type of message
            int messageTypeInt = networkMessage.GetMessageType();
            System.out.println("[THREAD" + Thread.currentThread().getName() + "] Package type int: " + messageTypeInt);

            // Get JSON
            String json = networkMessage.GetJSONFromBuffer();
            System.out.println("[THREAD" + Thread.currentThread().getName() + "] Result: " + json);

            MessageType messageType = MessageType.values()[messageTypeInt];

            Gson gson = new Gson();
            switch (messageType) {
                case Authentication -> {
                    AuthenticationMessage message = gson.fromJson(json, AuthenticationMessage.class);
                    AuthenticationRequestReceived(message);
                }
                case ChatMessage -> {
                    ChatMessage message = gson.fromJson(json, ChatMessage.class);
                    ChatMessageReceived(message);
                }
                case FriendRequest -> {
                    // Not implemented yet
                }
            }
        } catch (Exception e) {

        }
    }

    private void SendWelcome(String _content) {
        System.out.println("[THREAD " + Thread.currentThread().getName() + "] Generating welcome message");
        ChatMessage message = new ChatMessage();
        User sender = new User();
        sender.Name = "Server";
        message.Sender = sender;
        message.Content = _content;
        message.MessageType = MessageType.ChatMessage;

        byte[] bytes = message.GetMessageBytes();

        System.out.println("[THREAD " + Thread.currentThread().getName() + "] Sending welcome message");
        System.out.println("[THREAD " + Thread.currentThread().getName() + "] Welcome message length: " + bytes.length);
        clientConnection.Write(bytes);
    }

    private void Disconnected(){
        UserStateMessage toBeBroadcast = new UserStateMessage();
        toBeBroadcast.User = user;
        toBeBroadcast.MessageType = MessageType.UserState;
        toBeBroadcast.IsOnline = false;

        BroadcastMessage(toBeBroadcast);
    }

    private void ChatMessageReceived(ChatMessage _message) {
        if (!Autenticated()) return;

        _message.Date = Calendar.getInstance().getTime().toString();
        _message.MessageType = MessageType.ChatMessage;

        BroadcastMessage(_message);
    }

    private void AuthenticationRequestReceived(AuthenticationMessage _message) {
        autenticated = true;

        user.Name = _message.Username;

        UserStateMessage toBeBroadcast = new UserStateMessage();
        toBeBroadcast.User = user;
        toBeBroadcast.MessageType = MessageType.UserState;
        toBeBroadcast.IsOnline = true;

        _message.Success = true;
        _message.MessageType = MessageType.Authentication;

        BroadcastMessage(toBeBroadcast, _message);
    }

    private void BroadcastMessage(MessageBase _messageBroadcast, MessageBase _senderMessage) {
        MessageBase toSend;
        if (Server.connectedClients.size() > 0) {
            for (ClientInstance _client : Server.connectedClients) {
                toSend = _messageBroadcast;
                if (_client == this)
                    toSend = _senderMessage;

                byte[] bytes = toSend.GetMessageBytes();
                System.out.println("[THREAD" + Thread.currentThread().getName() + "] Sending message to: " + _client.ConnectedUser().Name);
                _client.clientConnection.Write(bytes);
            }
        }
    }

    private void BroadcastMessage(MessageBase _messageBroadcast) {
        if (Server.connectedClients.size() > 0) {
            for (ClientInstance _client : Server.connectedClients) {
                byte[] bytes = _messageBroadcast.GetMessageBytes();
                System.out.println("[THREAD" + Thread.currentThread().getName() + "] message bytes " + bytes.toString());
                _client.clientConnection.Write(bytes);
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
