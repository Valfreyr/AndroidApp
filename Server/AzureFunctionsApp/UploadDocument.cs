using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Microsoft.Azure;
using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Blob;
using AzureFunctionsApp.Models;
using Microsoft.Extensions.Primitives;
using System.Linq;

namespace AzureFunctionsApp
{
    public static class UploadDocument
    {
        [FunctionName("UploadDocument")]
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
            UserIDModel data = new UserIDModel();
            bool isProfilePicture;
            try
            {
                req.Form.TryGetValue("id", out StringValues id);
                req.Form.TryGetValue("isProfilePicture", out StringValues ProfilePicture);
                bool.TryParse(ProfilePicture, out isProfilePicture);
                data.UserID = Convert.ToInt32(id.ToString());
            }
            catch
            {
                return new BadRequestResult();
            }

            using (DataContext dc = new DataContext())
            {
                User u = dc.User.Where(x => x.UserID == data.UserID).FirstOrDefault();
                if (u == null)
                {
                    return new BadRequestResult();
                }
                if(requester.UserType.UserTypeName!="Admin" && requester.UserID != u.UserID)
                {
                    return new StatusCodeResult(403);
                }

                var f = req.Form.Files[0];

                // Create Reference to Azure Storage Account
                string strorageconn = "DefaultEndpointsProtocol=https;AccountName=team10projecta916;AccountKey=4V9RYDcZUjn442xOgaSW/RwC7mzB+zguDgfHM0tyqff8cSeomPMj6cvOA2ATav0Vor2eoAR64A7L+DCsFDrZCw==;EndpointSuffix=core.windows.net";
                CloudStorageAccount storageacc = CloudStorageAccount.Parse(strorageconn);

                //Create Reference to Azure Blob
                CloudBlobClient blobClient = storageacc.CreateCloudBlobClient();

                //The next 2 lines create if not exists a container named "democontainer"
                CloudBlobContainer container = blobClient.GetContainerReference("user" + data.UserID.ToString());

                await container.CreateIfNotExistsAsync();
                await container.SetPermissionsAsync(new BlobContainerPermissions { PublicAccess = BlobContainerPublicAccessType.Blob });
                //The next 7 lines upload the file 
                CloudBlockBlob blockBlob = container.GetBlockBlobReference(f.FileName);
                using (var filestream = f.OpenReadStream())
                {

                    await blockBlob.UploadFromStreamAsync(filestream);

                }
                if (isProfilePicture)
                {
                    Document doc = dc.Documents.Where(x => x.IsProfilePicture && x.User == u).FirstOrDefault();
                    if (doc != null)
                    {
                        dc.Documents.Remove(doc);
                    }
                }

                Document d = dc.Documents.Where(x => x.FileName == f.FileName).FirstOrDefault();
                if (d != null)
                {
                    dc.Documents.Remove(d);
                }

                dc.Documents.Add(new Document { User = u, FileName = f.FileName, FileLocation = blockBlob.Uri.AbsoluteUri, IsProfilePicture = isProfilePicture });
                dc.SaveChanges();
            }

            return new OkResult();
        }
    }
}
