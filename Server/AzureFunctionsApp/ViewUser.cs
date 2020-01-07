using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using System.Collections.Generic;
using AzureFunctionsApp.Models;
using System.Linq;
using Microsoft.Extensions.Primitives;
using Microsoft.EntityFrameworkCore;
using System.Net;

namespace AzureFunctionsApp
{
    public static class ViewUser
    {
        [FunctionName("ViewUser")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            req.Headers.TryGetValue("sessionToken", out StringValues sessionToken);
            User requester = SessionValidator.ValidateSession(sessionToken.ToString());
            if (requester == null)
            {
                return new StatusCodeResult(403);
            }
            List<string> errors = new List<string>();
            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            UserIDModel data;
            CreateUserModel user;
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
                User u = dc.User.Include(x=>x.UserLanguages).ThenInclude(x=>x.Language).Include(x=>x.UserSkills).ThenInclude(x=>x.Skill).Include(x=>x.Documents).Include(x=>x.Team).Include(x=>x.MaritalStatus).Include(x=>x.Role).Where(x => x.UserID == data.UserID).FirstOrDefault();
                if (u == null)
                {
                    errors.Add("User does not exist");
                }

                if (errors.Count > 0)
                {
                    return new BadRequestObjectResult(errors);
                }
                
                String MaritalStatus = null;
                if(u.MaritalStatus != null)
                {
                    MaritalStatus = u.MaritalStatus.MaritalStatusName;
                }
                user = new CreateUserModel
                {
                    Address = u.Address,
                    DateOfBirth = u.DateOfBirth,
                    Email = u.Email,
                    Gender = u.Gender,
                    Languages = u.UserLanguages.Select(x => x.Language.LanguageName).ToList(),
                    Skills = u.UserSkills.Select(x => x.Skill.SkillName).ToList(),
                    MaritalStatus = MaritalStatus,
                    MedicalStatus = u.MedicalStatus,
                    Mobile = u.Mobile,
                    Name = u.Name,
                    Nationality = u.Nationality,
                    NextOfKin1 = u.NextOfKin1,
                    NextOfKin2 = u.NextOfKin2,
                    Role = u.Role.Title,
                    VisaStatus = u.VisaStatus,
                    UserID = u.UserID,
                    TeamID = u.Team?.TeamID.ToString(),
                    Documents = u.Documents.Where(x => !x.IsProfilePicture).Select(x => x.FileName.ToString()).ToList()
                };
                Document d = u.Documents.Where(x => x.IsProfilePicture).SingleOrDefault();
                if (d != null)
                {
                    var webClient = new WebClient();
                    byte[] f = webClient.DownloadData(d.FileLocation);
                    user.ProfilePicture = f;
                }

            }

            return new JsonResult(user);
        }
    }
}
