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
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Primitives;

namespace AzureFunctionsApp
{
    public static class DeleteTeam
    {
        [FunctionName("DeleteTeam")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            req.Headers.TryGetValue("sessionToken", out StringValues sessionToken);
            User requester = SessionValidator.ValidateSession(sessionToken.ToString());
            if (requester == null || requester.UserType.UserTypeName!="Admin")
            {
                return new StatusCodeResult(403);
            }
            List<string> errors = new List<string>();
            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            TeamIDModel data;
            Team team;
            try
            {
                data = JsonConvert.DeserializeObject<TeamIDModel>(requestBody);
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
                Team t = dc.Team.Where(x => x.TeamID == data.TeamID).FirstOrDefault();
                if (t == null)
                {
                    errors.Add("Team does not exist");
                }

                if (errors.Count > 0)
                {
                    return new BadRequestObjectResult(errors);
                }

                //Creates a list of users who are in the team we want to delete and removes them each from the team before it is deleted
                IEnumerable<User> UsersInTeam = dc.User.Where(x => x.Team.TeamID == t.TeamID);
                foreach (User u in UsersInTeam)
                {
                    NotificationHandler.SendNotification(new Notification { Title = "Team Update", Body = "You have been removed from your team", User = u },dc);
                    u.Team = null;
                }

                if (t != null)
                {
                    User leader = dc.User.Where(x => x.UserID == t.LeaderID).FirstOrDefault();
                    if (leader != null)
                    {
                        UserType type = dc.UserTypes.Where(x => x.UserTypeName == "User").FirstOrDefault();
                        leader.UserType = type; //Changes usertype to basic when manager is removed from team
                    }

                }

                team = t;
                dc.Remove(team);
                dc.SaveChanges();
            }

            return new OkResult();
        }
    }
}
