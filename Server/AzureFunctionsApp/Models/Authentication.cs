using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text;
using Newtonsoft.Json;

namespace AzureFunctionsApp.Models
{
    public class Authentication
    {
        [Key]
        public string AuthenticationID { get; set; }
        [Required]
        public string PasswordHash { get; set; }
        public int UserID { get; set; }
        [JsonIgnore]
        [Required]
        public User User { get; set; }
        [Required]
        public string Salt { get; set; }

    }
}
