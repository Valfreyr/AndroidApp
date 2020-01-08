/**
 * Class Description    : Class which hold initial model for new team details
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddTeamModel {
    @JsonProperty("teamID")
    public String teamID;
    @JsonProperty("teamName")
    public String teamName;
    @JsonProperty("projectName")
    public String projectName;
    @JsonProperty("leaderName")
    public String leaderName;

    /**
     * Empty constructor for Json conversion
     */
    public AddTeamModel(){

    }

    /**
     * Constructor for adding teams(used in AddTeam)
     * @param teamName Team's name
     * @param projectName Project's Name
     * @param leaderName Leader's name
     */
    public AddTeamModel(String teamName, String projectName, String leaderName) {
        this.teamName = teamName;
        this.projectName = projectName;
        this.leaderName = leaderName;
    }

    /**
     * Constructor used when amending teams
     * @param teamName Team's name
     * @param projectName Project's name
     * @param leaderName Leader's name
     * @param teamID Team's ID
     */
    public AddTeamModel(String teamName, String projectName, String leaderName, String teamID) {
        this.teamName = teamName;
        this.projectName = projectName;
        this.leaderName = leaderName;
        this.teamID = teamID;
    }
}
