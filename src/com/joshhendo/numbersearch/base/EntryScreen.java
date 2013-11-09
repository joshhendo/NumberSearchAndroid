package com.joshhendo.numbersearch.base;

import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * com.joshhendo.numbersearch.base Copyright 2011
 * User: Josh
 * Date: 31/10/11
 * Time: 8:11 PM
 */
public class EntryScreen extends Activity
{
    protected void setLongClickCopyToClipboard(final TextView text, final Context context)
    {
        text.setOnLongClickListener(new View.OnLongClickListener()
        {
            public boolean onLongClick(View view)
            {
                ClipboardManager clipboard =  (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(text.getText().toString());
                Toast.makeText(context, "Text copied to clipboard.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
