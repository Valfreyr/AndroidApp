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

namespace AzureFunctionsApp
{
    public static class AddUser
    {
        [FunctionName("AddUser")]
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
            CreateUserModel data;
            try
            {
                data = JsonConvert.DeserializeObject<CreateUserModel>(requestBody);
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

                if (data.Mobile == null || data.Mobile.Length > 13)
                {
                    errors.Add("Mobile number must be less than 13 digits.");
                }

                if (string.IsNullOrWhiteSpace(data.Email))
                {
                    errors.Add("E-mail cannot be empty");
                }

                if (string.IsNullOrWhiteSpace(data.Name))
                {
                    errors.Add("Name cannot be empty");
                }

                UserType u = dc.UserTypes.Where(x => x.UserTypeName == data.UserType).FirstOrDefault();
                if (u == null)
                {
                    errors.Add("User type does not exist");
                }

                if(string.IsNullOrWhiteSpace(data.Role))
                {
                    errors.Add("User must have a role");
                }

                if (errors.Count > 0)
                {
                    return new BadRequestObjectResult(errors);
                }

                // checking for existing element; create if it doesn't exist
                Role r = dc.Roles.Where(x => x.Title == data.Role).FirstOrDefault();
                if (r == null)
                {
                    r = new Role { Title = data.Role };
                    dc.Roles.Add(r);
                }

                MaritalStatus m = dc.MaritalStatuses.Where(x => x.MaritalStatusName == data.MaritalStatus).FirstOrDefault();
                if (m == null)
                {
                    if (data.MaritalStatus != null)
                    {
                        m = new MaritalStatus { MaritalStatusName = data.MaritalStatus };
                        dc.MaritalStatuses.Add(m);
                    }
                }

                User user = new User
                {
                    Name = data.Name,
                    Mobile = data.Mobile,
                    DateOfBirth = data.DateOfBirth,
                    Role = r,
                    UserType = u,
                    Address = data.Address,
                    Email = data.Email,
                    NextOfKin1 = data.NextOfKin1,
                    NextOfKin2 = data.NextOfKin2,
                    MaritalStatus = m,
                    Nationality = data.Nationality,
                    VisaStatus = data.VisaStatus,
                    Gender = data.Gender,
                    MedicalStatus = data.MedicalStatus,
                    DateTimeUpdated = DateTime.Now

                };
                dc.User.Add(user);
                
                if(data.Languages != null)
                {
                    foreach (string language in data.Languages)
                    {
                        Language l = dc.Languages.Where(x => x.LanguageName == language).FirstOrDefault();
                        if (l == null)
                        {
                            l = new Language { LanguageName = language };
                            dc.Languages.Add(l);
                        }
                        dc.UserLanguages.Add(new UserLanguage { Language = l, User = user });
                    }
                }
               
                if(data.Skills != null)
                {
                    foreach (string skill in data.Skills)
                    {
                        Skill s = dc.Skills.Where(x => x.SkillName == skill).FirstOrDefault();
                        if (s == null)
                        {
                            s = new Skill { SkillName = skill };
                            dc.Skills.Add(s);
                        }
                        dc.UserSkills.Add(new UserSkill { Skill = s, User = user });
                    }
                }
              

                string salt = Convert.ToBase64String(Guid.NewGuid().ToByteArray());
                string hashedPassword = SessionValidator.ComputeHash(data.Password, salt);

                Authentication login = new Authentication
                {
                    PasswordHash = hashedPassword,
                    User = user,
                    Salt = salt
                };
                dc.Authentication.Add(login);

                dc.SaveChanges();

                return new OkResult();
            }

        }

    }
}
