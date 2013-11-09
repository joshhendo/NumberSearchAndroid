package com.joshhendo.numbersearch;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

public class PhoneListView extends ListActivity
{

    private Context context = null;
    private ProgressDialog m_ProgressDialog = null;
    private ArrayList<Entry> m_entries = null;
    private EntryAdapter m_adapter;
    private Runnable viewOrders;
    private Fetcher fetcher = null;

    private Uri selectedContact = null;

    private Integer currentPage = 1;

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
                fetcher = (Fetcher) extras.getSerializable("FETCHER");
                selectedContact = Uri.parse(extras.getString("CONTACT"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        runSearch(currentPage, true);
    }

    private void runSearch(Integer page, Boolean progresDialog)
    {
        fetcher.setPage(page);
        m_entries = new ArrayList<Entry>();
        if (this.m_adapter == null)
            this.m_adapter = new EntryAdapter(this, R.layout.row, m_entries);
        this.m_adapter.setSelectedContact(selectedContact);
        setListAdapter(this.m_adapter);

        viewOrders = new Runnable()
        {
            public void run()
            {
                getOrders();
            }
        };

        Thread thread = new Thread(null, viewOrders, "MangentoBackground");
        thread.start();

        if (progresDialog)
        {
            m_ProgressDialog = ProgressDialog.show(PhoneListView.this, "Please Wait...", "Retrieveing data", true);
        }
    }

    private void getOrders()
    {
        try
        {
            if ( fetcher != null )
                m_entries = fetcher.process();

        }
        catch(Exception e)
        {

        }
        runOnUiThread(returnRes);
    }

    private Runnable returnRes = new Runnable()
    {
        public void run()
        {
            if ( m_entries != null && m_entries.size() > 0 )
            {
                m_adapter.notifyDataSetChanged();
                for ( int i = 0; i < m_entries.size(); i++ )
                    m_adapter.add(m_entries.get(i));
            }

            m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();

            if ( fetcher.getTotalPages() != null && fetcher.getTotalPages() > currentPage && currentPage <= 5 )
            {
                currentPage ++;
                runSearch(currentPage, false);
            }
        }
    };
}
