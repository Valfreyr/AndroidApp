using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class Role
    {
        [Key]
        public int RoleID { get; set; }
        [Required]
        public string Title { get; set; }
        [JsonIgnore]
        public IEnumerable<User> Users {get; set; }
    }
}
