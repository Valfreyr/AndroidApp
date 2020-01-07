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
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Primitives;

namespace AzureFunctionsApp
{
    public static class AttemptLogin
    {
        [FunctionName("AttemptLogin")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            List<string> errors = new List<string>();

            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            AuthenticaitonModel data;
            try
            {
                data = JsonConvert.DeserializeObject<AuthenticaitonModel>(requestBody);
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
                if (data.Password == null)
                {
                    return new BadRequestResult();
                }

                //Find entry in database with that email
                Authentication check = dc.Authentication.Include(x => x.User).Include(x => x.User.Team).Include(x => x.User.UserType).Where(x => x.User.Email == data.Email).FirstOrDefault();

                //If no entry found, return error
                if (check == null)
                {
                    errors.Add("Username or Password is incorrect");
                    return new BadRequestObjectResult(errors);
                }

                string hashedPassword = SessionValidator.ComputeHash(data.Password, check.Salt);

                //If password is incorrect, return error
                if (!hashedPassword.Equals(check.PasswordHash))
                {
                    errors.Add("Username or Password is incorrect");
                    return new BadRequestObjectResult(errors);
                }

                //If no user attached to that login, return error
                if (check.User == null)
                {
                    errors.Add("Username or Password is incorrect");
                    return new BadRequestObjectResult(errors);
                }

                var sessions = dc.Sessions.Where(x => x.User == check.User);
                foreach (Session s in sessions)
                {
                    dc.Sessions.Remove(s);
                }

                Session sesh = new Session
                {
                    User = check.User
                };

                dc.Sessions.Add(sesh);
                dc.SaveChanges();

                UserSessionModel manSesh = new UserSessionModel { User = check.User, SessionToken = sesh.SessionToken};

                //if all checks pass, return user
                return new JsonResult(manSesh);
            }

        }
    }
}
