package com.joshhendo.numbersearch.base;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import com.joshhendo.numbersearch.FetcherBusiness;
import com.joshhendo.numbersearch.R;
import com.joshhendo.numbersearch.adapter.BusinessSearchAdapter;
import com.joshhendo.numbersearch.structures.BusinessSearchEntry;
import com.joshhendo.numbersearch.views.ViewBusinessEntry;

import java.util.ArrayList;

/**
 * com.joshhendo.numbersearch.base Copyright 2011
 * User: Josh
 * Date: 20/10/11
 * Time: 11:06 PM
 */

// This class is the base class for ViewBusinessList and ViewGovernmentList. ViewResidentialList is handled differently.

public class ViewList extends ListActivity
{
    protected Context context = null;
    protected ProgressDialog m_ProgressDialog = null;
    protected ArrayList<BusinessSearchEntry> m_Business_Search_entries = null;
    protected BusinessSearchAdapter m_adapter;
    protected Runnable viewOrders;
    protected FetcherBusiness fetcher = null;

    protected Uri selectedContact = null;

    protected Integer currentPage = 1;

    private String type = null;
    
    protected void runSearch(Integer page, Boolean progresDialog, final String type)
    {
        this.type = type;

        fetcher.setPage(page);
        m_Business_Search_entries = new ArrayList<BusinessSearchEntry>();
        if (this.m_adapter == null)
            this.m_adapter = new BusinessSearchAdapter(this, R.layout.businesssearchrow, m_Business_Search_entries);
        this.m_adapter.setSelectedContact(selectedContact);
        setListAdapter(this.m_adapter);

        viewOrders = new Runnable()
        {
            public void run()
            {
                getOrders(type);
            }
        };

        final Thread thread = new Thread(null, viewOrders, "MangentoBackground");
        thread.start();

        if (progresDialog)
        {
            m_ProgressDialog = new ProgressDialog(ViewList.this);
            m_ProgressDialog.setCancelable(true);
            m_ProgressDialog.setTitle(context.getString(R.string.please_wait));
            m_ProgressDialog.setMessage(context.getString(R.string.retrieving_data));
            m_ProgressDialog.setOnCancelListener(
                    new DialogInterface.OnCancelListener()
                    {
                        public void onCancel(DialogInterface dialogInterface)
                        {
                            //thread.stop();
                            ((Activity) context).finish();
                        }
                    }
            );
            m_ProgressDialog.show();
            //m_ProgressDialog = ProgressDialog.show(ViewResidentialList.this, "Please Wait...", "Retrieveing data", true);
        }
    }

    protected void getOrders(String type)
    {
        try
        {
            if ( fetcher != null )
            {
                m_Business_Search_entries = fetcher.process(type);
            }

        }
        catch(Exception e)
        {
            System.out.println("Exception e: " + e.getMessage().toString());
        }
        runOnUiThread(returnRes);
    }

    protected Runnable returnRes = new Runnable()
    {
        public void run()
        {
            if ( m_Business_Search_entries != null && m_Business_Search_entries.size() > 0 )
            {
                m_adapter.notifyDataSetChanged();
                for (BusinessSearchEntry m_Business_Search_entry : m_Business_Search_entries) m_adapter.add(m_Business_Search_entry);
            }

            m_ProgressDialog.dismiss();

            if (m_Business_Search_entries.isEmpty())
            {
                CommonFunctions.displayAlertDialog(context.getString(R.string.no_results), context);
            }
            else if (m_Business_Search_entries.size() == 1)
            {
                // Redirect to that page.
                Intent intent = new Intent(context, ViewBusinessEntry.class);
                intent.putExtra("ENTRY", m_Business_Search_entries.get(0));
                context.startActivity(intent);
            }
            else
            {
                m_adapter.notifyDataSetChanged();

                if ( fetcher.getTotalPages() != null && fetcher.getTotalPages() > currentPage && currentPage <= 5 )
                {
                    currentPage ++;
                    runSearch(currentPage, false, type);
                }
            }
        }
    };
}
