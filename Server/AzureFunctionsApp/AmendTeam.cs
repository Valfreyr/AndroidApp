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
    public static class AmendTeam
    {
        [FunctionName("AmmendTeam")]
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
            CreateTeamModel data;
            try
            {
                data = JsonConvert.DeserializeObject<CreateTeamModel>(requestBody);
            }
            catch
            {
                return new BadRequestResult();
            }

            using (DataContext dc = new DataContext())
            {
                List<Notification> n = new List<Notification>();
                //Links to the database
                if (data.TeamID == null)
                {
                    return new BadRequestResult();
                }

                Team original = dc.Team.Include(x=>x.Users).Where(x => x.TeamID == data.TeamID).FirstOrDefault();
                if (original == null)
                {
                    errors.Add("Team cannot be found");
                    return new BadRequestObjectResult(errors);
                }

                //Team Name Statement

                if (!string.IsNullOrWhiteSpace(data.TeamName))
                {
                    Team t = dc.Team.Where(x => x.TeamName == data.TeamName).FirstOrDefault();
                    if (t != null)
                    {
                        errors.Add("Team with that name already exists");
                    }
                    else
                    {
                        n.Add(new Notification { Title = "Team Update", Body = "Your team name has been changed to " + data.TeamName });
                        original.TeamName = data.TeamName;
                    }
                }

                //Project Name

                if (!string.IsNullOrWhiteSpace(data.ProjectName))
                {
                    original.ProjectName = data.ProjectName;
                    n.Add(new Notification { Title = "Team Update", Body = "Your team's project has been changed to " + data.ProjectName });
                }

                if (errors.Count > 0)
                {
                    return new BadRequestObjectResult(errors);
                }
                
                foreach (User u in original.Users)
                {
                    if (n.Count == 1)
                    {
                        Notification no = n.First();
                        NotificationHandler.SendNotification(new Notification { Title = no.Title, Body = no.Body, User = u },dc);
                    } 
                    else if(n.Count > 1)
                    {
                        NotificationHandler.SendNotification(new Notification { Title = "Team Update", Body = "Your team information has been updated", User = u},dc);
                    }
                }
                //Saves the changes.
                dc.SaveChanges();
            }
            return new JsonResult(data);
        }
    }
}
