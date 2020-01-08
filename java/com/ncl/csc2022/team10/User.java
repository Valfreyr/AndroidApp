/**
 * Class Description    : Class which holds all information about a user
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @JsonProperty("userID")
    public Integer userID;
    @JsonProperty("name")
    public String name;
    @JsonProperty("role")
    public String role;
    @JsonProperty("mobile")
    public String mobile;
    @JsonProperty("dateOfBirth")
    public String dateOfBirth;
    @JsonProperty("userType")
    public UserType userType;
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
    @JsonProperty("team")
    public Team team;
    @JsonProperty("languages")
    public List<String> languages;
    @JsonProperty("skills")
    public List<String> skills;
    @JsonProperty("profilePicture")
    public String profilePicture;
    @JsonProperty("password")
    public String password;
    @JsonProperty("teamID")
    public Integer teamID;
    @JsonProperty("documents")
    public List<String> documents;

    public User(){ }

    public User(Integer userID, String name, Integer roleID, String role, String mobile, String dateOfBirth, UserType userType, String address,
                String email, String nextOfKin1, String nextOfKin2, String maritalStatus, String nationality, String visaStatus, String gender,
                String medicalStatus, List<String> languages, List<String> skills, String profilePicture, String password, Integer teamID, Team team) {
        this.userID = userID;
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
        this.teamID = teamID;
        this.team = team;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNextOfKin1() {
        return nextOfKin1;
    }

    public void setNextOfKin1(String nextOfKin1) {
        this.nextOfKin1 = nextOfKin1;
    }

    public String getNextOfKin2() {
        return nextOfKin2;
    }

    public void setNextOfKin2(String nextOfKin2) {
        this.nextOfKin2 = nextOfKin2;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getVisaStatus() {
        return visaStatus;
    }

    public void setVisaStatus(String visaStatus) {
        this.visaStatus = visaStatus;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMedicalStatus() {
        return medicalStatus;
    }

    public void setMedicalStatus(String medicalStatus) {
        this.medicalStatus = medicalStatus;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        languages = languages;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        skills = skills;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTeamID(){
        return teamID;
    }

    /**
     * Decode the profile picture
     * @param profilePicture profile picture string
     * @return Bitmap of the profile picture
     */
    public Bitmap getBitmapProfilePicture(String profilePicture){
        Bitmap pf = null;
        if(profilePicture!=null) {
            byte[] decodedString = Base64.decode(profilePicture, Base64.DEFAULT);

            pf = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, null);

            Log.d("User","setting pp " + pf.getByteCount());
        }
        return pf;
    }

    public String getLanguagesString(){
        return listToString(languages);
    }

    public String getSkillsString(){
        if(this.skills==null){
            return "";
        }
        Log.d("User", skills.toString());
        return listToString(skills.subList(0,skills.size()<3?skills.size():3));
    }

    private String listToString(List<String> l){
        if(l==null){
            return null;
        }
        StringBuilder ret = new StringBuilder();
        for(int i = 0; i < l.size(); i++){
            ret.append(l.get(i));
            if(i!=l.size()-1){
                ret.append(", ");
            }
        }
        return ret.toString();
    }
}
