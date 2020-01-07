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
    public static class DeleteUser
    {
        [FunctionName("DeleteUser")]
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
            UserIDModel data;
            User user;
            try
            {
                data = JsonConvert.DeserializeObject<UserIDModel>(requestBody);
            }
            catch
            {
                return new BadRequestResult();
            }

            if (string.IsNullOrWhiteSpace(requestBody) || data == null)
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
                
                Team t = dc.Team.Where(x => x.LeaderID == u.UserID).FirstOrDefault();
                if (t != null)
                {
                    t.LeaderID = null;
                }
                IEnumerable<UserSkill> uss = dc.UserSkills.Where(X => X.User == u);
                foreach (UserSkill us in uss)
                {
                    dc.UserSkills.Remove(us);
                }

                IEnumerable<UserLanguage> uls = dc.UserLanguages.Where(X => X.User == u);
                foreach (UserLanguage ul in uls)
                {
                    dc.UserLanguages.Remove(ul);
                }

                IEnumerable<Document> docs = dc.Documents.Where(X => X.User == u);
                foreach (Document doc in docs)
                {
                    dc.Documents.Remove(doc);
                }

                //login credentials removed is user is deleted
                Authentication login = dc.Authentication.Where(x => x.User.Email == u.Email).FirstOrDefault();
                if (login != null)
                {
                    dc.Authentication.Remove(login);
                }

                IEnumerable<Notification> notifications = dc.Notification.Where(x => x.User == u);
                foreach (Notification n in notifications)
                {
                    dc.Notification.Remove(n);
                }

                user = u;
                dc.Remove(user);
                dc.SaveChanges();
            }

            return new OkResult();
        }
    }
}

