using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text;
using Newtonsoft.Json;

namespace AzureFunctionsApp.Models
{
    public class Team
    {
        [Key]
        public int TeamID { get; set; }
        [Required]
        public string TeamName { get; set; }
        public string ProjectName { get; set; }
        public int? LeaderID { get; set; }
        [JsonIgnore]
        public IEnumerable<User> Users { get; set; }

    }
}