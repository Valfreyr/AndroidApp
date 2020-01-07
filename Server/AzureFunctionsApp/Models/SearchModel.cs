using Microsoft.Azure.Search;
using Microsoft.Azure.Search.Models;
using System;
using System.Collections.Generic;
using System.Text;

namespace AzureFunctionsApp.Models
{
   
    public partial class SearchModel
    {
        public String SearchString { get; set; }
        public String Filter { get; set; }
    }
}
