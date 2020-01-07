using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Security.Cryptography;
using System.Text;

namespace AzureFunctionsApp.Models
{
    public class Session
    {
        [Key]
        public int SessionTokenID { get; set; }
        [Required]
        public string SessionToken { get; set; } = GetNewSessionToken();
        public User User { get; set; }

        public static string GetNewSessionToken()
        {
            byte[] random = new byte[256];
            RNGCryptoServiceProvider rng = new RNGCryptoServiceProvider();
            rng.GetNonZeroBytes(random);
            return Convert.ToBase64String(random);
        }
    }
}
