package com.joshhendo.numbersearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.joshhendo.numbersearch.base.SearchScreen;
import com.joshhendo.numbersearch.database.adapter.LocationDatabaseAdapter;
import com.joshhendo.numbersearch.views.ViewGovernmentList;

/**
 * com.joshhendo.numbersearch Copyright 2011
 * User: Josh
 * Date: 28/06/11
 * Time: 7:42 PM
 */
public class SearchGovernment extends SearchScreen
{

    EditText name = null;
    AutoCompleteTextView location = null;
    ImageButton currentlocation = null;

    private LocationDatabaseAdapter locationDatabaseAdapter;

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        try
        {
            if (locationDatabaseAdapter != null)
                locationDatabaseAdapter.close();
        }
        catch (NullPointerException e)
        {
            // Seriously, don't worry about it. It isn't always closeable!
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.government);

        name = (EditText) findViewById(R.id.government_edit_government_name);
        location = (AutoCompleteTextView) findViewById(R.id.government_edit_government_location);

        // Set Get current location button
        currentlocation = (ImageButton) findViewById(R.id.government_button_location);
        setGetCurrentLocation(currentlocation, location, this);

        // Search button
        Button search = (Button) findViewById(R.id.government_button_search);
        search.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                // Create a fetcher object to pass to the intent.
                FetcherBusiness fetcher = new FetcherBusiness(name.getText().toString(), location.getText().toString(), 1);

                Intent intent = new Intent(SearchGovernment.this, ViewGovernmentList.class);
                intent.putExtra("FETCHER", fetcher);
                startActivity(intent);
            }
        });

        if (this.locationDatabaseAdapter == null)
            this.locationDatabaseAdapter = new LocationDatabaseAdapter(this);

        setAutoComplete(location, locationDatabaseAdapter, LocationDatabaseAdapter.KEY_VALUE);
    }


}
