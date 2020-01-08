/**
 * Class Description    : Model for login request from the app to the server functions
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {
    @JsonProperty("Email")
    public String email;
    @JsonProperty("Password")
    public String password;

    /**
     * Login request constructor
     * @param email User's email
     * @param password User's passowrd
     */
    public LoginRequest(String email, String password){
        this.email = email;
        this.password = password;
    }
}
