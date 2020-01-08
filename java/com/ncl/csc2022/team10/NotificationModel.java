/**
 * Class Description    : Initial model for notification messages
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationModel {
    @JsonProperty("body")
    public String body;
    @JsonProperty("date")
    public String date;

    /**
     * Empty constructor for Json conversion
     */
    public NotificationModel(){

    }

    /**
     * Constructor for notification model (used when getting data from the server)
     * @param body Notification's body
     * @param date Notification's date
     */
    public NotificationModel(String body, String date){
        this.body = body;
        this.date = date;
    }
}
