﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ChatClient.Models.Messages {
    public class AuthenticationMessage : MessageBase {

        public string Username { get; set; }
        public string Password { get; set; }
        public bool Success { get; set; }
    }
}
