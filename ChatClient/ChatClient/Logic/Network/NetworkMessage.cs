using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ChatClient.Logic.Network {
    public class NetworkMessage {

        public List<byte> Bytes { get; set; }
        public int MessageSize { get; set; }

        public NetworkMessage () {
            Bytes = new List<byte>();
        }

        public int ReadInt (int readPos) {
            int value = BitConverter.ToInt32(Bytes.ToArray(), readPos);
            return value;
        }

        public string ReadString (int fromIndex, int toIndex) {
            try {
                byte [ ] messageBytes = Bytes.GetRange(fromIndex, toIndex).ToArray();
                string value = Encoding.ASCII.GetString(messageBytes);

                return value;
            }
            catch {
                throw new Exception("Couldn't read value of type 'string'!");
            }
        }

        public string GetJSONFromBuffer () {
            string json = ReadString(8, GetMessageSize(0) - 4);
            return json;
        }

        public int GetMessageSize (int readPos) {
            int value = 0;

            if (Bytes.Count >= 4)
                value = ReadInt(readPos);
            
            return value;
        }

        public int GetMessageType () {
            int value = 0;

            if (Bytes.Count >= 8)
                value = ReadInt(4);
            
            return value;
        }
    }
}
