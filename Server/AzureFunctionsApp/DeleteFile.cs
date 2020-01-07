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
using Microsoft.Extensions.Primitives;

namespace AzureFunctionsApp
{
    public static class DeleteFile
    {
        [FunctionName("DeleteFile")]
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
                Document d = dc.Documents.Where(x => x.User.UserID==requester.UserID && x.FileName == data.FileName).FirstOrDefault();
                if (d != null)
                {
                    dc.Documents.Remove(d);
                }
                dc.SaveChanges();
            }

            return new OkResult();
        }
    }
}
