package com.joshhendo.numbersearch.structures;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.joshhendo.numbersearch.structures Copyright 2011
 * User: Josh
 * Date: 23/06/11
 * Time: 12:38 PM
 */
public class Contact
{
    String name;
    String street;
    String suburb;
    String postcode;
    String state;

    public Contact()
    {
        name = "";
        street = "";
        suburb = "";
        postcode = "";
        state = "";
    }

    public String getName()
    {
        return name;
    }

    public String getSurname()
    {
        Pattern pattern = Pattern.compile("(\\b\\w*$)");
        Matcher matcher = pattern.matcher(name);
        if ( matcher.find() )
            return matcher.group(1);
        return null;
    }

    public String getInitials()
    {
        return String.valueOf(name.charAt(0));
    }

    public String getLocation()
    {
        String toReturn = "";

        if (this.suburb != null && this.suburb.length() > 0) toReturn += this.suburb + " ";
        if (this.postcode != null && this.postcode.length() > 0) toReturn += this.postcode + " ";
        if (this.state != null && this.state.length() > 0) toReturn += this.state + " ";

        return toReturn.trim();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getSuburb()
    {
        return suburb;
    }

    public void setSuburb(String suburb)
    {
        this.suburb = suburb;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }
}
