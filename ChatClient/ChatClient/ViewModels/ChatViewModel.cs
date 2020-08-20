using ChatClient.Logic.Network;
using ChatClient.Models;
using ChatClient.Models.Messages;
using ChatClient.ViewModels.Commands;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;
using System.Windows.Threading;

namespace ChatClient.ViewModels {
    class ChatViewModel : ViewModelBase {
        public User User { get; set; }
        private string message;

        public string Message {
            get { return message; }
            set {
                message = value;
                OnPropertyChanged();
            }
        }


        private MessageBase selectedMessage;

        public MessageBase SelectedMessage {
            get { return selectedMessage; }
            set {
                selectedMessage = value;
                OnPropertyChanged();
            }
        }




        private ObservableCollection<MessageBase> messages;
        public ObservableCollection<MessageBase> Messages {
            get { return messages; }
            set {
                messages = value;
                OnPropertyChanged();
            }
        }

        public ICommand SendMessageCommand { get; set; }
        public ICommand SoftEnterCommand { get; set; }

        public ChatViewModel (User _user) {
            SendMessageCommand = new SimpleCommand(SendMessage, CanSendMessage);
            SoftEnterCommand = new SimpleCommand(SoftEnter, CanSoftEnter);
            User = _user;
            SetupPlaceholder();
            CommunicationManager.OnMessageReceived += CommunicationManager_OnMessageReceived; ;
        }

        private void CommunicationManager_OnMessageReceived (object sender, ChatMessage msg) {
            Application.Current.Dispatcher.Invoke(DispatcherPriority.Background, new Action(() => {
                Messages.Add(msg);
                SelectedMessage = msg;
            }));
        }

        void SendMessage (object parameter) {
            if (parameter is string) {
                string text = (parameter as string);

                if (!String.IsNullOrEmpty(text)) {
                    ChatMessage msg = new ChatMessage() {
                        Content = text,
                        Sender = User,
                        Date = DateTime.Now.ToString(),
                        MessageType = MessageType.ChatMessage
                    };
                    SendMessageToServer(msg);
                }
            }

            Message = "";
        }

        bool CanSendMessage (object parameter) {
            return true;
        }

        void SoftEnter (object parameter) {
            Message += "\n";
        }

        bool CanSoftEnter (object parameter) {
            return true;
        }

        void SetupPlaceholder () {
            if (String.IsNullOrEmpty(User.Name)) {
                User = new User() {
                    Name = "Neebz"
                };
            }

            Messages = new ObservableCollection<MessageBase>();
        }

        void SendMessageToServer (ChatMessage message) {
            CommunicationManager.SendChat(message);
        }
    }
}
