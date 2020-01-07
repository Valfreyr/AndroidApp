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
using System.Net.Http;

namespace AzureFunctionsApp
{
    public static class AddTeamLeader
    {
        [FunctionName("AddTeamLeader")]
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
            TeamIDModel teamID;

            try
            {
                teamID = JsonConvert.DeserializeObject<TeamIDModel>(requestBody);
            }
            catch
            {
                return new BadRequestResult();
            }

            using (DataContext dc = new DataContext())
            {

                //Links to the database
                User user = dc.User.Include(x=>x.UserType).Where(x => x.Name == teamID.userName).FirstOrDefault();
                if (user == null)
                {
                    errors.Add("User does not exist");
                }

                Team team = dc.Team.Include(x=>x.Users).Where(x => x.TeamID == teamID.TeamID).FirstOrDefault();
                if (team == null)
                {
                    errors.Add("Team does not exist");
                }

                if (errors.Count > 0)
                {
                    return new BadRequestObjectResult(errors);
                }

                // Demote previous leader
                User previousLeader = dc.User.Include(x => x.UserType).Where(x => x.UserID == team.LeaderID).FirstOrDefault();
                if (previousLeader != null)
                {
                    if (previousLeader.UserType.UserTypeName != "Admin")
                    {
                        previousLeader.UserType = dc.UserTypes.Where(x => x.UserTypeName == "User").FirstOrDefault();
                    }
                    NotificationHandler.SendNotification(new Notification { User = previousLeader, Title = "Team Update", Body = "You were removed as team leader from " + team.TeamName },dc);
                }

                if (user.Team != team)
                {
                    user.Team = team;
                }


                UserType t = dc.UserTypes.Where(x => x.UserTypeName == "Manager").FirstOrDefault();
                if (user.UserType.UserTypeName != "Admin")
                {
                    user.UserType = t; //Changes usertype to manager when made leader of a team
                }
                team.LeaderID = user.UserID;
                NotificationHandler.SendNotification(new Notification { User = user, Title = "Team Update", Body = "You were made team leader of " + team.TeamName },dc);
                
                foreach(User u in team.Users)
                {
                    if (u != user && u != previousLeader)
                    {
                        NotificationHandler.SendNotification(new Notification { User = u, Title = "Team Update", Body = user.Name + " has been made your team leader" },dc);
                    }
                }

                dc.SaveChanges();

                return new OkResult();
            }
        }
    }
}
