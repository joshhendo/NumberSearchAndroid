package com.joshhendo.numbersearch.database.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.joshhendo.numbersearch.database.helper.LocationDatabaseHelper;

import java.sql.SQLException;

/**
 * com.joshhendo.numbersearch.database.location Copyright 2011
 * User: Josh
 * Date: 29/09/11
 * Time: 1:51 PM
 */
public class LocationDatabaseAdapter implements DatabaseAdapter
{
    // Database Fields
    public static final String KEY_ID = "_id";
    public static final String KEY_VALUE = "value";
    private static final String DATABASE_TABLE = "suburbs";

    private Context context;
    private SQLiteDatabase database;
    private LocationDatabaseHelper locationDatabaseHelper;

    public LocationDatabaseAdapter(Context context)
    {
        this.context = context;
    }

    public void open() throws SQLException
    {
        locationDatabaseHelper = new LocationDatabaseHelper(context);
        database = locationDatabaseHelper.getReadableDatabase();
        // return this;
    }

    public void close()
    {
        if (locationDatabaseHelper != null)
            locationDatabaseHelper.close();
    }

    public Cursor fetchValuesWithConstraint(String constraint)
    {
        String queryString = "SELECT " + KEY_ID + "," + KEY_VALUE + " FROM " + DATABASE_TABLE;

        if (constraint == null || constraint.length() < 3)
            return null;

        if (constraint != null)
        {
            constraint = constraint.trim() + "%";
            queryString += " WHERE " + KEY_VALUE + " LIKE ?";
        }

        String params[] = { constraint };

        if (constraint == null)
        {
            params = null;
        }

        Cursor cursor = database.rawQuery(queryString, params);
        
        if ( cursor != null )
        {
            ((Activity) context).startManagingCursor(cursor);
            cursor.moveToFirst();
            return cursor;
        }

        return null;
    }
}
