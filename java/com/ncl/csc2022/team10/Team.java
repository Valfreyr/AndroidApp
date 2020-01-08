/**
 * Class Description    : Class which holds all information for a team
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Team {
    @JsonProperty("teamID")
    public Integer teamID;
    @JsonProperty("teamName")
    public String teamName;
    @JsonProperty("projectName")
    public String projectName;
    @JsonProperty("leaderID")
    public Integer leaderID;
    @JsonProperty("leaderName")
    public String leaderName;

    /**
     * Empty constructor for Json conversion
     */
    public Team(){

    }

    /**
     * Constructor for team
     * @param teamID Team's ID
     * @param teamName Team's name
     * @param projectName Project's name
     * @param leaderID Leader's ID
     * @param leaderName Leader's name
     */
    public Team(int teamID, String teamName, String projectName, Integer leaderID, String leaderName) {
        this.teamID = teamID;
        this.teamName = teamName;
        this.projectName = projectName;
        this.leaderID = leaderID;
        this.leaderName = leaderName;
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getLeaderID() {
        return leaderID;
    }

    public void setLeaderID(int leaderID) {
        this.leaderID = leaderID;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }
}


