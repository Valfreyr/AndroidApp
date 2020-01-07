
using AzureFunctionsApp.Models;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace AzureFunctionsApp
{
    public static class NotificationHandler
    {
        public static void SendNotification(Notification n, DataContext dc)
        {
            dc.Notification.Add(n);
            if (n.User.PhoneToken != null)
            {
                try
                {
                    var payload = new
                    {
                        to = n.User.PhoneToken,
                        notification = new
                        {
                            body = n.Body,
                            title = n.Title
                        }
                    };

                    string postbody = JsonConvert.SerializeObject(payload).ToString();

                    HttpRequestMessage hrm = new HttpRequestMessage
                    {
                        RequestUri = new Uri("https://fcm.googleapis.com/fcm/send"),
                        Content = new StringContent(postbody, Encoding.UTF8, "application/json"),
                        Method = HttpMethod.Post
                    };
                    //serverKey - Key from Firebase cloud messaging server  
                    hrm.Headers.TryAddWithoutValidation("Authorization", Environment.GetEnvironmentVariable("FirebaseServerKey"));
                    //Sender Id - From firebase project setting  '
                    Task.Run(() =>
                    {
                        using (HttpClient c = new HttpClient())
                        {
                            var a = c.SendAsync(hrm).Result;
                            var m = a.Content.ReadAsStringAsync().Result;
                        }
                    });
                    

                }
                catch (Exception)
                { }

            }
        }
    }
}
