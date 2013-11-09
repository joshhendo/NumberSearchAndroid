package com.joshhendo.numbersearch;

import com.joshhendo.numbersearch.base.Fetcher;
import com.joshhendo.numbersearch.structures.BusinessSearchEntry;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.com.joshhendo.joshhendo.joshhendo.numbersearch Copyright 2011
 * User: josh
 * Date: 17/06/11
 * Time: 10:10 PM
 */
public class FetcherBusiness extends Fetcher<String> implements Serializable
{
    public FetcherBusiness(String name, String location, Integer page)
    {
        this.name = name;
        this.location = location;
        this.page = page;
        this.totalPages = null;
    }

    private final String ANDROID_USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.3; en-us) AppleWebKit/999+ (KHTML, like Gecko) Safari/999.9";
    private final String SEARCH_URL = "http://mobile.whitepages.com.au/search/doSearch.action";

    public ArrayList<BusinessSearchEntry> fetchBusinessNames(String iURL, List<NameValuePair> paramaters)
    {
        ArrayList<BusinessSearchEntry> searchEntries = new ArrayList<BusinessSearchEntry>();

        try
        {
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            HttpClient client = new DefaultHttpClient();
            HttpProtocolParams.setUserAgent(client.getParams(), ANDROID_USER_AGENT);
            HttpPost post = new HttpPost("http://mobile.whitepages.com.au/");

            client.execute(post, localContext);
            cookieStore.getCookies();

            post = new HttpPost(SEARCH_URL );
            
            post.addHeader("Referer", "http://mobile.whitepages.com.au/");
            post.setHeader("User Agent", ANDROID_USER_AGENT);
            post.addHeader("X-Requested-With", "XMLHttpRequest");
            post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            post.addHeader("Pragma", "no-cache");
            post.addHeader("Cache-Control", "no-cache");
            post.setEntity(new UrlEncodedFormEntity(paramaters));
            HttpResponse response = client.execute(post, localContext);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String output = "";
            String line;
            while ((line = rd.readLine()) != null)
            {
                output += line;
            }
            
            String regex = "<a href=\"([^<^\"]*)\"( class=\"captionResult\")? title=\"(.*?)\">\\3</a></div>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(output);

            while ( matcher.find() )
            {
                searchEntries.add(new BusinessSearchEntry(matcher.group(3), matcher.group(1), cookieStore, localContext));
            }

            if (searchEntries.isEmpty())
            {
                String regex2 = "<SCRIPT LANGUAGE=\"JavaScript\">.*?window\\.location=\"(.*?)\";</script>";
                Pattern pattern2 = Pattern.compile(regex2);
                Matcher matcher2 = pattern2.matcher(output);

                if (matcher2.find())
                {
                    String URL = matcher2.group(1);
                    String businessName = this.name;

                    // Extract name from URL.
                    String regex3 = "ln=(.*?)&";
                    Pattern pattern3 = Pattern.compile(regex3);
                    Matcher matcher3 = pattern3.matcher(URL);

                    if (matcher3.find())
                    {
                        businessName = matcher3.group(1).replace("+", " ");
                    }

                    searchEntries.add(new BusinessSearchEntry(businessName, URL, cookieStore, localContext));
                }
            }

            System.out.println("The response: " + output);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return searchEntries;
    }

    public ArrayList<BusinessSearchEntry> process(String type)
    {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("location", location));
        parameters.add(new BasicNameValuePair("nm", name));
        parameters.add(new BasicNameValuePair("sd", type));
        
        return fetchBusinessNames(SEARCH_URL, parameters);
    }
}
