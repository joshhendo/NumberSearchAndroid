package com.joshhendo.numbersearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * com.joshhendo.numbersearch Copyright 2011
 * User: Josh
 * Date: 13/08/11
 * Time: 1:48 AM
 */
public class Welcome extends Activity
{
    private Context gcontext = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        gcontext = this;


        //RelativeLayout background = (RelativeLayout) findViewById(R.id.welcome_relative_layout_1);
        //background.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.icon));
    }
    
    public void startResidential(View v)
    {
        Intent i = new Intent(Welcome.this, SearchResidential.class);
        startActivity(i);
    }

    public void startBusiness(View v)
    {
        Intent i = new Intent(Welcome.this, SearchBusiness.class);
        startActivity(i);
    }

    public void startGovernment(View v)
    {
        Intent i = new Intent(Welcome.this, SearchGovernment.class);
        startActivity(i);
    }
}
