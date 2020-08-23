using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ChatClient.Models.Messages {
    public class UserStateMessage : MessageBase {

        public User User { get; set; }
        public bool IsOnline { get; set; }

    }
}
