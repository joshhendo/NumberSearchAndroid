package com.joshhendo.numbersearch;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * com.joshhendo.numbersearch Copyright 2011
 * User: Josh
 * Date: 23/06/11
 * Time: 11:30 AM
 */
public class ViewTab extends TabActivity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);

        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        // Create an intent for the Residential search
        intent = new Intent().setClass(this, SearchResidential.class);
        spec = tabHost.newTabSpec("residential").setIndicator("Residential", null).setContent(intent);
        tabHost.addTab(spec);
        
        // Create an intent for the Business search
        intent = new Intent().setClass(this, SearchBusiness.class);
        spec = tabHost.newTabSpec("business").setIndicator("Business", null).setContent(intent);
        tabHost.addTab(spec);

        // Create an intent for the Government search
        intent = new Intent().setClass(this, SearchResidential.class);
        spec = tabHost.newTabSpec("government").setIndicator("Government", null).setContent(intent);
        tabHost.addTab(spec);
    }
}
