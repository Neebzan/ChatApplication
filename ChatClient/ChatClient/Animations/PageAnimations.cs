using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media.Animation;

namespace ChatClient.Animations
{
    public static class PageAnimations
    {
        public static async Task SlideAndFadeInFromLeft (this Page page, float seconds) {
            var sb = new Storyboard();

            sb.AddSlideFromLeft(seconds, 100);
            sb.AddFadeIn(seconds);

            sb.Begin(page);

            page.Visibility = Visibility.Visible;

            await Task.Delay((int)(seconds * 1000));
        }
        public static async Task SlideAndFadeOutToRight (this Page page, float seconds) {
            var sb = new Storyboard();

            sb.AddSlideToRight(seconds, 100);
            sb.AddFadeOut(seconds);

            sb.Begin(page);

            page.Visibility = Visibility.Visible;

            await Task.Delay((int)(seconds * 1000));

            page.Visibility = Visibility.Hidden;
        }

        public static async Task FadeIn (this Page page, float seconds) {
            var sb = new Storyboard();

            sb.AddFadeIn(seconds);

            sb.Begin(page);

            page.Visibility = Visibility.Visible;

            await Task.Delay((int)(seconds * 1000));
        }

        public static async Task FadeOut (this Page page, float seconds) {
            var sb = new Storyboard();

            sb.AddFadeOut(seconds);

            sb.Begin(page);

            page.Visibility = Visibility.Visible;

            await Task.Delay((int)(seconds * 1000));

            page.Visibility = Visibility.Hidden;
        }
    }
}
