using ChatClient.Models;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ChatClient.Models.Messages {

    public enum MessageType { Authentication, ChatMessage, FriendRequest, UserState }

    public class MessageBase {
        public MessageType MessageType { get; set; }

        public string Date { get; set; }


        public byte [ ] GetMessageBytes () {
            List<byte> buffer = new List<byte>();

            // Generate JSON
            string json = JsonConvert.SerializeObject(this);
            Debug.WriteLine("chat json: " + json);

            // Add json
            byte [ ] jsonBytes = Encoding.UTF8.GetBytes(json);
            buffer.AddRange(jsonBytes);

            // Add message type integer
            buffer.InsertRange(0, BitConverter.GetBytes((int)MessageType));

            // Add length of entire package as header
            Debug.WriteLine("message length: " + buffer.Count);
            buffer.InsertRange(0, BitConverter.GetBytes(buffer.Count));

            // Return as array
            return buffer.ToArray();
        }
    }
}
