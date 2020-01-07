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
    public static class AddUserToTeam
    {
        [FunctionName("AddUserToTeam")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            req.Headers.TryGetValue("sessionToken", out StringValues sessionToken);
            User requester = SessionValidator.ValidateSession(sessionToken.ToString());
            if (requester == null || (requester.UserType.UserTypeName != "Admin" && requester.UserType.UserTypeName != "Manager"))
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

                teamID.userName = teamID.userName.Trim();

                User user = dc.User.Include(x => x.UserType).Include(x => x.Team).Where(x => x.Name == teamID.userName).FirstOrDefault();
                if (user == null)
                {
                    errors.Add("User does not exist");
                }


                Team team = dc.Team.Include(x=>x.Users).Where(x => x.TeamID == teamID.TeamID).FirstOrDefault();
                if (team == null)
                {
                    errors.Add("Team does not exist");
                }

                if (user != null && team != null && user.Team == team)
                {
                    errors.Add("User is already in team");
                }

                if (errors.Count > 0)
                {
                    return new BadRequestObjectResult(errors);
                }

                if (user.UserType.UserTypeName.Equals("Manager"))
                {
                    Team prevTeam = dc.Team.Where(x => x.TeamID == user.Team.TeamID).FirstOrDefault();
                    if (prevTeam != null)
                    {
                        prevTeam.LeaderID = null;
                    }
                    user.UserType = dc.UserTypes.Where(x => x.UserTypeName == "User").FirstOrDefault();
                }


                user.Team = team;
                NotificationHandler.SendNotification(new Notification { User = user, Title = "Team Update", Body = "You have been added to team " + team.ProjectName },dc);
                foreach (User u in team.Users)
                {
                    if (u != user)
                    {
                        NotificationHandler.SendNotification(new Notification { User = u, Title = "Team Update", Body = user.Name + " has been added to your team" },dc);
                    }
                }
                dc.SaveChanges();

                return new OkResult();
            }
        }
    }
}
