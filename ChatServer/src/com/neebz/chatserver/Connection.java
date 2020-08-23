package com.neebz.chatserver;

import com.google.gson.Gson;
import com.neebz.chatserver.com.neebz.chatserver.models.*;

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
    OutputStream outputStream;
    byte[] receiveBuffer;
    int bufferReadIndex = 0;
    private List<MessageHandler> observers = new ArrayList<>();


    public Connection(Socket _socket) throws IOException {
        receiveBuffer = new byte[4096];
        incomingMessage = new NetworkMessage();
        socket = _socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public Connection(Socket _socket, MessageHandler caller) throws IOException {
        receiveBuffer = new byte[4096];
        incomingMessage = new NetworkMessage();
        socket = _socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        Subscribe(caller);
    }

    public void Subscribe(MessageHandler handler) {
        observers.add(handler);
    }

    public void ReadForData() {
        try {
            // Read data from the stream
            inputStream.read(receiveBuffer);

            // If there was any data
            if (receiveBuffer.length > 0) {
                // Put that data into a list
                ArrayList<Byte> data = new ArrayList<Byte>();
                for (int i = 0; i < receiveBuffer.length; i++)
                    data.add(receiveBuffer[i]);

                //Handle data
                HandleData(data);
            }

        } catch (Exception e) {

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
                int size = incomingMessage.GetMessageSize(bufferReadIndex);

                // If the size is valid
                if (size > 0)
                    incomingMessage.MessageSize = size;

                else return;

                // Trim array
                if (bufferReadIndex > 0)
                    incomingMessage.Bytes = new ArrayList<Byte>(incomingMessage.Bytes.subList(0, bufferReadIndex));

                if (incomingMessage.MessageSize + 4 < incomingMessage.Bytes.size())
                    incomingMessage.Bytes = new ArrayList<Byte>(incomingMessage.Bytes.subList(0, incomingMessage.MessageSize + 4));

            } else return;

        }

        // We have the entire message
        if (incomingMessage.Bytes.size() >= incomingMessage.MessageSize && incomingMessage.MessageSize > 0) {
            MessageReceived(incomingMessage);

            // If there is more data, then there is another message
            if (data.size() - (incomingMessage.MessageSize + 4) > 0) {
                // We skip to the beginning of the next message
                bufferReadIndex += incomingMessage.MessageSize + 4;

                // We get a new list of data, containing all bytes except the message we just read
                ArrayList<Byte> remainingBytes = new ArrayList<Byte>(data.subList(bufferReadIndex, data.size() - 1));
                bufferReadIndex = 0;
                incomingMessage = new NetworkMessage();

                HandleData(remainingBytes);
            }
        }
    }

    private void MessageReceived(NetworkMessage networkMessage) {
        for (MessageHandler observer : observers) {
            observer.MessageReceived(networkMessage);
        }
    }

    private int GetIntFromBytes(byte[] bytes) {
        if (bytes.length < 4) return -1;

        int value = 0;

        // Converts the bytes from signed (bytes are signed in Java), to unsigned, as used in .NET
        value = (((bytes[3] & 0xff) << 24) | ((bytes[2] & 0xff) << 16) |
                ((bytes[1] & 0xff) << 8) | (bytes[0] & 0xff));

        return value;
    }
}

