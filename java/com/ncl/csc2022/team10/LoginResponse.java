/**
 * Class Description    : Login response details, including user's sessionToken from the server
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {

    @JsonProperty("user")
    public User user;
    @JsonProperty("sessionToken")
    public String sessionToken;

    /**
     * Empty constructor for Json conversion
     */
    public LoginResponse(){

    }

    /**
     * Constructor for login response
     * @param u User Object
     * @param sessionToken Session token
     */
    public LoginResponse(User u, String sessionToken){
        this.user = u;
        this.sessionToken = sessionToken;
    }
}
