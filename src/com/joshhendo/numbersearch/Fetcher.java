package com.joshhendo.numbersearch;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.com.joshhendo.joshhendo.joshhendo.numbersearch Copyright 2011
 * User: josh
 * Date: 17/06/11
 * Time: 10:10 PM
 */
public class Fetcher implements Serializable
{
    private final int TIMEOUT = 60000;

    private String name;
    private String initial;
    private String location;
    private Integer page;

    private Integer totalPages;

    public Integer getTotalPages()
    {
        return totalPages;
    }

    public void setPage(Integer page)
    {
        this.page = page;
    }

    public Fetcher(String name, String initial, String location, Integer page)
    {
        this.name = name.replace(' ', '+');
        this.initial = initial.replace(' ', '+');
        this.location = location.replace(' ', '+');
        this.page = page;
        this.totalPages = null;
    }

    ArrayList<Entry> process()
    {
        ArrayList<Entry> entries = new ArrayList<Entry>();

        String strResponse = fetchPage();

        String regex = "<div class=\"entry_title clearfix\"><h1 class=\" \">(.*?)</h1>.*?<span class=\"phone_number   *.?\">([0-9\\(\\) ]*)</span>*.?<div class=\"address\"><span class=\"street_line\">(.*?)</span><span class=\"locality\">(.*?)</span><span class=\"state\">([A-Z]{2,3})</span><span class=\"postcode\">([0-9]{4})</span></div>";

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
            entries.add(new Entry(fixText(matcher.group(1)), matcher.group(2), fixText(matcher.group(3)),
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

        return entries;
    }

    String fetchPage()
    {
        String url = String.format("http://www.whitepages.com.au/resSearch.do?subscriberName=%s&givenName=%s&location=%s", name, initial, location);

        if ( page > 1 )
            url += String.format("&page=%d", page);

                // Connection
        HttpClient httpClient = new DefaultHttpClient();

        // Set the connection timeout
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), TIMEOUT);

        System.out.println("URL: " + url);

        HttpGet httpGet = new HttpGet(url);

        String strResponse = null;
        try
        {
            HttpResponse response = httpClient.execute(httpGet);
            strResponse = inputStreamToString(response.getEntity().getContent()).toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return strResponse;
    }

    private StringBuilder inputStreamToString(InputStream is)
    {
        String line;
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        try
        {
            while ((line = rd.readLine()) != null)
            {
                total.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // Return full string
        return total;
    }

    private String fixText(String input)
    {
        input = input.replaceAll("&amp;", "&");
        input = input.replaceAll("&#039;", "'");
        return input.replaceAll("&amp;", "&");
    }

}
