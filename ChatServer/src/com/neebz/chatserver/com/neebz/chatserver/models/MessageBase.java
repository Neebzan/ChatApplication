package com.neebz.chatserver.com.neebz.chatserver.models;

import com.google.gson.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class MessageBase {

    public MessageType MessageType;
    public String Date;

    public MessageBase(){
        Date = Calendar.getInstance().getTime().toString();
    }

    public byte[] GetMessageBytes() {
        List<Byte> buffer = new ArrayList<Byte>();

        // Get JSON
        Gson gson = new Gson();
        String json = gson.toJson(this);

        // Add message as bytes
        byte[] jsonBytes = json.getBytes();
        // Add to arrayList
        for (byte b : jsonBytes) {
            buffer.add(b);
        }

        // Add message type integer
        byte[] messageTypeBytes = ByteBuffer.allocate(4).putInt((MessageType.ordinal())).array();
        // Add to arrayList
        for (byte b : messageTypeBytes) {
            buffer.add(0, b);
        }

        int messageLength = buffer.size();
        // Add message length integer
        byte[] messageLengthBytes = ByteBuffer.allocate(4).putInt(messageLength).array();
        // Add to arrayList
        for (byte b : messageLengthBytes) {
            buffer.add(0, b);
        }

        // Return as byte array
        byte[] bufferArray = new byte[buffer.size()];
        for (int i = 0; i < buffer.size(); i++){
            bufferArray[i] = buffer.get(i);
        }

        return bufferArray;
    }
}
