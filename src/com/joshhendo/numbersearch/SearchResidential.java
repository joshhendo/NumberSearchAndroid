package com.joshhendo.numbersearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.joshhendo.numbersearch.structures.Contact;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * com.com.joshhendo.joshhendo.joshhendo.numbersearch Copyright 2011
 * User: josh
 * Date: 17/06/11
 * Time: 7:29 PM
 */

public class SearchResidential extends Activity
{
    private static final int CONTACT_PICKER_RESULT = 1001;
    public static final String DEBUG_TAG = "NumberSearchDebug";

    Context gContext = null;
    Contact contact = null;
    Uri selectedContact = null;

    EditText surname = null;
    EditText initial = null;
    AutoCompleteTextView location = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.residential);

        gContext = this;

        surname = (EditText) findViewById(R.id.edit_lastname);
        initial = (EditText) findViewById(R.id.edit_initial);
        location = (AutoCompleteTextView) findViewById(R.id.edit_location);

        // Search button
        Button search = (Button) findViewById(R.id.button_search);
        search.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                // Create a fetcher object to pass to the intent.
                Fetcher fetcher = new Fetcher(surname.getText().toString(), initial.getText().toString(), location.getText().toString(), 1);

                Intent intent = new Intent(SearchResidential.this, PhoneListView.class);
                intent.putExtra("FETCHER", fetcher);
                intent.putExtra("CONTACT", selectedContact.toString());
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
    }

    // This one actually crashed android! :(
    private void ReadRawResourceIntoArray(ArrayList list, int resource)
    {
        try
        {
            InputStream is = getResources().openRawResource(R.raw.suburbs);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(is);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");

            for ( int s = 0; s < nodeList.getLength(); s++)
            {
                Node node = nodeList.item(s);
                
                Toast.makeText(gContext, node.getTextContent(), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            System.out.println("XML Pasing Excpetion = " + e);
        }

    }

    // This one still crashed on large lists on emulator, but not on phone. Disappointing. To use, pass R.xml.suburbs to it
    private void ReadResourceIntoArray(ArrayList list, int resource)
    {
        System.out.println("Starting XML thingo");
        Toast.makeText(gContext, "starting XML thingo", Toast.LENGTH_SHORT).show();

        XmlResourceParser parser = gContext.getResources().getXml(resource);

        try
        {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;

                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        // handle open tags
                        break;
                    case XmlPullParser.END_TAG:
                        // handle close tags
                        break;
                    case XmlPullParser.TEXT:
                        list.add(parser.getText());
                        break;
                }

                eventType = parser.next();
            }
        }
        catch (XmlPullParserException e)
        {
            throw new RuntimeException("Cannot parse XML");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Cannot parse XML");
        }
        finally
        {
            parser.close();
        }
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
                    location.setText("");

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
                    }
                    catch (Exception e)
                    {

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

                            location.setText(contact.getLocation());
                        }
                    }
                    catch (Exception e)
                    {

                    }
            }
        }
    }
}
