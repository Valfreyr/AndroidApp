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
using Microsoft.EntityFrameworkCore;

namespace AzureFunctionsApp
{
    public static class RemoveUserFromTeam
    {
        [FunctionName("RemoveUserFromTeam")]
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

                Team team = dc.Team.Include(x=>x.Users).Where(x => x.TeamID == teamID.TeamID).FirstOrDefault();
                if (team == null)
                {
                    errors.Add("Team does not exist");
                }

                if (errors.Count > 0)
                {
                    return new BadRequestObjectResult(errors);
                }

                foreach(User u in team.Users)
                {
                    if (u.UserID != team.LeaderID)
                    {
                        dc.User.Where(x => x.UserID == u.UserID).Single().Team = null;
                    }
                }

                team.Users = new List<User> { dc.User.Where(x => x.UserID == team.LeaderID).Single() };
                
                dc.SaveChanges();

                return new OkResult();
            }
        }
    }
}
