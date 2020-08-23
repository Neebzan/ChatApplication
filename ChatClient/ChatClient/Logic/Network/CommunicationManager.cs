using ChatClient.Logic.Helpers;
using ChatClient.Models;
using ChatClient.Models.Messages;
using Newtonsoft.Json;
using System;
using System.CodeDom;
using System.Collections.Generic;
using System.Data;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Documents;

namespace ChatClient.Logic.Network {

    public delegate void UserAuthenticatedCallback (AuthenticationMessage authData);


    public static class CommunicationManager {
        public static Connection ChatService { get; private set; }

        public static event EventHandler<ChatMessage> OnMessageReceived;

        private static UserAuthenticatedCallback authenticatedCallback;

        public static void ConnectToChatService () {
            ChatService = new Connection(Constants.CHAT_SERVICES_IP, Constants.CHAT_SERVICES_PORT);
        }



        public static void ReceivedMessage (NetworkMessage networkMessage) {
            Debug.WriteLine("Message received");

            // Read the message type
            MessageType msgType = (MessageType)networkMessage.GetMessageType();
            string json = networkMessage.GetJSONFromBuffer();

            switch (msgType) {
                case MessageType.Authentication:
                    AuthenticationMessage auth = JsonConvert.DeserializeObject<AuthenticationMessage>(json);
                    if (authenticatedCallback != null) {
                        authenticatedCallback?.Invoke(auth);
                    }

                    break;
                case MessageType.ChatMessage:
                    ChatMessage chat = JsonConvert.DeserializeObject<ChatMessage>(json);
                    OnMessageReceived?.Invoke(null, chat);
                    break;

                case MessageType.UserState:
                    UserStateMessage userState = JsonConvert.DeserializeObject<UserStateMessage>(json);
                    OnMessageReceived?.Invoke(null, new ChatMessage() {
                        Sender = new User() {Name = "SYSTEM" },
                        Content = userState.User.Name + " has " + (userState.IsOnline ? "come online" : "gone offline"),
                        Date = userState.Date});

                    break;

                default:
                    break;
            }
        }

        public static void SendChat (ChatMessage chatMessage) {
            byte[] data = chatMessage.GetMessageBytes();

            ChatService.SendData(data);
        }

        public static void SendAuthenticationRequest (AuthenticationMessage authMessage, UserAuthenticatedCallback callback) {
            byte [ ] data = authMessage.GetMessageBytes();

            authenticatedCallback = callback;

            ChatService.SendData(data);
       }
    }

    public class Connection {

        TcpClient socket = new TcpClient();
        NetworkStream networkStream;
        NetworkMessage incommingMessage;
        byte [ ] receiveBuffer;
        int bufferReadIndex = 0;

        public Connection (string IP, int port, int bufferSize = 4096) {
            receiveBuffer = new byte [ bufferSize ];
            incommingMessage = new NetworkMessage();
            Connect(IP, port);
        }

        private void Connect (string IP, int port) {
            socket = new TcpClient();

            socket.BeginConnect(IP, port, ConnectResult, null);
        }

        private void ConnectResult (IAsyncResult _result) {
            socket.EndConnect(_result);
            networkStream = socket.GetStream();

            networkStream.BeginRead(receiveBuffer, 0, receiveBuffer.Length, ReadResult, null);
        }

        private void ReadResult (IAsyncResult _result) {
            int incommingBytes = networkStream.EndRead(_result);

            // Set the data buffer to the amount of bytes that was just read
            byte [ ] dataBuffer = new byte [ incommingBytes ];
            // Copy data into this buffer
            Array.Copy(receiveBuffer, dataBuffer, incommingBytes);

            // Call hamdle data
            // Split in another method to allow recursion
            HandleData(dataBuffer);

            networkStream.BeginRead(receiveBuffer, 0, receiveBuffer.Length, ReadResult, null);
        }

        private void HandleData (byte [ ] data) {
            // Set the data as a list for easier manipulation
            List<byte> dataBytes = data.ToList();

            // Add the data to the incomming message
            // The message might already contain some data, therefore we add instead of setting the value
            incommingMessage.Bytes.AddRange(dataBytes);

            // If we don't know the size of the message yet
            if (incommingMessage.MessageSize <= 0) {
                // If more or 4 bytes are in the buffer (size of int in memory), we can get the size of the incomming package
                if (incommingMessage.Bytes.Count >= 4) {
                    // Read the first four bytes as an integer value, and set that as the total message size
                    int sizeValue = incommingMessage.GetMessageSize(bufferReadIndex);
                    if (sizeValue > 0) {
                        incommingMessage.MessageSize = sizeValue;
                    }
                    else
                        return;

                    // Trim byte array
                    if (bufferReadIndex > 0) {
                        incommingMessage.Bytes.RemoveRange(0, bufferReadIndex);
                    }
                    if (incommingMessage.MessageSize + 4 < incommingMessage.Bytes.Count) {
                        int from = incommingMessage.MessageSize + 4; // The byte after the last byte in the message
                        int amount = incommingMessage.Bytes.Count - from;
                        incommingMessage.Bytes.RemoveRange(from, amount);
                    }

                }
                else
                    // If we don't even have four bytes, we should simply continue to read
                    return;
            }


            // We've got a whole package 
            if (incommingMessage.Bytes.Count >= incommingMessage.MessageSize && incommingMessage.MessageSize > 0) {
                // Tell the communication manager to handle the message
                CommunicationManager.ReceivedMessage(incommingMessage);

                // If there is more data - the package contains another message
                if ((data.Length - (incommingMessage.MessageSize + 4)) > 0) {
                    // The next message begins at the end of the previous
                    // This is calculated as the length of the previous message, plus the size-integer (4 more bytes)
                    bufferReadIndex += incommingMessage.MessageSize + 4;

                    // We now set the next bytes to be from the new index, and to the length of the list, subtracted by the new index
                    byte [ ] RemainingBytes = dataBytes.GetRange(bufferReadIndex, dataBytes.Count - bufferReadIndex).ToArray();

                    incommingMessage = new NetworkMessage();
                    bufferReadIndex = 0;
                    HandleData(RemainingBytes);
                }
                else {
                    incommingMessage = new NetworkMessage();
                    bufferReadIndex = 0;
                }
            }
        }

        public bool Connected () {
            throw new NotImplementedException();
        }

        public void SendData (byte [ ] data) {
            networkStream.BeginWrite(data, 0, data.Length, null, null);
        }

    }
}
