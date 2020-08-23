package com.neebz.chatserver;

import com.google.gson.Gson;
import com.neebz.chatserver.com.neebz.chatserver.models.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.*;

public class Connection {

    Socket socket;
    NetworkMessage incomingMessage;
    InputStream inputStream;
    BufferedInputStream dataBuffer;
    OutputStream outputStream;
    byte[] receiveBuffer;
    private List<MessageHandler> observers = new ArrayList<>();


    public Connection(Socket _socket) throws IOException {
        incomingMessage = new NetworkMessage();
        socket = _socket;

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public Connection(Socket _socket, MessageHandler caller) throws IOException {
        incomingMessage = new NetworkMessage();
        socket = _socket;

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        Subscribe(caller);
    }

    public void Subscribe(MessageHandler handler) {
        observers.add(handler);
    }

    public void ReadForData() throws IOException {
        // Read data from the stream
       receiveBuffer = new byte[4096];
       int bytesRead = inputStream.read(receiveBuffer);

        // If there was any data
        if (receiveBuffer.length > 0) {
            // Put that data into a list
            ArrayList<Byte> data = new ArrayList<Byte>();
            for (int i = 0; i < bytesRead; i++)
                data.add(receiveBuffer[i]);

            //Handle data
            HandleData(data);
        }
    }

    private void HandleData(ArrayList<Byte> data) {
        // Add data to the incoming message
        incomingMessage.Bytes.addAll(data);

        // If we don't yet know the size of the incoming message
        if (incomingMessage.MessageSize <= 0) {
            // If we have at least four bytes
            if (incomingMessage.Bytes.size() >= 4) {
                // Get the message length
                int size = incomingMessage.GetMessageSize();
                // If the size is valid
                if (size > 0)
                    incomingMessage.MessageSize = size;

                else return;

                if (incomingMessage.MessageSize + 4 < incomingMessage.Bytes.size())
                    incomingMessage.Bytes = new ArrayList<Byte>(incomingMessage.Bytes.subList(0, incomingMessage.MessageSize + 4));

            } else return;

        }

        // We have the entire message
        if (incomingMessage.Bytes.size() >= incomingMessage.MessageSize + 4 && incomingMessage.MessageSize > 0) {
            MessageReceived(incomingMessage);

            // If there is more data, then there is another message
            if (data.size() - (incomingMessage.MessageSize + 4) > 0) {
                // We get a new list of data, containing all bytes except the message we just read
                ArrayList<Byte> remainingBytes = new ArrayList<Byte>(data.subList(4, data.size() - 1));
                incomingMessage = new NetworkMessage();

                HandleData(remainingBytes);
            }
            else{
                incomingMessage = new NetworkMessage();
            }
        }
    }

    public synchronized void Write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.err.println("[ERROR] " + e.getStackTrace());
        }
    }

    private void MessageReceived(NetworkMessage networkMessage) {
        for (MessageHandler observer : observers) {
            observer.MessageReceived(networkMessage);
        }
    }
}

