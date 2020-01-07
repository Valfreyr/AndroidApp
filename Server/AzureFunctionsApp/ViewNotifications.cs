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
using Microsoft.Extensions.Primitives;
using System.Linq;
using System.Collections.Generic;

namespace AzureFunctionsApp
{
    public static class ViewNotifications
    {
        [FunctionName("ViewNotifications")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            req.Headers.TryGetValue("sessionToken", out StringValues sessionToken);
            User requester = SessionValidator.ValidateSession(sessionToken.ToString());
            if (requester == null)
            {
                return new StatusCodeResult(403);
            }
            IEnumerable<Notification> notifications = new List<Notification>();

            using (DataContext dc = new DataContext())
            {
                notifications = dc.Notification.Where(x => x.User == requester).ToList();
            }

            return new JsonResult(notifications); 
        }
    }
}
