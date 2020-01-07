using AzureFunctionsApp.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace AzureFunctionsApp
{
    public class DataContext : DbContext
    {
        public DataContext() : base(GetOptions())
        {

        }
        public DataContext(DbContextOptions options) : base(options)
        {

        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<MaritalStatus>();
            modelBuilder.Entity<Role>();
            modelBuilder.Entity<UserType>();
            modelBuilder.Entity<Language>().HasMany(x => x.UserLanguages).WithOne(y => y.Language);
            modelBuilder.Entity<Skill>().HasMany(x => x.UserSkills).WithOne(y => y.Skill);

            modelBuilder.Entity<User>().HasOne(x => x.MaritalStatus).WithMany(y => y.Users);
            modelBuilder.Entity<User>().HasOne(x => x.Role).WithMany(y => y.Users);
            modelBuilder.Entity<User>().HasOne(x => x.UserType).WithMany(y => y.Users);
            modelBuilder.Entity<User>().HasMany(x => x.UserLanguages).WithOne(y => y.User);
            modelBuilder.Entity<User>().HasMany(x => x.UserSkills).WithOne(y => y.User);
            modelBuilder.Entity<User>().HasMany(x => x.Documents).WithOne(y => y.User);
            modelBuilder.Entity<User>().HasMany(x => x.Notifications).WithOne(y => y.User);
            modelBuilder.Entity<User>().HasOne(x => x.Team).WithMany(y => y.Users);

            modelBuilder.Entity<Session>().HasOne(x => x.User).WithMany(x => x.Sessions);

            modelBuilder.Entity<Authentication>().HasOne(x => x.User).WithOne(x => x.Authentication);
        }

        private static DbContextOptions GetOptions()
        {
            string connectionString = Environment.GetEnvironmentVariable("sqldb_connection");
            if (string.IsNullOrEmpty(connectionString))
                throw new ArgumentException(
             $"{nameof(connectionString)} is null or empty.",
             nameof(connectionString));

            var optionsBuilder =
                 new DbContextOptionsBuilder<DataContext>();

            optionsBuilder.UseSqlServer(connectionString);
            return optionsBuilder.Options;
        }

        public DbSet<MaritalStatus> MaritalStatuses { get; set; }
        public DbSet<Role> Roles { get; set; }
        public DbSet<UserType> UserTypes { get; set; }
        public DbSet<User> User { get; set; }
        public DbSet<Skill> Skills { get; set; }
        public DbSet<Language> Languages { get; set; }
        public DbSet<UserLanguage> UserLanguages { get; set; }
        public DbSet<UserSkill> UserSkills { get; set; }
        public DbSet<Document> Documents { get; set; }
        public DbSet<Team> Team { get; set; }
        public DbSet<Session> Sessions { get; set; }
        public DbSet<Authentication> Authentication { get; set; } //ADDING AUTHENTICATION TABLE
        public DbSet<Notification> Notification { get; set; }
    }
}

