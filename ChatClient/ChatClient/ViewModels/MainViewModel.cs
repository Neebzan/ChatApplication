using ChatClient.Logic.Helpers;
using ChatClient.Logic.Network;
using ChatClient.Models;
using ChatClient.Models.Messages;
using ChatClient.ViewModels.Commands;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Runtime.CompilerServices;
using System.Windows.Input;

namespace ChatClient.ViewModels {
    class MainViewModel : ViewModelBase {

        private static readonly MainViewModel instance = new MainViewModel();


        static MainViewModel () {

        }

        private MainViewModel () {
            CommunicationManager.ConnectToChatService();

        }

        public static MainViewModel Instance {
            get {
                return instance;
            }
        }
    }
}
