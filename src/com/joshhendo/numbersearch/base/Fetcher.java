package com.joshhendo.numbersearch.base;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.*;
import java.util.ArrayList;

/**
 * com.com.joshhendo.joshhendo.joshhendo.numbersearch Copyright 2011
 * User: josh
 * Date: 17/06/11
 * Time: 10:10 PM
 */
public class Fetcher<T> implements Serializable
{
    protected final int TIMEOUT = 60000;

    protected String name;
    protected String initial;
    protected String location;
    protected Integer page;

    protected Integer totalPages;

    public Fetcher()
    {
    }

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

    ArrayList<T> process()
    {
        return null;
    }

    public String fetchPage(String iURL)
    {
        String url = String.format(iURL, name, initial, location);

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

    public StringBuilder inputStreamToString(InputStream is)
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
}
