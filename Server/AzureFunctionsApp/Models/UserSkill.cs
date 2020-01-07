using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class UserSkill
    {
        [Key]
        public int UserSkillID { get; set; }
        public User User { get; set; }
        public Skill Skill { get; set; }
    }
}
