package com.joshhendo.numbersearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class EntryAdapter extends ArrayAdapter<Entry>
{
    private ArrayList<Entry> items;
    private Context context;
    private Uri selectedContact = null;

    public EntryAdapter(Context context, int textViewResourceId, ArrayList<Entry> items)
    {
        super(context, textViewResourceId, items);
        this.items = items;
        this.context = context;
    }

    public void setSelectedContact(Uri selectedContact)
    {
        this.selectedContact = selectedContact;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row, null);
        }

        final Entry o = items.get(position);
        if (o != null)
        {
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            ImageButton button = (ImageButton) v.findViewById(R.id.button_call);

            if (tt != null)
            {
                tt.setText(o.getName());
            }
            if (bt != null)
            {
                bt.setText(o.getStreetAddress() + ", " + o.getSuburb());
            }
            if (button != null)
            {
                button.setClickable(true);
                button.setFocusable(true);
                button.setBackgroundResource(android.R.drawable.menuitem_background);
                //button.setImageResource(android.R.drawable.ic_menu_call);
                button.setImageResource(android.R.drawable.sym_action_call);

                button.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        //Toast.makeText(context, o.getPhoneNumber(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + getOnlyNumerics(o.getPhoneNumber())));
                        context.startActivity(intent);
                    }
                });
            }
            if (v != null)
            {
                v.setClickable(true);
                v.setFocusable(true);
                v.setBackgroundResource(android.R.drawable.menuitem_background);

                v.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        //Toast.makeText(context, o.getName(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, ViewEntry.class);
                        intent.putExtra("ENTRY", o);
                        intent.putExtra("CONTACT", selectedContact.toString());
                        context.startActivity(intent);
                    }
                });
            }
        }

        return v;
    }

    public static String getOnlyNumerics(String str)
    {

        if (str == null)
        {
            return null;
        }

        StringBuffer strBuff = new StringBuffer();
        char c;

        for (int i = 0; i < str.length(); i++)
        {
            c = str.charAt(i);

            if (Character.isDigit(c))
            {
                strBuff.append(c);
            }
        }
        return strBuff.toString();
    }
}
