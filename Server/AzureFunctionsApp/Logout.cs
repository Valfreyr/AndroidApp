using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Microsoft.Extensions.Primitives;
using AzureFunctionsApp.Models;
using System.Linq;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace AzureFunctionsApp
{
    public static class Logout
    {
        [FunctionName("Logout")]
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
            List<string> errors = new List<string>();
            UserIDModel data = new UserIDModel();
            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            try
            {
                data = JsonConvert.DeserializeObject<UserIDModel>(requestBody);
            }
            catch
            {
                return new BadRequestResult();
            }
            using (DataContext dc = new DataContext())
            {
                User u = dc.User.Where(x => x.UserID == data.UserID).FirstOrDefault();
                if (u == null)
                {
                    errors.Add("User does not exist");
                }

                if (errors.Count > 0)
                {
                    return new BadRequestObjectResult(errors);
                }

                Session s = dc.Sessions.Include(x => x.User).Where(x => x.User.UserID == u.UserID).FirstOrDefault();
                if(s == null)
                {
                    return new BadRequestResult();
                }

                dc.Remove(s);
                dc.SaveChanges();
            }
            return new OkResult();
        }
    }
}
