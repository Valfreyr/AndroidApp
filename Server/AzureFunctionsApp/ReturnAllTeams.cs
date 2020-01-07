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
using Microsoft.Extensions.Primitives;
using Microsoft.EntityFrameworkCore;
using System.Linq;

namespace AzureFunctionsApp
{
    public static class ReturnAllTeams
    {
        [FunctionName("ReturnAllTeams")]
        public static IActionResult Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            req.Headers.TryGetValue("sessionToken", out StringValues sessionToken);
            User requester = SessionValidator.ValidateSession(sessionToken.ToString());
            if (requester == null)
            {
                return new StatusCodeResult(403);
            }
            List<CreateTeamModel> ctms = new List<CreateTeamModel>();

            using (DataContext dc = new DataContext())
            {
                var teams = dc.Team.Include(x => x.Users).Where(x => x.Users.Contains(requester) || requester.UserType.UserTypeName == "Admin");
                foreach (var Team in teams)
                {
                    CreateTeamModel ctm = new CreateTeamModel { TeamName = Team.TeamName, LeaderID = Team.LeaderID, ProjectName = Team.ProjectName, TeamID = Team.TeamID};
                    ctms.Add(ctm);
                }

            }
            return new JsonResult(ctms);
        }
    }
}
