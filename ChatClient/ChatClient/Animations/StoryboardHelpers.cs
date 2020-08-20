using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media.Animation;

namespace ChatClient.Animations
{
    public static class StoryboardHelpers
    {
        public static void AddSlideFromLeft(this Storyboard storyboard, float seconds, double offset, float decelerationRatio = 0.99f) {
            var animation = new ThicknessAnimation {
                Duration = new Duration(TimeSpan.FromSeconds(seconds)),
                From = new Thickness(-offset, 0, offset, 0),
                To = new Thickness(0),
                DecelerationRatio = decelerationRatio
            };
            
            Storyboard.SetTargetProperty(animation, new PropertyPath("Margin"));
            storyboard.Children.Add(animation);
        }

        public static void AddSlideToRight (this Storyboard storyboard, float seconds, double offset, float decelerationRatio = 0.99f) {
            var animation = new ThicknessAnimation {
                Duration = new Duration(TimeSpan.FromSeconds(seconds)),
                From = new Thickness(0, 0, 0, 0),
                To = new Thickness(offset, 0, -offset, 0),
                DecelerationRatio = decelerationRatio
            };

            Storyboard.SetTargetProperty(animation, new PropertyPath("Margin"));
            storyboard.Children.Add(animation);
        }

        public static void AddFadeIn (this Storyboard storyboard, float seconds) {
            var animation = new DoubleAnimation {
                Duration = new Duration(TimeSpan.FromSeconds(seconds)),
                From = 0,
                To = 1,
            };

            Storyboard.SetTargetProperty(animation, new PropertyPath("Opacity"));
            storyboard.Children.Add(animation);
        }

        public static void AddFadeOut (this Storyboard storyboard, float seconds) {
            var animation = new DoubleAnimation {
                Duration = new Duration(TimeSpan.FromSeconds(seconds)),
                From = 1,
                To = 0,
            };

            Storyboard.SetTargetProperty(animation, new PropertyPath("Opacity"));
            storyboard.Children.Add(animation);
        }

        public static void AddValueFadeChange (this Storyboard storyboard,float oldValue, float newValue, float seconds) {
            var animation = new DoubleAnimation {
                Duration = new Duration(TimeSpan.FromSeconds(seconds)),
                From = oldValue,
                To = newValue,
                DecelerationRatio=.99
            };
            Storyboard.SetTargetProperty(animation, new PropertyPath("Value"));
            storyboard.Children.Add(animation);
        }
    }
}
