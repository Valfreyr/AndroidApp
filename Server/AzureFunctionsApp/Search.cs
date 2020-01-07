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
using System.Data;
using Microsoft.Azure.Search.Models;
using Microsoft.Azure.Search;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Primitives;

namespace AzureFunctionsApp
{
    public static class Search
    {
        [FunctionName("Search")]
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
            SearchModel data = JsonConvert.DeserializeObject<SearchModel>(requestBody);
            ISearchIndexClient indexClient = new SearchIndexClient(Environment.GetEnvironmentVariable("SearchServiceName"), "azuresql-index", new SearchCredentials(Environment.GetEnvironmentVariable("SearchServiceQueryApiKey")));

            using (DataContext dc = new DataContext())
            {
                var sp = new SearchParameters();

                if (!string.IsNullOrEmpty(data.Filter))
                {
                    sp.Filter = data.Filter;
                }

                DocumentSearchResult<User> reponses = indexClient.Documents.Search<User>(data.SearchString, sp);

                return new JsonResult(reponses);
            }

               
        }
    }
}
