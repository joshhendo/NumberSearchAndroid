package com.joshhendo.numbersearch.views;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import com.joshhendo.numbersearch.FetcherResidential;
import com.joshhendo.numbersearch.R;
import com.joshhendo.numbersearch.adapter.ResidentialSearchAdapter;
import com.joshhendo.numbersearch.base.CommonFunctions;
import com.joshhendo.numbersearch.structures.ResidentialSearchEntry;

import java.util.ArrayList;

public class ViewResidentialList extends ListActivity
{
    private Context context = null;
    private ProgressDialog m_ProgressDialog = null;
    private ArrayList<ResidentialSearchEntry> m_Residential_Search_entries = null;
    private ResidentialSearchAdapter m_adapter;
    private Runnable viewOrders;
    private FetcherResidential fetcher = null;

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
                fetcher = (FetcherResidential) extras.getSerializable("FETCHER");
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
        m_Residential_Search_entries = new ArrayList<ResidentialSearchEntry>();
        if (this.m_adapter == null)
            this.m_adapter = new ResidentialSearchAdapter(this, R.layout.residentailsearchrow, m_Residential_Search_entries);
        this.m_adapter.setSelectedContact(selectedContact);
        setListAdapter(this.m_adapter);

        viewOrders = new Runnable()
        {
            public void run()
            {
                getOrders();
            }
        };

        final Thread thread = new Thread(null, viewOrders, "MangentoBackground");
        thread.start();

        if (progresDialog)
        {
            m_ProgressDialog = new ProgressDialog(ViewResidentialList.this);
            m_ProgressDialog.setCancelable(true);
            m_ProgressDialog.setTitle(context.getString(R.string.please_wait));
            m_ProgressDialog.setMessage(context.getString(R.string.retrieving_data));
            m_ProgressDialog.setOnCancelListener(
                    new DialogInterface.OnCancelListener()
                    {
                        public void onCancel(DialogInterface dialogInterface)
                        {
                            /*try
                            {
                                //thread.stop();
                            }
                            finally
                            {

                            }*/

                            ((Activity) context).finish();
                        }
                    }
            );
            m_ProgressDialog.show();
            //m_ProgressDialog = ProgressDialog.show(ViewResidentialList.this, "Please Wait...", "Retrieveing data", true);
        }
    }

    private void getOrders()
    {
        try
        {
            if ( fetcher != null )
                m_Residential_Search_entries = fetcher.process();

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
            if ( m_Residential_Search_entries != null && m_Residential_Search_entries.size() > 0 )
            {
                m_adapter.notifyDataSetChanged();
                for (ResidentialSearchEntry m_Residential_Search_entry : m_Residential_Search_entries) m_adapter.add(m_Residential_Search_entry);
            }

            m_ProgressDialog.dismiss();

            if ( m_Residential_Search_entries.isEmpty() )
            {
                CommonFunctions.displayAlertDialog(context.getString(R.string.no_results), context);
                return;
            }

            m_adapter.notifyDataSetChanged();

            if ( fetcher.getTotalPages() != null && fetcher.getTotalPages() > currentPage && currentPage <= 5 )
            {
                currentPage ++;
                runSearch(currentPage, false);
            }
        }
    };
}
