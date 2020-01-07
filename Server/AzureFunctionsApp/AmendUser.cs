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
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Primitives;
using System.Net;
using System.Text;
using System.Collections.Generic;

namespace AzureFunctionsApp
{
    public static class AmendUser
    {
        [FunctionName("AmendUser")]
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

            using (DataContext dc = new DataContext())
            {
                List<Notification> n = new List<Notification>();
                //Links to the database
                User original = dc.User.Include(x=>x.MaritalStatus).Include(x=>x.Role).Where(x => x.UserID == data.UserID).FirstOrDefault();
                if (original == null)
                {
                    return new BadRequestResult();
                }

                if(requester.UserType.UserTypeName!="Admin" && requester.UserID != original.UserID)
                {
                    return new StatusCodeResult(403);
                }

                //Each if statement checks if a new value has been inserted (IE, not null) then pushes for the change to be made.
                //Name Statement

                if (string.IsNullOrWhiteSpace(data.Name) == false && original.Name != data.Name)
                {
                    original.Name = data.Name;
                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your name has been changed to " + data.Name.ToString() });
                }

                //Mobile If statement

                if (data.Mobile != null && data.Mobile.Length < 13 && original.Mobile != data.Mobile)
                {
                    original.Mobile = data.Mobile;
                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your mobile number has been changed to " + data.Mobile.ToString() });
                }
                //DoB

                if (data.DateOfBirth != null && original.DateOfBirth != data.DateOfBirth)
                {
                    original.DateOfBirth = data.DateOfBirth;
                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your date of birth has been changed to " + data.DateOfBirth.Value.ToString("dd/MM/yyyy") });
                }

                //Role

                if (string.IsNullOrWhiteSpace(data.Role) == false && original.Role.Title != data.Role)
                {

                    Role r = dc.Roles.Where(x => x.Title == data.Role).FirstOrDefault();
                    if (r == null)
                    {
                        r = new Role { Title = data.Role };
                        dc.Roles.Add(r);
                        n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your role has been changed to " + data.Role.ToString() });
                    }

                    original.Role = r;

                }

                //Address

                if (string.IsNullOrWhiteSpace(data.Address) == false && original.Address != data.Address)
                {
                    original.Address = data.Address;
                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your address has been changed to " + data.Address });
                }

                //Email

                if (string.IsNullOrWhiteSpace(data.Email) == false && original.Email != data.Email)
                {
                    original.Email = data.Email;
                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your email has been changed to " + data.Email });
                }

                //NoK1

                if (string.IsNullOrWhiteSpace(data.NextOfKin1) == false && original.NextOfKin1 != data.NextOfKin1)
                {
                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your next of kin has been changed to " + data.NextOfKin1 });
                    original.NextOfKin1 = data.NextOfKin1;
                }

                //NoK2

                if (string.IsNullOrWhiteSpace(data.NextOfKin2) == false && original.NextOfKin2 != data.NextOfKin2)
                {
                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your next of kin has been changed to " + data.NextOfKin2 });
                    original.NextOfKin2 = data.NextOfKin2;
                }

                //Marital Status

                if (string.IsNullOrWhiteSpace(data.MaritalStatus) == false)
                {
                    MaritalStatus m = dc.MaritalStatuses.Where(x => x.MaritalStatusName == data.MaritalStatus).FirstOrDefault();
                    if (m == null)
                    {
                        dc.MaritalStatuses.Add(new MaritalStatus { MaritalStatusName = data.MaritalStatus });
                    }

                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your marital status has been changed to " + data.MaritalStatus });
                    original.MaritalStatus = m;
                }

                //Nationality

                if (string.IsNullOrWhiteSpace(data.Nationality) == false && original.Nationality != data.Nationality)
                {
                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your nationality has been changed to " + data.Nationality });
                    original.Nationality = data.Nationality;
                }

                //Visa Status

                if (string.IsNullOrWhiteSpace(data.VisaStatus) == false && original.VisaStatus != data.VisaStatus)
                {
                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your visa status has been changed to " + data.VisaStatus });
                    original.VisaStatus = data.VisaStatus;
                }

                //Gender

                if (string.IsNullOrWhiteSpace(data.Gender) == false && original.Gender != data.Gender)
                {
                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your gender has been changed to " + data.Gender });
                    original.Gender = data.Gender;
                }

                //Medical Status

                if (string.IsNullOrWhiteSpace(data.MedicalStatus) == false && original.MedicalStatus != data.MedicalStatus)
                {
                    n.Add(new Notification { User = original, Title = "Profile Update", Body ="Your medical status has been changed to " + data.MedicalStatus });
                    original.MedicalStatus = data.MedicalStatus;
                }

                //UserLanguages
                if (data.Languages != null)
                {
                    dc.UserLanguages.Include(x=>x.Language).Where(x => x.User.UserID == data.UserID).ToList().ForEach(x => {
                        if(!data.Languages.Contains(x.Language.LanguageName))
                        dc.UserLanguages.Remove(x);
                        });

                    foreach (var language in data.Languages)
                    {
                        Language l = dc.Languages.Where(x => x.LanguageName == language).FirstOrDefault();
                        if (l == null)
                        {
                            l = new Language { LanguageName = language };
                            dc.Languages.Add(l);
                        }

                        UserLanguage ul = dc.UserLanguages.Include(x => x.Language).Include(x => x.User).Where(x => x.Language == l && x.User.UserID == data.UserID).FirstOrDefault();

                        if (ul == null)
                        {
                            n.Add(new Notification { User = original, Title = "Profile Update", Body ="A new language has been added " + data.Languages.ToString() });
                            dc.UserLanguages.Add(new UserLanguage { Language = l, User = dc.User.Where(x => x.UserID == data.UserID).FirstOrDefault() });
                        }
                    }
                }

                //User Skills
                if (data.Skills != null)
                {
                    dc.UserSkills.Include(x=>x.Skill).Where(x => x.User.UserID == data.UserID).ToList().ForEach(x=> {
                        if (!data.Skills.Contains(x.Skill.SkillName))
                            dc.UserSkills.Remove(x);
                        });
                    
                    foreach (var skill in data.Skills)
                    {
                        Skill s = dc.Skills.Where(x => x.SkillName == skill).FirstOrDefault();
                        if (s == null)
                        {
                            s = new Skill { SkillName = skill };
                            dc.Skills.Add(s);
                        }

                        UserSkill us = dc.UserSkills.Include(x => x.Skill).Include(x => x.User).Where(x => x.Skill == s && x.User.UserID == data.UserID).FirstOrDefault();
                        
                        if(us == null)
                        {
                            n.Add(new Notification { User = original, Title = "Profile Update", Body ="A new skill has been added " + data.Skills.ToString() });
                            dc.UserSkills.Add(new UserSkill { Skill = s, User = dc.User.Where(x => x.UserID == data.UserID).FirstOrDefault() });
                        }
                    }
                }

                original.DateTimeUpdated = DateTime.Now;
                if (n.Count == 1)
                {
                    NotificationHandler.SendNotification(n.First(),dc); 
                }
                else if(n.Count > 1)
                {
                    NotificationHandler.SendNotification(new Notification { Title = "Profile Update", Body = "Your profile has been udpated", User = original },dc);
                }
                //Saves the changes.
                dc.SaveChanges();
            }
            return new JsonResult(data);

        }
    }
}
