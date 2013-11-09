package com.joshhendo.numbersearch;

import java.io.Serializable;

/**
 * com.com.joshhendo.joshhendo.joshhendo.numbersearch Copyright 2011
 * User: josh
 * Date: 17/06/11
 * Time: 5:39 PM
 */
public class Entry implements Serializable
{
    private String name;
    private String phoneNumber;
    private String streetAddress;
    private String suburb;
    private String state;
    private String postcode;

    public Entry(String name, String phoneNumber, String streetAddress, String suburb, String state, String postcode)
    {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.streetAddress = streetAddress;
        this.suburb = suburb;
        this.state = state;
        this.postcode = postcode;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getStreetAddress()
    {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
    }

    public String getSuburb()
    {
        return suburb;
    }

    public void setSuburb(String suburb)
    {
        this.suburb = suburb;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }
}
