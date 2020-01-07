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
    public static class CreateTeam
    {
        [FunctionName("CreateTeam")]
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
            CreateTeamModel data;
            try
            {
                data = JsonConvert.DeserializeObject<CreateTeamModel>(requestBody);
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
                if (string.IsNullOrWhiteSpace(data.TeamName))
                {
                    errors.Add("Team name cannot be empty");
                }
                else
                {
                    Team t = dc.Team.Where(x => x.TeamName == data.TeamName).FirstOrDefault();
                    if (t != null)
                    {
                        errors.Add("Team with that name already exists");
                    }
                }

                User u = null;
                if (!string.IsNullOrEmpty(data.LeaderName))
                {
                    u = dc.User.Where(x => x.Name.ToLower() == data.LeaderName.ToLower()).FirstOrDefault();
                    if (u == null)
                    {
                        errors.Add("The specified leader does not exist");
                    }
                }

                if (errors.Count > 0)
                {
                    return new BadRequestObjectResult(errors);
                }

                User user = dc.User.Include(x => x.Team).Where(x => x.Name.ToLower() == data.LeaderName.ToLower()).FirstOrDefault();
                if (user != null && user.Team != null)
                {
                    Team removeManager = dc.Team.Where(x => x.TeamID == user.Team.TeamID).FirstOrDefault();
                    if( removeManager != null)
                    {
                        removeManager.LeaderID = null;
                    }
                }

                Team team = new Team
                {
                    TeamName = data.TeamName,
                    ProjectName = data.ProjectName
                };

                if (u != null)
                {
                    team.LeaderID = u.UserID;
                    u.Team = team;
                }

                dc.Team.Add(team);
                dc.SaveChanges();

                return new OkResult();
            }
        }
    }
}
