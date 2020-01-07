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
using Microsoft.Extensions.Primitives;
using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Blob;
using System.Linq;
using Microsoft.EntityFrameworkCore;

namespace AzureFunctionsApp
{
    public static class DownloadDocument
    {
        [FunctionName("DownloadDocument")]
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
            DownloadDocumentModel data;
            Document d;
            try
            {
                data = JsonConvert.DeserializeObject<DownloadDocumentModel>(requestBody);
            }
            catch
            {
                return new BadRequestResult();
            }

            using (DataContext dc = new DataContext())
            {
                User u = dc.User.Include(x => x.Documents).Where(x => x.UserID == data.UserID).FirstOrDefault();
                if (u == null)
                {
                    return new BadRequestObjectResult("User ID does not exist");
                }

                if(requester.UserType.UserTypeName!="Admin" && u.UserID != requester.UserID)
                {
                    return new StatusCodeResult(403);
                }

                d = u.Documents.Where(x =>x.User.UserID==u.UserID && x.FileName == data.FileName).FirstOrDefault();

                if (d == null)
                {
                    return new BadRequestObjectResult("File does not exist");
                }
            }
            return new JsonResult(d.FileLocation);
        }
    }
}
