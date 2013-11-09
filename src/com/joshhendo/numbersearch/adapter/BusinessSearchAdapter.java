package com.joshhendo.numbersearch.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.joshhendo.numbersearch.R;
import com.joshhendo.numbersearch.structures.BusinessSearchEntry;
import com.joshhendo.numbersearch.views.ViewBusinessEntry;

import java.util.ArrayList;

public class BusinessSearchAdapter extends ArrayAdapter<BusinessSearchEntry>
{
    private ArrayList<BusinessSearchEntry> items;
    private Context context;
    private Uri selectedContact = null;

    public BusinessSearchAdapter(Context context, int textViewResourceId, ArrayList<BusinessSearchEntry> items)
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
            v = vi.inflate(R.layout.businesssearchrow, null);
        }

        final BusinessSearchEntry o = items.get(position);
        if (o != null)
        {
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            ImageButton button = (ImageButton) v.findViewById(R.id.button_call);

            if (tt != null)
            {
                tt.setText(o.getName());
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
                        //intent.setData(Uri.parse("tel:" + getOnlyNumerics(o.getPhoneNumber())));
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
                        Intent intent = new Intent(context, ViewBusinessEntry.class);
                        intent.putExtra("ENTRY", o);
                        context.startActivity(intent);
                    }
                });
            }
        }

        return v;
    }
}
