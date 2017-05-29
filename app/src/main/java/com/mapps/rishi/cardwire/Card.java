package com.mapps.rishi.cardwire;

/**
 * Created by rishi on 2017-01-21.
 */

public class Card {
    private String type;
    private String link;

    public Card(String type, String link) {
        super();
        this.type = type;
        this.link = link;
    }

    //Copy Constructor
    public Card (Card c){
        this.type = c.getType();
        this.link = c.getLink();
    }

    public String getType (){
        return this.type;
    }

    public String getLink (){
        return this.link;
    }

    public void setType (String type){
        this.type = type;
    }

    public void setLink (String link){
        this.link = link;
    }
}
