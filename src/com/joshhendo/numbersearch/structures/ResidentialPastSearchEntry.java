package com.joshhendo.numbersearch.structures;

import java.io.Serializable;

/**
 * com.joshhendo.numbersearch.structures Copyright 2011
 * User: Josh
 * Date: 29/09/11
 * Time: 7:03 PM
 */
public class ResidentialPastSearchEntry implements Serializable
{
    private Integer _id;
    private String surname;
    private String initial;
    private String location;

    public ResidentialPastSearchEntry(Integer _id, String surname, String initial, String location)
    {
        this._id = _id;
        this.surname = surname;
        this.initial = initial;
        this.location = location;
    }

    public Integer getId()
    {
        return _id;
    }

    public String getSurname()
    {
        return surname;
    }

    public String getInitial()
    {
        return initial;
    }

    public String getLocation()
    {
        return location;
    }
}
