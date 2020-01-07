using System;
using System.Collections.Generic;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class DownloadDocumentModel
    {
        public int UserID { get; set; }
        public string FileName { get; set; }
    }
}
