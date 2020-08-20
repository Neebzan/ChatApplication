using ChatClient.Models;
using ChatClient.ViewModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace ChatClient.Views {
    /// <summary>
    /// Interaction logic for ChatPage.xaml
    /// </summary>
    public partial class ChatPage : BasePage {
        public ChatPage (User _user) {
            InitializeComponent();
            DataContext = new ChatViewModel(_user);
        }

        private void ListView_SelectionChanged (object sender, SelectionChangedEventArgs e) {
            (sender as ListView).ScrollIntoView((sender as ListView).SelectedItem);
        }
    }
}
