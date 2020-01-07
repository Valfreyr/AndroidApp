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
using System.Collections.Generic;
using Microsoft.Extensions.Primitives;

namespace AzureFunctionsApp
{
    public static class RemoveRole
    {
        [FunctionName("RemoveRole")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            req.Headers.TryGetValue("sessionToken", out StringValues sessionToken);
            User requester = SessionValidator.ValidateSession(sessionToken.ToString());
            if (requester == null || requester.UserType.UserTypeName != "Admin")
            {
                return new StatusCodeResult(403);
            }
            List<string> errors = new List<string>();
            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            Role role;
            try
            {
                role = JsonConvert.DeserializeObject<Role>(requestBody);
            }
            catch
            {
                return new BadRequestResult();
            }

            if (string.IsNullOrWhiteSpace(requestBody) || role == null)
            {
                return new BadRequestResult();
            }

            using (DataContext dc = new DataContext())
            {
                Role r = dc.Roles.Where(x => x.RoleID == role.RoleID).FirstOrDefault();
                if (r == null)
                {
                    errors.Add("Role does not exist");
                }
                User u = dc.User.Where(x => x.RoleID == role.RoleID).FirstOrDefault();
                if (u != null)
                {
                    errors.Add("There are currently users with that role. Make sure this role is not used before deleting");
                }
                if (errors.Count > 0)
                {
                    return new BadRequestObjectResult(errors);
                }

                dc.Remove(r);
                dc.SaveChanges();
            }

            return new OkResult();
        }
    }
    
}
