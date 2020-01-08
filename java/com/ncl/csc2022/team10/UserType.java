/**
 * Class Description    : Class for initial user type information
 * Contributors         : Changda Pan, Elise Hosu, Eko Manggaprouw, Joseph Heath, Katarzyna Nozka
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserType {
    @JsonProperty("userTypeID")
    public Integer userTypeID;
    @JsonProperty("userTypeName")
    public String userTypeName;

    /**
     * Empty constructor for Json conversion
     */
    public UserType(){

    }

    /**
     * Constructor for UserType
     * @param userTypeID User Type ID
     * @param userTypeName User Type Name
     */
    public UserType(Integer userTypeID, String userTypeName) {
        this.userTypeID = userTypeID;
        this.userTypeName = userTypeName;
    }
}