using ChatClient.Logic.Network;
using ChatClient.Models.Messages;
using ChatClient.ViewModels;
using System;
using System.Windows;
using System.Windows.Threading;

namespace ChatClient.Views {
    /// <summary>
    /// Interaction logic for LoginPage.xaml
    /// </summary>
    public partial class LoginPage : BasePage {
        public LoginPage () {
            InitializeComponent();
            this.PageLoadAnimation = Animations.PageAnimation.FadeIn;
            DataContext = new LoginViewModel();
        }

        private bool attemptingLogin = false;

        private void Button_Click (object sender, RoutedEventArgs e) {
            AuthenticationMessage data = new AuthenticationMessage {
                Username = UsernameTextbox.Text,
                Password = PasswordTextbox.Password,
                MessageType = MessageType.Authentication
            };

            if (!attemptingLogin) {
                CommunicationManager.SendAuthenticationRequest(data, (args) => NavigateToChat(args));
                attemptingLogin = true;
            }
        }

        private void NavigateToChat (AuthenticationMessage authData) {
            attemptingLogin = false;
            if (authData.Success) {
                Dispatcher.Invoke(DispatcherPriority.Normal,
                    new Action(async () => {
                        await AnimateOut();
                        (Application.Current.MainWindow as MainWindow).MainFrame.NavigationService.Navigate(new ChatPage(new Models.User() { Name = UsernameTextbox.Text }));
                    }));
            }
        }
    }
}
