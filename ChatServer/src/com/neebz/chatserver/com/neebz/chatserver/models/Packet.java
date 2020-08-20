package com.neebz.chatserver.com.neebz.chatserver.models;

import com.neebz.chatserver.ClientInstance;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Packet {
    private List<Byte> buffer;
    private byte [ ] readableBuffer;
    private int readPos;


    public Packet () {
        buffer = new ArrayList<Byte>();
        readPos = 0;
    }

/*
    public void Write (String _value) {
        Write(_value.length()); //Write the length of the string
        buffer.AddRange(Encoding.ASCII.GetBytes(_value)); //Add the string
    }

 */

    public void Write (int _value) {
        byte[] byteArr = ByteBuffer.allocate(4).putInt(_value).array(); // Convert integer to byte array

        // Insert bytes into buffer byte array
        for (byte b : byteArr) {
            buffer.add(b);
        }
    }
}
