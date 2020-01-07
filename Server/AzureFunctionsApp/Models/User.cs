using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class User
    { //TODO add required to not null
        [Key]
        public int UserID { get; set; }
        [Required]
        public string Name { get; set; }
        public string Mobile { get; set; } 
        public DateTime? DateOfBirth { get; set; } 
        public int RoleID { get; set; } 
        public Role Role { get; set; } 
        public UserType UserType { get; set; } 
        public string Address { get; set; }
        [Required]
        public string Email { get; set; } 
        public string NextOfKin1 { get; set; } 
        public string NextOfKin2 { get; set; } 
        public MaritalStatus MaritalStatus { get; set; } 
        public string Nationality { get; set; } 
        public string VisaStatus { get; set; } 
        public string Gender { get; set; } 
        public string MedicalStatus { get; set; } 
        public IEnumerable<UserLanguage> UserLanguages { get; set; }
        public IEnumerable<UserSkill> UserSkills { get; set; }
        public IEnumerable<Document> Documents { get; set; }
        [JsonIgnore]
        public IEnumerable<Session> Sessions { get; set; }
        public Team Team { get; set; }
        public DateTime DateTimeUpdated { get; set; }
        [JsonIgnore]
        public Authentication Authentication { get; set; }
        [JsonIgnore]
        public IEnumerable<Notification> Notifications { get; set; }
        public string PhoneToken { get; set; }
    }
}

/*[UserID] bigint not null IDENTITY(1,1), 
	[Name] varchar(50) not null, 
	[Mobile] varchar(30), 
	[DOB] datetime,
	[RoleID] bigint not null, 
	[UserTypeID] int not null, 
	[Address] varchar(500), 
	[Email] varchar(100) not null, 
	[NextOfKin1] varchar(50), 
	[NextOfKin2] varchar(50), 
	[MaritalStatusID] int, 
	[Nationality] varchar(50), 
	[VisaStatus] varchar(50),
	[MedicalStatus] varchar(50),
	[Gender] varchar(50), 
	Primary Key(UserID), 
	Foreign Key(RoleID) References[Role], 
	Foreign Key(UserTypeID) References[UserType], 
	Foreign Key(MaritalStatusID) References[MaritalStatus]*/