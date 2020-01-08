/**
 * Class Description    : Class which hold initial model for downloading document
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DownloadDocumentModel {
    @JsonProperty("userID")
    public String userID;
    @JsonProperty("fileName")
    public String fileName;

    /**
     * Empty constructor for Json conversion
     */
    public DownloadDocumentModel(){

    }

    /**
     * Constructor used when getting the download link for a document
     * @param userID User's ID
     * @param fileName Name of the file
     */
    public DownloadDocumentModel(String userID, String fileName){
        this.userID = userID;
        this.fileName = fileName;
    }
}
