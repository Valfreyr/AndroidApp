using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class Document
    {
        [Key]
        public int DocumentID { get; set; }
        public User User { get; set; }
        public string FileName { get; set; }
        public string FileLocation { get; set; }
        public bool IsProfilePicture { get; set; }
    }
}
