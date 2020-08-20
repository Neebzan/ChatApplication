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

        private void Button_Click (object sender, RoutedEventArgs e) {
            AuthenticationMessage data = new AuthenticationMessage {
                Username = UsernameTextbox.Text,
                Password = PasswordTextbox.Password
            };


            //PacketSender.AuthenticationRequest(data);

            Dispatcher.Invoke(DispatcherPriority.Normal,
                new Action(async () => {
                    await AnimateOut();
                    (Application.Current.MainWindow as MainWindow).MainFrame.NavigationService.Navigate(new ChatPage(new Models.User() { Name = UsernameTextbox.Text }));
                }));
        }
    }
}
