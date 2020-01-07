using System;
using System.Collections.Generic;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class UserSessionModel
    {
        public User User { get; set; } 
        public string SessionToken { get; set; }
    }
}
