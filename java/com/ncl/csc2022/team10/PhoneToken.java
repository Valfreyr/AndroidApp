/**
 * Class Description    : Holds the phone token from server after successful log in
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhoneToken {
    @JsonProperty("PhoneToken")
    public String phoneToken;

    /**
     * Constructor for phone token
     * Used for notifications
     * @param phoneToken Phone token
     */
    public PhoneToken(String phoneToken){
        this.phoneToken = phoneToken;
    }
}
