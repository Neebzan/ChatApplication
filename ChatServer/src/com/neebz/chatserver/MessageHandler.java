package com.neebz.chatserver;

import com.neebz.chatserver.com.neebz.chatserver.models.NetworkMessage;

public interface MessageHandler {

    public void MessageReceived(NetworkMessage networkMessage);

}
