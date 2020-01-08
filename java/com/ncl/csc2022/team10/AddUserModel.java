/**
 * Class Description    : Class which hold initial model for new user details
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AddUserModel {
    @JsonProperty("name")
    public String name;
    @JsonProperty("role")
    public String role;
    @JsonProperty("mobile")
    public String mobile;
    @JsonProperty("dateOfBirth")
    public String dateOfBirth;
    @JsonProperty("userType")
    public String userType;
    @JsonProperty("address")
    public String address;
    @JsonProperty("email")
    public String email;
    @JsonProperty("nextOfKin1")
    public String nextOfKin1;
    @JsonProperty("nextOfKin2")
    public String nextOfKin2;
    @JsonProperty("maritalStatus")
    public String maritalStatus;
    @JsonProperty("nationality")
    public String nationality;
    @JsonProperty("visaStatus")
    public String visaStatus;
    @JsonProperty("gender")
    public String gender;
    @JsonProperty("medicalStatus")
    public String medicalStatus;
    @JsonProperty("languages")
    public List<String> languages;
    @JsonProperty("skills")
    public List<String> skills;
    @JsonProperty("profilePicture")
    public String profilePicture;
    @JsonProperty("password")
    public String password;

    /**
     * Empty constructor for Json conversion
     */
    public AddUserModel(){

    }

    /**
     * Constructor for adding users
     * @param name User's name
     * @param role User's role
     * @param mobile User's mobile number
     * @param dateOfBirth User's date of birth
     * @param userType User's type (admin/manager/user)
     * @param address User's address
     * @param email User's email
     * @param nextOfKin1 User's first next of kin
     * @param nextOfKin2 User's second next of kin
     * @param maritalStatus User's marital status
     * @param nationality User's nationality
     * @param visaStatus User's visa status
     * @param gender User's gender
     * @param medicalStatus User's medical status
     * @param languages Languages spoken by the user
     * @param skills Skills known by the user
     * @param profilePicture User's profile picture
     * @param password User's password
     */
    public AddUserModel(String name, String role, String mobile, String dateOfBirth, String userType, String address, String email,
                        String nextOfKin1, String nextOfKin2, String maritalStatus, String nationality, String visaStatus, String gender,
                        String medicalStatus, List<String> languages, List<String> skills, String profilePicture, String password) {
        this.name = name;
        this.role = role;
        this.mobile = mobile;
        this.dateOfBirth = dateOfBirth;
        this.userType = userType;
        this.address = address;
        this.email = email;
        this.nextOfKin1 = nextOfKin1;
        this.nextOfKin2 = nextOfKin2;
        this.maritalStatus = maritalStatus;
        this.nationality = nationality;
        this.visaStatus = visaStatus;
        this.gender = gender;
        this.medicalStatus = medicalStatus;
        this.languages = languages;
        this.skills = skills;
        this.profilePicture = profilePicture;
        this.password = password;
    }
}
