package com.joshhendo.numbersearch.views;

import android.net.Uri;
import android.os.Bundle;
import com.joshhendo.numbersearch.FetcherBusiness;
import com.joshhendo.numbersearch.base.ViewList;

/**
 * com.joshhendo.numbersearch.views Copyright 2011
 * User: Josh
 * Date: 20/10/11
 * Time: 11:20 PM
 */
public class ViewGovernmentList extends ViewList
{
    private final String type = "g";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = this;

        Bundle extras = getIntent().getExtras();
        if ( extras != null )
        {
            try
            {
                fetcher = (FetcherBusiness) extras.getSerializable("FETCHER");
                selectedContact = Uri.parse(extras.getString("CONTACT"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        runSearch(currentPage, true, type);
    }
}
