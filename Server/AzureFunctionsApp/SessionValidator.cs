using AzureFunctionsApp.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AzureFunctionsApp
{
    public class SessionValidator
    {
        public static User ValidateSession(string sessionToken)
        {
            using (DataContext dc = new DataContext())
            {
                var sesh = dc.Sessions.Include(x=>x.User).ThenInclude(x=>x.UserType).Where(x => x.SessionToken == sessionToken).FirstOrDefault();
                if (sesh == null)
                {
                    return null; 
                }
                else
                {
                    return sesh.User;
                }
            }
        }

        public static string ComputeHash(string password, string salt)
        {
            //Hashing inputted password
            var sha = new System.Security.Cryptography.SHA256Managed();
            byte[] textData = System.Text.Encoding.UTF8.GetBytes(password + salt);
            byte[] hash = sha.ComputeHash(textData);

            string hashedPassword = Convert.ToBase64String(hash);
            return hashedPassword;
        }
    }
}
