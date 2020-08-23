package com.neebz.chatserver.com.neebz.chatserver.models;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NetworkMessage {

    public List<Byte> Bytes = new ArrayList<Byte>();
    public int MessageSize;

    public NetworkMessage() {
       Bytes = new ArrayList<Byte>();
    }

    public int ReadInt (int readPos) {
        int value = 0;
        byte[] byteArr = new byte[4];
        int i = 0;
        while (byteArr.length < 4){
            byteArr[i] = Bytes.get(readPos + i);
            i++;
        }

        value = (((byteArr[3] & 0xff) << 24) | ((byteArr[2] & 0xff) << 16) |
                ((byteArr[1] & 0xff) << 8) | (byteArr[0] & 0xff));

        return value;
    }

    public String ReadString (int fromIndex, int toIndex) throws IOException {
            String value = "";
            List<Byte> textBytes = Bytes.subList(fromIndex, toIndex);

            byte[] byteArr = ByteListToArray(textBytes);

            value = new String(byteArr, StandardCharsets.UTF_8);
            return value;
    }

    public static byte[] ByteListToArray(List<Byte> Bytes){
        byte[] byteArr = new byte[Bytes.size()];

        for (int i = 0; i < Bytes.size(); i++){
            byteArr[i] = Bytes.get(i);
        }

        return byteArr;
    }

    public int GetMessageSize (int readPos) {
        int value = 0;

        if (Bytes.size() >= 4)
            value = ReadInt(readPos);

        return value;
    }

    public int GetMessageType () {
        int value = 0;

        if (Bytes.size() >= 8)
            value = ReadInt(4);

        return value;
    }

}
