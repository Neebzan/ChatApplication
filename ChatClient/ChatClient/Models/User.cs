using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ChatClient.Models {
    public class User : ModelBase{
        public int ID { get; set; }
        public string Name { get; set; }
    }
}
