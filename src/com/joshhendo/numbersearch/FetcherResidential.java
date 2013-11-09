package com.joshhendo.numbersearch;

import com.joshhendo.numbersearch.base.CommonFunctions;
import com.joshhendo.numbersearch.base.Fetcher;
import com.joshhendo.numbersearch.structures.ResidentialSearchEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.joshhendo.numbersearch Copyright 2011
 * User: Josh
 * Date: 18/08/11
 * Time: 2:18 AM
 */
public class FetcherResidential extends Fetcher<ResidentialSearchEntry> implements Serializable
{
    public FetcherResidential(String name, String initial, String location, Integer page)
    {
        this.name = name.replace(' ', '+');
        this.initial = initial.replace(' ', '+');
        this.location = location.replace(' ', '+');
        this.page = page;
        this.totalPages = null;
    }

    //@Override
    public ArrayList<ResidentialSearchEntry> process()
    {
        ArrayList<ResidentialSearchEntry> residentialSearchEntries = new ArrayList<ResidentialSearchEntry>();

        String strResponse = fetchPage("http://www.whitepages.com.au/resSearch.do?subscriberName=%s&givenName=%s&location=%s");

        String regex = "<div class=\"entry_title clearfix\"><h[0-9] class=\" \">(.*?)</h[0-9]>.*?<span class=\"phone_number   *.?\">([0-9\\(\\) ]*)</span>*.?<div class=\"address\"><span class=\"street_line\">(.*?)</span><span class=\"locality\">(.*?)</span><span class=\"state\">([A-Z]{2,3})</span><span class=\"postcode\">([0-9]{4})</span></div>";

        /*  Group 1: Name
        Group 2: Phone Number
        Group 3: Street Address
        Group 4: Suburb
        Group 5: State
        Group 6: Postcode */

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(strResponse);

        while ( matcher.find() )
        {
            residentialSearchEntries.add(new ResidentialSearchEntry(CommonFunctions.fixText(matcher.group(1)), matcher.group(2), CommonFunctions.fixText(matcher.group(3)),
                    matcher.group(4), matcher.group(5), matcher.group(6)));
        }

        // Also determine how many pages there are
        if ( this.page == 1 )
        {
            pattern = Pattern.compile("<li><a href=\\\".*\\\">([0-9]+)</a></li><li class=\\\"navigation_next\">");
            matcher = pattern.matcher(strResponse);

            if ( matcher.find() )
            {
                this.totalPages = Integer.parseInt(matcher.group(1));
            }
        }

        return residentialSearchEntries;
    }
}
