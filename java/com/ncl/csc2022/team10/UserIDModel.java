/**
 * Class Description    : Class for initial user id model
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserIDModel {
    @JsonProperty("userID")
    private String userID;

    /**
     * Empty constructor for Json conversion
     */
    public UserIDModel(){

    }

    /**
     * User ID constructor
     * @param userID User's ID
     */
    public UserIDModel(String userID){
        this.userID = userID;
    }
}
