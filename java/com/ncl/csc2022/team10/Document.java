/**
 * Class Description    : Details for a document object
 * Contributors         : Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

public class Document {

    // Document properties
    private String name;
    private String date;

    /**
     * Constructor for Document class
     * @param name Document's name
     * @param date Document's date
     */
    public Document(String name, String date) {
        this.name = name;
        this.date = date;
    }

    /**
     * Getting document's name
     * @return Name set for the document
     */
    public String getName() {
        return name;
    }

    /**
     * Setting document's name
     * @param name New Document's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getting document's date
     * @return Date set for the document
     */
    public String getDate() {
        return date;
    }

    /**
     * Setting document's date
     * @param date New Document's date
     */
    public void setDate(String date) {
        this.date = date;
    }
}
