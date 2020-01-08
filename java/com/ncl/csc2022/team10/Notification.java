/**
 * Class Description    : Notification details for notification messages
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notification {
    @JsonProperty("notificationID")
    public int notificationID;
    @JsonProperty("title")
    public String titlte;
    @JsonProperty("body")
    public String body;
    @JsonProperty("date")
    public String date;

    /**
     * Empty constructor for Json conversion
     */
    public Notification(){

    }

    /**
     * Constructor for notification
     * @param notificationID Notification's ID
     * @param title Notification's title
     * @param body Notification's body message
     * @param date Notification's date
     */
    public Notification(int notificationID, String title, String body, String date){
        this.notificationID = notificationID;
        this.titlte = title;
        this.body = body;
        this.date = date;
    }
}
