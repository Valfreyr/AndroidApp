using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace AzureFunctionsApp.Models
{
   public class Notification
    {
        [Key]
        public int NotificationID { get; set; }
        [JsonIgnore]
        public User User { get; set; }
        public string Title { get; set; }
        public string Body { get; set; }
        public DateTime Date { get; set; } = DateTime.Now;
        
    }
}
