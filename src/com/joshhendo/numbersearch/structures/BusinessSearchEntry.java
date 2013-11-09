package com.joshhendo.numbersearch.structures;

import com.joshhendo.numbersearch.base.CommonFunctions;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.HttpContext;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.joshhendo.numbersearch.structures Copyright 2011
 * User: Josh
 * Date: 3/10/11
 * Time: 11:23 PM
 */
public class BusinessSearchEntry implements Serializable
{
    // Note: CookieStore isn't serializable, therefore we need to store it as a string.
    // There is a reason to the maddness, it would be much easier to store it as a CookieStore, but
    // we can't do that :(     (20111015)

    private String name;
    private String url;
    private String cookieStore;
    private String httpContext;

    public BusinessSearchEntry(String name, String url, CookieStore cookieStore, HttpContext httpContext)
    {
        this.name = CommonFunctions.fixText(name);
        this.url = url;
        this.cookieStore = cookieStore.toString();
        this.httpContext = httpContext.toString();
    }

    private ArrayList<Cookie> getCookies(String input)
    {
        ArrayList<Cookie> toReturn = new ArrayList<Cookie>();

        String regex = "\\[version: ([0-9]*)\\]\\[name: (.*?)\\]\\[value: (.*?)\\]\\[domain: (.*?)\\]\\[path: (.*?)\\]\\[expiry: (.*?)\\]";
        Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(input);

        while ( matcher.find() )
        {
            final String name = matcher.group(2);
            final String value = matcher.group(3);
            final String domain = matcher.group(4);
            final String path = matcher.group(5);
            Date date = null;

            try
            {
                date = (Date) new SimpleDateFormat("E MMM dd HH:mm:ss Z").parse(matcher.group(6));
            }
            catch (ParseException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            final Date finalDate = date;

            Cookie cookie = new Cookie()
            {
                public String getName()
                {
                    return name;  //To change body of implemented methods use File | Settings | File Templates.
                }

                public String getValue()
                {
                    return value;  //To change body of implemented methods use File | Settings | File Templates.
                }

                public String getComment()
                {
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }

                public String getCommentURL()
                {
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }

                public Date getExpiryDate()
                {
                    return finalDate;  //To change body of implemented methods use File | Settings | File Templates.
                }

                public boolean isPersistent()
                {
                    return false;  //To change body of implemented methods use File | Settings | File Templates.
                }

                public String getDomain()
                {
                    return domain;  //To change body of implemented methods use File | Settings | File Templates.
                }

                public String getPath()
                {
                    return path;  //To change body of implemented methods use File | Settings | File Templates.
                }

                public int[] getPorts()
                {
                    return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
                }

                public boolean isSecure()
                {
                    return false;  //To change body of implemented methods use File | Settings | File Templates.
                }

                public int getVersion()
                {
                    return 0;  //To change body of implemented methods use File | Settings | File Templates.
                }

                public boolean isExpired(Date date)
                {
                    return false;  //To change body of implemented methods use File | Settings | File Templates.
                }
            };

            toReturn.add(cookie);
        }

        return toReturn;
    }

    public CookieStore getCookieStore()
    {
        ArrayList<Cookie> cookies = getCookies(this.cookieStore);

        CookieStore CookieStoreReconstructed = new BasicCookieStore();

        for ( Cookie cookie : cookies )
        {
            CookieStoreReconstructed.addCookie(cookie);
        }

        return CookieStoreReconstructed;
    }

    public String getHttpContext()
    {
        return httpContext;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
