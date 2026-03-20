using Microsoft.Maui.Controls;
using Firebase.Database;
using Firebase.Database.Query;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using System;

namespace YogaCustomerApp.Views
{
    public partial class MyClassesPage : ContentPage
    {
        private readonly FirebaseClient _firebaseClient;
        private bool _isActive; // Tracks if the page is active

        public ObservableCollection<Booking> BookedClasses { get; set; }

        public MyClassesPage()
        {
            InitializeComponent();
            _firebaseClient = new FirebaseClient("https://yoga-e55c5-default-rtdb.asia-southeast1.firebasedatabase.app");
            BookedClasses = new ObservableCollection<Booking>();
            BookedClasses.CollectionChanged += OnBookedClassesChanged; // Subscribe to collection changes
            LoadBookedClasses(); // Load booked classes when page is initialized
        }

        protected override void OnAppearing()
        {
            base.OnAppearing();
            _isActive = true; // Mark the page as active
        }

        protected override void OnDisappearing()
        {
            _isActive = false; // Mark the page as inactive
            BookedClasses.CollectionChanged -= OnBookedClassesChanged; // Unsubscribe from events
            base.OnDisappearing();
        }

        // Event to update the total when the collection changes
        private void OnBookedClassesChanged(object sender, System.Collections.Specialized.NotifyCollectionChangedEventArgs e)
        {
            UpdateTotalBookedClasses();
        }

        // Load booked classes from Firebase
        private async Task LoadBookedClasses()
        {
            try
            {
                var bookings = await _firebaseClient
                    .Child("bookings") // Get data from the "bookings" node
                    .OnceAsync<Booking>();

                // Populate the BookedClasses collection with the fetched bookings
                Device.BeginInvokeOnMainThread(() =>
                {
                    BookedClasses.Clear();
                    foreach (var booking in bookings.Select(b => b.Object))
                    {
                        BookedClasses.Add(booking);
                    }
                    BindingContext = this;
                    UpdateTotalBookedClasses(); // Update total booked classes
                });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error loading classes: {ex.Message}");
                await DisplayAlert("Error", $"An error occurred while loading booked classes: {ex.Message}", "OK");
            }
        }

        // Handle "Cancel Class" button click
        private async void OnCancelClassClicked(object sender, EventArgs e)
        {
            var button = (Button)sender;
            var booking = button.CommandParameter as Booking;

            if (booking == null || string.IsNullOrEmpty(booking.ClassType) || string.IsNullOrEmpty(booking.Email))
            {
                await DisplayAlert("Error", "Invalid booking data. Please try again.", "OK");
                return;
            }

            bool confirmCancel = await DisplayAlert("Cancel Class", $"Do you want to cancel the class '{booking.ClassType}'?", "Yes", "No");
            if (!confirmCancel) return;

            try
            {
                // Delete booking
                var bookingSnapshot = await _firebaseClient
                    .Child("bookings")
                    .OnceAsync<Booking>();

                var bookingToDelete = bookingSnapshot.FirstOrDefault(b =>
                    b.Object.ClassType == booking.ClassType && b.Object.Email == booking.Email);

                if (bookingToDelete != null)
                {
                    await _firebaseClient
                        .Child("bookings")
                        .Child(bookingToDelete.Key)
                        .DeleteAsync();
                }

                // Update class
                var classSnapshot = await _firebaseClient
                    .Child("classes")
                    .OnceAsync<YogaClass>();

                var classToUpdate = classSnapshot.FirstOrDefault(c =>
                    c.Object.ClassType == booking.ClassType);

                if (classToUpdate != null && classToUpdate.Object.BookedUsers != null)
                {
                    classToUpdate.Object.BookedUsers.Remove(booking.Email);

                    await _firebaseClient
                        .Child("classes")
                        .Child(classToUpdate.Key)
                        .Child("BookedUsers")
                        .PutAsync(classToUpdate.Object.BookedUsers);
                }

                // Update customer
                var sanitizedEmail = booking.Email.Replace(".", "_");
                await _firebaseClient
                    .Child("customers")
                    .Child(sanitizedEmail)
                    .Child("BookedClasses")
                    .Child(booking.ClassType)
                    .DeleteAsync();

                // Update UI
                if (_isActive)
                {
                    Device.BeginInvokeOnMainThread(() =>
                    {
                        if (BookedClasses.Contains(booking))
                        {
                            BookedClasses.Remove(booking);
                            TotalBookedClassesLabel.Text = $"Total Classes Booked: {BookedClasses.Count}";
                        }
                    });
                }

                await DisplayAlert("Success", $"Class '{booking.ClassType}' has been successfully canceled.", "OK");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error canceling class: {ex.Message}");
                await DisplayAlert("Error", $"An error occurred while canceling the class: {ex.Message}", "OK");
            }
        }

        // Update the total booked classes
        private void UpdateTotalBookedClasses()
        {
            if (_isActive)
            {
                Device.BeginInvokeOnMainThread(() =>
                {
                    if (TotalBookedClassesLabel != null)
                    {
                        TotalBookedClassesLabel.Text = BookedClasses?.Count.ToString() ?? "0";
                    }
                });
            }
        }

        // Class representing a Booking
        public class Booking
        {
            public string Email { get; set; }
            public string ClassId { get; set; }
            public string ClassType { get; set; }
            public string Date { get; set; }
            public decimal Price { get; set; }
            public string Status { get; set; }
            public string TeacherName { get; set; }
        }

        // Class representing a YogaClass
        public class YogaClass
        {
            public int Capacity { get; set; }
            public string ClassType { get; set; }
            public string Date { get; set; }
            public string Description { get; set; }
            public int Duration { get; set; }
            public int Id { get; set; }
            public int Price { get; set; }
            public string TeacherName { get; set; }
            public string Time { get; set; }
            public string FirebaseId { get; set; }
            public List<string> BookedUsers { get; set; } = new List<string>();
        }
    }
}
