using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
using System.Windows.Media.Animation;

namespace ChatClient.Animations
{
    public static class ProgressBarAnimations
    {
        public static async Task AnimateValueChange (this ProgressBar progressBar, float oldValue, float newValue, float seconds) {
            var sb = new Storyboard();

            sb.AddValueFadeChange(oldValue, newValue, seconds);

            sb.Begin(progressBar);

            await Task.Delay((int)(seconds * 1000));
        }
    }
}
