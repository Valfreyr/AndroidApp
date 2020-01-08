/**
 * Class Description    : Initial model for team id information
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TeamIDModel {
    @JsonProperty("teamID")
    public String teamID;
    @JsonProperty("userName")
    public String userName;

    /**
     * Empty constructor for Json conversion
     */
    public TeamIDModel(){

    }

    /**
     * Constructor for teamIDModel
     * @param teamID Team's ID
     * @param userName User's name
     */
    public TeamIDModel(String teamID, String userName){
        this.teamID = teamID;
        this.userName = userName;
    }
}
