using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class UserType
    {
        [Key]
        public int UserTypeID { get; set; } 
        [Required]
        public string UserTypeName { get; set; }
        [JsonIgnore]
        public IEnumerable<User> Users { get; set; }
    }
}
