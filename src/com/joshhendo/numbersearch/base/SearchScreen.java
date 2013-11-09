package com.joshhendo.numbersearch.base;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.View;
import android.widget.*;
import com.joshhendo.numbersearch.R;
import com.joshhendo.numbersearch.database.adapter.DatabaseAdapter;
import com.joshhendo.numbersearch.database.adapter.LocationDatabaseAdapter;
import com.joshhendo.numbersearch.database.adapter.ResidentialDatabaseAdapter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * com.joshhendo.numbersearch Copyright 2011
 * User: Josh
 * Date: 20/10/11
 * Time: 10:18 PM
 */
public class SearchScreen extends Activity
{
    Cursor[] cursors = new Cursor[2];
    protected Boolean usingCurrentLocation = false;

    @Override
    public void onPause()
    {
        super.onPause();
        
        // Close all databases if possible.
        for (Cursor cursor : cursors)
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }

    protected void setGetCurrentLocation (ImageButton currentlocation, final AutoCompleteTextView txtLocation, final Context gContext)
    {
        currentlocation.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Toast.makeText(gContext, gContext.getString(R.string.searching_location), Toast.LENGTH_LONG).show();

                MyLocation myLocation = new MyLocation();
                MyLocation.LocationResult locationResult = new MyLocation.LocationResult()
                {
                    @Override
                    public void gotLocation(Location location)
                    {
                        try
                        {
                            Geocoder geocoder = new Geocoder(gContext);
                            List<Address> addresses = null;

                            try
                            {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }

                            if ( !addresses.isEmpty())
                            {
                                String strLocation = (addresses.get(0).getLocality() != null ? addresses.get(0).getLocality() + " " : "") + (addresses.get(0).getAdminArea() != null ? CommonFunctions.getStateAcronym(addresses.get(0).getAdminArea()) + " " : "") + (addresses.get(0).getPostalCode() != null ? addresses.get(0).getPostalCode() + " " : "");
                                strLocation = strLocation.toUpperCase().trim();

                                txtLocation.setFocusable(false);
                                txtLocation.setFocusableInTouchMode(false);

                                txtLocation.setText(strLocation);
                                usingCurrentLocation = true;

                                txtLocation.setFocusable(true);
                                txtLocation.setFocusableInTouchMode(true);
                            }
                            else
                            {
                                Toast.makeText(gContext, gContext.getString(R.string.no_location), Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(gContext, gContext.getString(R.string.no_location), Toast.LENGTH_LONG).show();
                        }
                    }
                };

                try
                {
                    myLocation.getLocation(gContext, locationResult);
                }
                catch (Exception e)
                {
                    Toast.makeText(gContext, gContext.getString(R.string.no_location), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    protected void setAutoComplete (AutoCompleteTextView item, final DatabaseAdapter databaseAdapter, final String key)
    {
        int cursorIndex = 0;

        if (key == ResidentialDatabaseAdapter.KEY_LASTNAME)
        {
            cursorIndex = 0;
        }
        else if (key == LocationDatabaseAdapter.KEY_VALUE)
        {
            cursorIndex = 1;
        }

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_dropdown_item_1line, null,  new String[] { key }, new int[] {android.R.id.text1} );
        item.setAdapter(adapter);

        // Set the CursorToStringConverter, to provide the labels for the choices to be displayed in the AutoCompleteTextView.
        adapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter()
        {
            public String convertToString(android.database.Cursor cursor)
            {
                // Get the label for this residentailsearchrow out of the "state" column
                final int columnIndex = cursor.getColumnIndexOrThrow(key);
                return cursor.getString(columnIndex);
            }
        });

        // Set the FilterQueryProvider, to run queries for choices that match the specified input.
        final int finalCursorIndex = cursorIndex;
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                // Search for states whose names begin with the specified letters. ResidentialDatabaseAdapter databaseAdapter = new ResidentialDatabaseAdapter(gContext);
                try
                {
                    databaseAdapter.open();
                }
                catch (SQLException e)
                {
                    return null;
                }
                catch (Exception e)
                {
                    return null;
                }

                String constraintString = (constraint != null ? constraint.toString() : null);
                cursors[finalCursorIndex] = databaseAdapter.fetchValuesWithConstraint(constraintString);
                //databaseAdapter.close();

                return cursors[finalCursorIndex];
            }
        });
    }
}
