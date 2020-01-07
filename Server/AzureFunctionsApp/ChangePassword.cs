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
using System.Collections.Generic;
using System.Linq;
using Microsoft.Extensions.Primitives;

namespace AzureFunctionsApp
{
    public static class ChangePassword
    {
        [FunctionName("ChangePassword")]
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

            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            ChangePasswordModel data;
            try
            {
                data = JsonConvert.DeserializeObject<ChangePasswordModel>(requestBody);
            }
            catch
            {
                return new BadRequestResult();
            }

            using (DataContext dc = new DataContext())
            {

                //Check if email or password entered is null
              
                if (data.OldPassword == null)
                {
                    return new BadRequestResult();
                }
                if (data.NewPassword == null)
                {
                    return new BadRequestResult();
                }

                Authentication original = dc.Authentication.Where(x => x.User.UserID == requester.UserID).FirstOrDefault();
                //If no entry found, return error
                if (original == null)
                {
                    errors.Add("Username or Password is incorrect");
                    return new BadRequestObjectResult(errors);
                }

                string hashedOldPassword = SessionValidator.ComputeHash(data.OldPassword, original.Salt);

                //If password is incorrect, return error
                if (!hashedOldPassword.Equals(original.PasswordHash))
                {
                    errors.Add("Username or Password is incorrect");
                    return new BadRequestObjectResult(errors);
                }

                original.PasswordHash = SessionValidator.ComputeHash(data.NewPassword, original.Salt);

                dc.SaveChanges();

                return new OkResult();
            }
        }
    }
}
