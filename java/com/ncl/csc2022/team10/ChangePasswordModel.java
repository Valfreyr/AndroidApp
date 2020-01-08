/**
 * Class Description    : Class which hold initial model for password change details
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangePasswordModel {
    @JsonProperty("email")
    public String email;
    @JsonProperty("oldPassword")
    public String oldPassword;
    @JsonProperty("newPassword")
    public String newPassword;

    /**
     * Empty constructor for Json conversion
     */
    public ChangePasswordModel(){

    }

    /**
     * Constructor used to pass information to the server when a password is changed
     * @param email User's current email
     * @param oldPassword User's current password
     * @param newPassword User's new password
     */
    public ChangePasswordModel(String email, String oldPassword, String newPassword){
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
