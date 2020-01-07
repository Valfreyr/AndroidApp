using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class UserLanguage
    {
        [Key]
        public int UserLanguageID { get; set; }
        public User User { get; set; } 
        public Language Language { get; set; }
    }
}
