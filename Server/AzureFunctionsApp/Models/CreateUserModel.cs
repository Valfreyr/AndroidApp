using System;
using System.Collections.Generic;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class CreateUserModel
    {
        public int? UserID { get; set; }
        public string Name { get; set; }
        public string Role { get; set; }
        public string Mobile { get; set; }
        public DateTime? DateOfBirth { get; set; }
        public string UserType { get; set; }
        public string Address { get; set; }
        public string Email { get; set; }
        public string NextOfKin1 { get; set; }
        public string NextOfKin2 { get; set; }
        public string MaritalStatus { get; set; }
        public string Nationality { get; set; }
        public string VisaStatus { get; set; }
        public string Gender { get; set; }
        public string MedicalStatus { get; set; }
        public List<string> Languages { get; set; }
        public List<string> Skills { get; set; }
        public byte[] ProfilePicture { get; set; }
        public string Password { get; set; }
        public string TeamID { get; set; }
        public List<string> Documents { get; set; }
    }
}
