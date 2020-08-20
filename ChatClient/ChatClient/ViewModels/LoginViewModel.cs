using ChatClient.ViewModels.Commands;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Security.Policy;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace ChatClient.ViewModels {
    class LoginViewModel : ViewModelBase{

        //public ICommand ConnectToServerCommand { get; set; }

        //public LoginViewModel () {
        //    ConnectToServerCommand = new SimpleCommand(ConnectToServer, CanConnectToServer);
        //}

        //void ConnectToServer (object param) {
        //    TcpClient client = new TcpClient("localhost", 5000);
        //}

        //bool CanConnectToServer (object param) {
        //    return true;
        //}

    }
}
