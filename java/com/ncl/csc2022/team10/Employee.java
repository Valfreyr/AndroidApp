/**
 * Class Description    : Class which holds all employee information
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

public class Employee implements Comparable<Employee>{

    // All Employee properties
    private String id;
    private String name;
    private String title;
    private String skills;
    private Bitmap profile_pic;
    String mobile, birthday, email, address, visaStatus, nextOfKin1, nextOfKin2, nationality, gender, medicalStatus, languages, maritalStatus;
    Integer teamID;

    /**
     * Empty constructor for Json conversion
     */
    public Employee() {
    }

    /**
     * Full class constructor
     * @param name Employee's name
     * @param title Employee's title/role
     * @param skills Employee's set of skills
     * @param profile_pic Employee's profile image
     * @param id Employee's ID
     * @param teamID Employee's team ID
     */
    public Employee(String name, String title, String skills, String profile_pic, String id, Integer teamID) {
        this.name = name;
        this.title = title;
        this.skills = skills;
        this.id = id;
        if(profile_pic!=null) {
            byte[] decodedString = Base64.decode(profile_pic, Base64.DEFAULT);

            this.profile_pic = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, null);

            Log.d("Employee","setting pp " + this.profile_pic.getByteCount());
        }
        this.teamID = teamID;
    }

    public Employee(String name, String title, String skills) {
        this.name = name;
        this.title = title;
        this.skills = skills;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getSkills() {
        return skills;
    }

    public Bitmap getProfile_pic() {
        return profile_pic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public void setProfile_pic(Bitmap profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVisaStatus() {
        return visaStatus;
    }

    public void setVisaStatus(String visaStatus) {
        this.visaStatus = visaStatus;
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

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
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

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getId() { return id; }

    public void setId(String id){ this.id = id; }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    @Override
    public int compareTo(@NonNull Employee e) {
        return getName().compareTo(e.getName());
    }
}