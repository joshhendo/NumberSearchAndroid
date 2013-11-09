package com.joshhendo.numbersearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * com.joshhendo.numbersearch Copyright 2011
 * User: josh
 * Date: 18/06/11
 * Time: 12:26 AM
 */
public class ViewEntry extends Activity
{
    private Entry entry = null;
    private Uri selectedContact = null;
    private Integer selectedContactID = null;
    private Context gContext = null;

    public enum AddTask { SELECTEDCONTACT, NEWCONTACT, OTHERCONTACT };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        
        gContext = this;

        // Get the fetcher data that was passed.
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            try
            {
                entry = (Entry) extras.getSerializable("ENTRY");
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

        name.setText(entry.getName());
        number.setText(entry.getPhoneNumber());
        address.setText(entry.getStreetAddress() + ", " + entry.getSuburb() + " " + entry.getPostcode() + ", " + entry.getState());

        // Configure CALL button
        Button btnCall = (Button) findViewById(R.id.button_entry_call);
        btnCall.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + EntryAdapter.getOnlyNumerics(entry.getPhoneNumber())));
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
                String address = String.format("%s %s %s %s Australia",
                        entry.getStreetAddress(), entry.getSuburb(), entry.getState(), entry.getPostcode());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("google.navigation:q=" + address));
                startActivity(intent);
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

                    ArrayList<CharSequence> items = new ArrayList<CharSequence>();

                    if ( name != "" )
                        items.add(name);
                    
                    items.add("Exiting Contact");
                    items.add("New Contact");

                    CharSequence[] itemsArray = new CharSequence[items.size()];
                    items.toArray(itemsArray);

                    popupList(itemsArray, "Add information to...");

                }
            }
        });
    }

    private void popupList(final CharSequence[] items, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Toast.makeText(gContext, String.valueOf(selectedContactID), Toast.LENGTH_LONG).show();
                addInfoToContact(i);
            }
        });
        builder.create().show();
    }

    private void addInfoToContact(int i)
    {
        if ( selectedContact == null ) i++;

        AddTask task = null;

        switch(i)
        {
            case 0:
                task = AddTask.SELECTEDCONTACT;
                break;
            case 1:
                task = AddTask.OTHERCONTACT;
                break;
            case 2:
                task = AddTask.NEWCONTACT;
                break;
        }

        switch (task)
        {
            case SELECTEDCONTACT:
                Uri phoneUri = null;
                ContentValues values = new ContentValues();
                phoneUri = Uri.withAppendedPath(Contacts.People.CONTENT_URI, selectedContactID.toString());
                phoneUri = Uri.withAppendedPath(phoneUri, Contacts.People.Phones.CONTENT_DIRECTORY);
                values.put(Contacts.People.Phones.TYPE, Contacts.People.Phones.TYPE_HOME);
                values.put(Contacts.People.Phones.NUMBER, EntryAdapter.getOnlyNumerics(entry.getPhoneNumber()));
                getContentResolver().insert(phoneUri, values);
                break;
            case NEWCONTACT:
                break;
            case OTHERCONTACT:
                break;
        }
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
