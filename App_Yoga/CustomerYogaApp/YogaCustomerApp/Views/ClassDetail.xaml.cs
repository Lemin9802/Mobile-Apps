using Microsoft.Maui.Controls;

namespace YogaCustomerApp.Views
{
    public partial class ClassDetail : ContentPage
    {
        public ClassDetail(YogaClass yogaClass)
        {
            InitializeComponent();
            BindingContext = yogaClass;  
        }

        private async void OnBackClicked(object sender, EventArgs e)
        {
            await Navigation.PopAsync();
        }
    }
}
