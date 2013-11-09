package com.joshhendo.numbersearch.views;

import android.net.Uri;
import android.os.Bundle;
import com.joshhendo.numbersearch.FetcherBusiness;
import com.joshhendo.numbersearch.base.ViewList;

public class ViewBusinessList extends ViewList
{
    private final String type = "b";

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
