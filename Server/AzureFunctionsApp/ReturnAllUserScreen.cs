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
using System.Data;
using Microsoft.EntityFrameworkCore;
using System.Net;
using Microsoft.Extensions.Primitives;

namespace AzureFunctionsApp
{
    public static class ReturnAllUserScreen
    {
        [FunctionName("ReturnAllUserScreen")]
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
            List<CreateUserModel> cums = new List<CreateUserModel>();

            using (DataContext dc = new DataContext())
            {
                foreach (var User in dc.User.Include(x => x.Team).Include(x => x.Role).Include(x => x.Documents).Include(x => x.UserSkills).ThenInclude(y => y.Skill))
                {
                    string TeamID = null;
                    if(User.Team != null)
                    {
                        TeamID = User.Team.TeamID.ToString();
                    }
                    CreateUserModel cum = new CreateUserModel { Name = User.Name, Role = User.Role.Title, Skills = User.UserSkills.Select(x => x.Skill.SkillName).ToList(), TeamID = TeamID, UserID = User.UserID};
                    Document d = User.Documents.Where(x => x.IsProfilePicture).SingleOrDefault();
                    if (d != null)
                    {
                        var webClient = new WebClient();
                        byte[] f = webClient.DownloadData(d.FileLocation);
                        cum.ProfilePicture = f;
                    }
                    cums.Add(cum);
                }


            }
            return new JsonResult(cums);
        }

    }

}


