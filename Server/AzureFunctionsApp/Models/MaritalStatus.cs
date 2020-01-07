using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class MaritalStatus
    {
        [Key]
        public int MaritalStatusID { get; set; } 
        [Required]
        public string MaritalStatusName { get; set; }
        [JsonIgnore]
        public IEnumerable<User> Users { get; set; } 
    }
}
