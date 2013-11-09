package com.joshhendo.numbersearch.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.joshhendo.numbersearch.R;
import com.joshhendo.numbersearch.structures.ResidentialPastSearchEntry;

import java.util.ArrayList;

/**
 * com.joshhendo.numbersearch.adapter Copyright 2011
 * User: Josh
 * Date: 29/09/11
 * Time: 7:10 PM
 *
 * for help go to http://techdroid.kbeanie.com/2009/07/custom-listview-for-android.html
 */
public class ResidentialPastSearchEntryAdapter extends ArrayAdapter<ResidentialPastSearchEntry>
{
    private ArrayList<ResidentialPastSearchEntry> items;
    private Context context;
    private Uri selectedContact = null;

    public enum ContextTask { DELETEENTRY }

    private Integer SelectedEntryID;
    private Integer SelectedPosition;

    public ResidentialPastSearchEntryAdapter(Context context, int textViewResourceId, ArrayList<ResidentialPastSearchEntry> items)
    {
        super(context, textViewResourceId, items);
        this.items = items;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.pastsearchesrow, null);
        }

        final ResidentialPastSearchEntry o = items.get(position);
        if (o != null)
        {
            TextView tt = (TextView) v.findViewById(R.id.pastSearchesRowTopText);
            TextView bt = (TextView) v.findViewById(R.id.pastSearchesRowBottomText);

            if (tt != null)
            {
                String topText = "";

                if (o.getSurname() != null && o.getSurname().length() > 0)
                    topText += o.getSurname();

                if (topText != "" && o.getInitial() != null && o.getInitial().length() > 0)
                    topText += ", ";

                if (o.getInitial() != null && o.getInitial().length() > 0)
                    topText += o.getInitial();

                tt.setText(topText);
            }

            if (bt != null)
            {
                String bottomText = "";

                if ( o.getLocation() != null && o.getLocation().length() > 0 )
                    bottomText += o.getLocation();

                bt.setText(bottomText);
            }
        }

        return v;
    }
}
