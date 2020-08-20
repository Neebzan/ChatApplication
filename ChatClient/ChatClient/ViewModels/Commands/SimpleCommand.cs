using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace ChatClient.ViewModels.Commands {
    class SimpleCommand : ICommand {

        Action<object> executeMethod;
        Func<object, bool> canExecuteMethod;

        public SimpleCommand (Action<object> _executeMethod, Func<object, bool> _canExecuteMethod) {
            executeMethod = _executeMethod;
            canExecuteMethod = _canExecuteMethod;
        }

        public event EventHandler CanExecuteChanged;

        public bool CanExecute (object parameter) {
            return canExecuteMethod.Invoke(parameter);
        }

        public void Execute (object parameter) {
            executeMethod(parameter);
        }
    }
}
