using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;

namespace ChatClient.ViewModels {
    class ViewModelBase : INotifyPropertyChanged {
        public ViewModelBase () {

        }

        public event PropertyChangedEventHandler PropertyChanged;

        public void OnPropertyChanged ([CallerMemberName] string caller = null) {
            var handler = PropertyChanged;
            if (handler != null) {
                handler(this, new PropertyChangedEventArgs(caller));
            }
        }
    }
}
