using System;
using System.Collections.Generic;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class CreateTeamModel
    {
        public int? TeamID { get; set; }
        public string TeamName { get; set; }
        public string ProjectName { get; set; }
        public int? LeaderID { get; set; }
        public string LeaderName { get; set; }
    }
}