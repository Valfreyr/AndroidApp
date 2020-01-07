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
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Primitives;
using System.Linq;

namespace AzureFunctionsApp
{
    public static class ReceiveAndroidToken
    {
        [FunctionName("ReceiveAndroidToken")]
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
            NotificationModel data;
            try
            {
                data = JsonConvert.DeserializeObject<NotificationModel>(requestBody);
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
                User u = dc.User.Where(x => x.UserID == requester.UserID).FirstOrDefault();
                if (u != null)
                {
                    u.PhoneToken = data.PhoneToken;
                }
                dc.SaveChanges();
            }
            return new OkResult();
        }
    }
}
