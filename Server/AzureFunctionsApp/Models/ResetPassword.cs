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

namespace AzureFunctionsApp
{
    public static class ResetPassword
    {
        [FunctionName("ResetPassword")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
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
                if (data.Email == null)
                {
                    return new BadRequestResult();
                }

                //Find entry in database with that email
                Authentication original = dc.Authentication.Where(x => x.User.Email == data.Email).FirstOrDefault();

                //If no entry found, return error
                if (original == null)
                {
                    errors.Add("User with that email does not exist");
                    return new BadRequestObjectResult(errors);
                }

                //Hashing default password
                var sha = new System.Security.Cryptography.SHA256Managed();
                byte[] textData = System.Text.Encoding.UTF8.GetBytes("password");
                byte[] hash = sha.ComputeHash(textData);

                string hashedPassword = BitConverter.ToString(hash).Replace("-", String.Empty);

                original.PasswordHash = hashedPassword;

                dc.SaveChanges();

                return new OkResult();
            }
        }
    }
}