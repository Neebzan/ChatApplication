using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ChatClient.Logic {

    public enum MessageState {
        Success = 0,
        Pending = 1,
        Denied = 2,
        Aborted = 3,
        Cancelled = 4,
        Failed = 5,
        Request = 6
    }

}
