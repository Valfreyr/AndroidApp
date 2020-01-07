using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using AzureFunctionsApp.Models;
using System.Linq;

namespace AzureFunctionsApp
{
    public static class Function1
    {
        [FunctionName("Function1")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            using (DataContext dc = new DataContext())
            {
                //dc.Roles.Add(new Role() { Title = "Developer" }); //Add new role with title developer 
                var role = dc.Roles.Where(x => x.Title == "Developer").FirstOrDefault();
                if(role != null) role.Title = "Developer2";
                dc.SaveChanges(); //Save Changes 
            }

            return (ActionResult)new OkObjectResult("");
        }
    }
}
