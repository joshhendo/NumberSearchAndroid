package com.joshhendo.numbersearch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.joshhendo.numbersearch.base.SearchScreen;
import com.joshhendo.numbersearch.database.adapter.LocationDatabaseAdapter;
import com.joshhendo.numbersearch.database.adapter.ResidentialDatabaseAdapter;
import com.joshhendo.numbersearch.structures.Contact;
import com.joshhendo.numbersearch.views.ViewPastSearchesResidential;
import com.joshhendo.numbersearch.views.ViewResidentialList;

import java.sql.SQLException;

/**
 * com.com.joshhendo.joshhendo.joshhendo.numbersearch Copyright 2011
 * User: josh
 * Date: 17/06/11
 * Time: 7:29 PM
 */

public class SearchResidential extends SearchScreen
{
    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final int HISTORY_RESULT = 1002;
    public static final String DEBUG_TAG = "NumberSearchDebug";

    Context gContext = null;
    Contact contact = null;
    Uri selectedContact = null;

    AutoCompleteTextView surname = null;
    EditText initial = null;
    AutoCompleteTextView txtLocation = null;
    Button search = null;
    ImageButton currentlocation = null;
    


    private ResidentialDatabaseAdapter residentialDatabaseAdapter;
    private LocationDatabaseAdapter locationDatabaseAdapter;

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        residentialDatabaseAdapter.close();
        locationDatabaseAdapter.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.residential);

        gContext = this;

        surname = (AutoCompleteTextView) findViewById(R.id.edit_lastname);
        initial = (EditText) findViewById(R.id.edit_initial);
        txtLocation = (AutoCompleteTextView) findViewById(R.id.edit_location);

        // Get current locaton button
        currentlocation = (ImageButton) findViewById(R.id.button_location);
        setGetCurrentLocation(currentlocation, txtLocation, gContext);

        // Search button
        search = (Button) findViewById(R.id.button_search);
        search.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                // Validate input fields first
                if ( ! validateInputFields(surname.getText().toString(), txtLocation.getText().toString()))
                {
                    Toast.makeText(gContext, gContext.getString(R.string.surname_location_valid_search), Toast.LENGTH_SHORT).show();
                    return ;
                }

                // Add current search to database
                AddToDatabase(surname.getText().toString(), initial.getText().toString(), txtLocation.getText().toString());

                // Create a fetcher object to pass to the intent.
                FetcherResidential fetcher = new FetcherResidential(surname.getText().toString(), initial.getText().toString(), txtLocation.getText().toString(), 1);

                Intent intent = new Intent(SearchResidential.this, ViewResidentialList.class);
                intent.putExtra("FETCHER", fetcher);
                if (selectedContact != null)
                {
                    intent.putExtra("CONTACT", selectedContact.toString());
                }

                startActivity(intent);
            }
        });

        // Select contact button
        Button btnContact = (Button) findViewById(R.id.button_select_contact);
        btnContact.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                doLaunchContactPicker();
            }
        });
        
        // Clear the location field if it's using the "Current" location
        txtLocation.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            public void onFocusChange(View view, boolean hasFocus)
            {
                if (hasFocus)
                {
                    if (usingCurrentLocation)
                    {
                        txtLocation.setText("");
                        usingCurrentLocation = false;
                    }
                }
            }
        });

        if (this.residentialDatabaseAdapter == null)
            this.residentialDatabaseAdapter = new ResidentialDatabaseAdapter(this);

        if (this.locationDatabaseAdapter == null)
            this.locationDatabaseAdapter = new LocationDatabaseAdapter(this);

        setAutoComplete(surname, residentialDatabaseAdapter, ResidentialDatabaseAdapter.KEY_LASTNAME);
        setAutoComplete(txtLocation, locationDatabaseAdapter, LocationDatabaseAdapter.KEY_VALUE);
    }




    private boolean validateInputFields(String surname, String location)
    {
        return (surname != null || location != null) || (surname.length() > 0 || location.length() > 0);
    }

    // Create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.residentialoptions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.pastSearches:
                Intent intent = new Intent(SearchResidential.this, ViewPastSearchesResidential.class);
                startActivityForResult(intent, HISTORY_RESULT);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean AddToDatabase(String surname, String initial, String location)
    {
        ResidentialDatabaseAdapter residentialDatabaseAdapter = new ResidentialDatabaseAdapter(gContext);
        
        try
        {
            residentialDatabaseAdapter.open();
            residentialDatabaseAdapter.createPastEntry(surname, initial, location);
        }
        catch (SQLException e)
        {
            return false;
        }
        finally
        {
            residentialDatabaseAdapter.close();
        }

        return true;
    }

    private void doLaunchContactPicker()
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case CONTACT_PICKER_RESULT:
                    // Clear current fields
                    surname.setText("");
                    initial.setText("");
                    txtLocation.setText("");

                    Cursor cursor = null;
                    contact = new Contact();

                    try
                    {
                        Uri result = data.getData();
                        selectedContact = result;
                        String id = result.getLastPathSegment();
                        
                        //Get Name
                        cursor = getContentResolver().query(result, null, null, null, null);
                        if (cursor.moveToFirst())
                        {
                            // Try and get their name
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            contact.setName(name);
                            surname.setText(contact.getSurname());
                            initial.setText(contact.getInitials());
                        }
                        cursor.close();
                    }
                    catch (Exception e)
                    {
                        return;
                    }

                    // Get Suburb and Postcode
                    try
                    {
                        Uri result = data.getData();
                        String id = result.getLastPathSegment();

                        String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                        String[] addrWhereParams = new String[]{id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};

                        cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, addrWhere, addrWhereParams, null);

                        if (cursor.moveToFirst())
                        {
                            String postcode = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                            String suburb = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                            String state = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                            contact.setPostcode(postcode);
                            contact.setSuburb(suburb);
                            contact.setState(state);

                            txtLocation.setText(contact.getLocation());
                        }
                        cursor.close();
                    }
                    catch (Exception e)
                    {
                        return;
                    }

                    break;

                case HISTORY_RESULT:

                    Bundle extras = data.getExtras();
                    
                    if (extras != null)
                    {
                        surname.setFocusable(false);
                        surname.setFocusableInTouchMode(false);

                        txtLocation.setFocusable(false);
                        txtLocation.setFocusableInTouchMode(false);

                        surname.setText(extras.getString("surname"));
                        initial.setText(extras.getString("initial"));
                        txtLocation.setText(extras.getString("location"));

                        surname.setFocusable(true);
                        surname.setFocusableInTouchMode(true);

                        txtLocation.setFocusable(true);
                        txtLocation.setFocusableInTouchMode(true);
                    }

                    break;
            }
        }
    }
}
