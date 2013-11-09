package com.joshhendo.numbersearch.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * com.joshhendo.numbersearch Copyright 2011
 * User: Josh
 * Date: 16/10/11
 * Time: 6:11 PM
 */
public class CommonFunctions
{
    public static String getOnlyNumerics(String input)
    {
        if (input == null) return null;

        StringBuilder strBuff = new StringBuilder();

        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);

            if (Character.isDigit(c)) strBuff.append(c);
        }
        
        return strBuff.toString();
    }

    public static String fixText(String input)
    {
        input = input.replaceAll("&amp;", "&");
        input = input.replaceAll("&#039;", "'");
        input = input.trim();
        return input;
    }

    public static String removeNewLines(String input)
    {
        input = input.replaceAll("\n", " ");
        return input;
    }

    // First few instances of "displayAlertDialog" only needed to close the activity when the close
    // button was pressed. Since has been changed, but this has been included for backwards compatability.
    public static void displayAlertDialog(String text, final Context context)
    {
        displayAlertDialog(text, context, true);
    }

    public static void displayAlertDialog(String text, final Context context, final boolean closeActivity)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text);
        builder.setCancelable(false);
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if ( closeActivity )
                {
                    ((Activity) context).finish();
                }
                else
                {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String getStateAcronym(String state)
    {
        if ( state.equalsIgnoreCase( "New South Wales")) return "NSW";
        if ( state.equalsIgnoreCase( "Queensland")) return "QLD";
        if ( state.equalsIgnoreCase( "Australian Capital Territory")) return "ACT";
        if ( state.equalsIgnoreCase( "Victoria")) return "VIC";
        if ( state.equalsIgnoreCase( "Northern Territory")) return "NT";
        if ( state.equalsIgnoreCase( "Western Australia")) return "WA";
        if ( state.equalsIgnoreCase( "South Australia")) return "SA";
        if ( state.equalsIgnoreCase( "Tasmania") ) return "TAS";
        return state;
    }

    public static String checkURL(String url)
    {
        if ( url.startsWith("http://")) return url;
        if ( url.startsWith("https://")) return url;
        return "http://" + url;
    }
}
