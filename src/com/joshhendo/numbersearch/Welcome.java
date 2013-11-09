package com.joshhendo.numbersearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.joshhendo.numbersearch.base.CommonFunctions;
import com.joshhendo.numbersearch.database.helper.LocationDatabaseHelper;

import java.io.IOException;

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

        // We need to make sure that the "locations.db" database
        Runnable databaseRunnable = new Runnable()
        {
            public void run()
            {
                LocationDatabaseHelper helperLocation = new LocationDatabaseHelper(gcontext);
                try
                {
                    helperLocation.createDatabase();
                }
                catch (IOException e)
                {
                    // Do nothing
                }
            }
        };

        Thread databaseThread = new Thread(databaseRunnable);
        databaseThread.start();
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

@Override
public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.welcome, menu);
    return true;
}

    @Override
public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
    case R.id.credits:
        displayCredits();
        return true;
    case R.id.donate:
        displayDonate();
        return true;
    default:
        return super.onOptionsItemSelected(item);
    }
}

    void displayCredits()
    {
        String credits = "Graphics from:\n"
+ "- Logo Graphics by Tim Henderson.\n"
+ "- Some icons from 2experts free icons set by Akhtar Sheikha at http://www.2expertsdesign.com.\n"
+ "- Some icons used from the Android resources by Google.\n"
+ "\n"
+ "Used resources from:\n"
+ "- MyLocation class from Stackoverflow user Fedor.\n"
+ "- SQLite and Autocomplete tutorial  by Chad Lung from giantflyingsaucer.com.\n"
+ "\n"
+ "Number Search is created by Joshua Henderson.\n";
        CommonFunctions.displayAlertDialog(credits, gcontext, false);
    }
    
    void displayDonate()
    {
        MessageDialogPreference.create(gcontext).show();
    }

    public static class MessageDialogPreference
    {
        public static AlertDialog create(Context context) {
            final TextView message = new TextView(context);
            // i.e.: R.string.dialog_message =>
            // "Test this dialog following the link to dtmilano.blogspot.com"
            final SpannableString s =
                    new SpannableString("If you want to donate, please consider making a donation to World Vision on my behalf. Other people in this world need donations more than me :). Go to http://r.joshhendo.com/go.php?wv");
            Linkify.addLinks(s, Linkify.WEB_URLS);
            message.setText(s);
            message.setMovementMethod(LinkMovementMethod.getInstance());

            return new AlertDialog.Builder(context)
                    .setTitle("Donate")
                    .setCancelable(true)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Close", null)
                    .setView(message)
                    .create();
        }

    }

}
