package com.joshhendo.numbersearch.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.joshhendo.numbersearch.R;
import com.joshhendo.numbersearch.base.CommonFunctions;
import com.joshhendo.numbersearch.base.EntryScreen;
import com.joshhendo.numbersearch.structures.ResidentialSearchEntry;

import java.util.List;

/**
 * com.joshhendo.numbersearch Copyright 2011
 * User: josh
 * Date: 18/06/11
 * Time: 12:26 AM
 */
public class ViewResidentialEntry extends EntryScreen
{
    private ResidentialSearchEntry residentialSearchEntry = null;
    private Uri selectedContact = null;
    private Integer selectedContactID = null;
    private Context gContext = null;

    public enum AddTask { SELECTEDCONTACT, NEWCONTACT, OTHERCONTACT }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.residentialentry);
        
        gContext = this;

        // Get the fetcher data that was passed.
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            try
            {
                residentialSearchEntry = (ResidentialSearchEntry) extras.getSerializable("ENTRY");
                selectedContact = Uri.parse(extras.getString("CONTACT"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        TextView name = (TextView) findViewById(R.id.text_entry_name);
        TextView number = (TextView) findViewById(R.id.text_entry_number);
        TextView address = (TextView) findViewById(R.id.text_entry_address);

        name.setText(residentialSearchEntry.getName());
        setLongClickCopyToClipboard(name, gContext);

        number.setText(residentialSearchEntry.getPhoneNumber());
        setLongClickCopyToClipboard(number, gContext);

        address.setText(residentialSearchEntry.getFullAddress());
        setLongClickCopyToClipboard(address, gContext);
        

        // Configure CALL button
        Button btnCall = (Button) findViewById(R.id.button_entry_call);
        btnCall.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + CommonFunctions.getOnlyNumerics(residentialSearchEntry.getPhoneNumber())));
                startActivity(intent);
            }
        });

        // Configure NAVIGATE button
        Button btnNavigate = (Button) findViewById(R.id.button_entry_navigate);

        // Check if intent is available. If not, disable the button
        if (!isIntentAvailable(this, Intent.ACTION_VIEW, Uri.parse("google.navigation:q=sydney,nsw,australia")))
            btnNavigate.setEnabled(false);

        btnNavigate.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                String address = String.format(gContext.getString(R.string.format_address),
                        residentialSearchEntry.getStreetAddress(), residentialSearchEntry.getSuburb(), residentialSearchEntry.getState(), residentialSearchEntry.getPostcode());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("google.navigation:q=" + address));
                startActivity(intent);
            }
        });

        // Configure VIEW ON MAP button
        Button btnViewOnMap = (Button) findViewById(R.id.button_entry_map);

        // Check if the intent is available. If not, disable the button.
        if (!isIntentAvailable(this, Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=sydney,nsw,australia")))
            btnViewOnMap.setEnabled(false);

        btnViewOnMap.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                String address = String.format(gContext.getString(R.string.format_address),
                        residentialSearchEntry.getStreetAddress(), residentialSearchEntry.getSuburb(), residentialSearchEntry.getState(), residentialSearchEntry.getPostcode());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:0,0?q=" + address));
                startActivity(intent);
            }
        });

        // Configure SHARE button
        Button btnShare = (Button) findViewById(R.id.button_entry_share);
        btnShare.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Phone Book Listing for " + residentialSearchEntry.getName());
                i.putExtra(Intent.EXTRA_TEXT, "Name: " + residentialSearchEntry.getName() + "\nPhone: " + residentialSearchEntry.getPhoneNumber() + "\nAddress: " + residentialSearchEntry.getFullAddress());
                startActivity(Intent.createChooser(i, "Share Entry..."));
            }
        });

        // Configure ADD TO CONTACT button
        Button btnAddToContact = (Button) findViewById(R.id.button_entry_add_to_contact);
        btnAddToContact.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                if (selectedContact != null)
                {
                    // Get the name of the contact
                    String name = "";
                    Cursor cursor = getContentResolver().query(selectedContact, null, null, null, null);
                    if (cursor.moveToFirst())
                    {
                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        selectedContactID = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                    }

                }

                addToContact();

            }
        });
    }

    private void addToContact()
    {
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.PHONE, CommonFunctions.getOnlyNumerics(residentialSearchEntry.getPhoneNumber()));
        intent.putExtra(ContactsContract.Intents.Insert.POSTAL, residentialSearchEntry.getStreetAddress() + ", " + residentialSearchEntry.getSuburb() + ", " + residentialSearchEntry.getState() + " " + residentialSearchEntry.getPostcode());
        gContext.startActivity(intent);
    }

    public static boolean isIntentAvailable(Context context, String action, Uri uri)
    {
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(action);
        if ( uri != null )
            intent.setData(uri);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
