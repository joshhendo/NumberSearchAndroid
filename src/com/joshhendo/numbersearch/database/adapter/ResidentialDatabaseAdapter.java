package com.joshhendo.numbersearch.database.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.joshhendo.numbersearch.database.helper.ResidentialDatabaseHelper;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * com.joshhendo.numbersearch.database Copyright 2011
 * User: Josh
 * Date: 29/09/11
 * Time: 12:44 AM
 *
 * see http://www.vogella.de/articles/AndroidSQLite/article.html for help
 *
 */
public class ResidentialDatabaseAdapter implements DatabaseAdapter
{
    // Database Fields
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_INITIALS = "initials";
    public static final String KEY_LOCATION = "location";
    private static final String DATABASE_TABLE = "pastresidential";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Context context;
    private SQLiteDatabase database;
    private ResidentialDatabaseHelper residentialDatabaseHelper;

    public ResidentialDatabaseAdapter(Context context)
    {
        this.context = context;
    }

    public void open() throws SQLException
    {
        residentialDatabaseHelper = new ResidentialDatabaseHelper(context);
        database = residentialDatabaseHelper.getWritableDatabase();
        //return this;
    }

    public void close()
    {
        if (residentialDatabaseHelper != null)
            residentialDatabaseHelper.close();

        if (database != null)
            database.close();
    }

    public long createPastEntry(String lastname, String initials, String location)
    {
        // Delete any past residentialentry with the same paramaters
        deletePastEntries(lastname, initials, location);

        ContentValues contentValues = new ContentValues();

        // Insert date
        contentValues.put(KEY_DATE, dateFormat.format(new Date()));
        contentValues.put(KEY_LASTNAME, lastname);
        contentValues.put(KEY_INITIALS, initials);
        contentValues.put(KEY_LOCATION, location);

        return database.insert(DATABASE_TABLE, null, contentValues);
    }

    public void deletePastEntries(String lastname, String initials, String location)
    {
        String whereClause = KEY_LASTNAME + " LIKE '" + lastname + "' AND " + KEY_INITIALS + " LIKE '" + initials + "' AND " + KEY_LOCATION + " LIKE '" + location + "'";
        Cursor cursor = database.query(DATABASE_TABLE, new String[] { KEY_ROWID }, whereClause, null, null, null, null);

        if ( cursor.moveToFirst())
        {
            while ( !cursor.isAfterLast() )
            {
                deletePastEntry(cursor.getLong(cursor.getColumnIndex(KEY_ROWID)));
                cursor.moveToNext();
            }
        }

        cursor.close();
    }

    public boolean deletePastEntry(long rowId)
    {
        return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllPastEntries()
    {
        return database.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_DATE, KEY_INITIALS, KEY_LASTNAME, KEY_LOCATION}, null, null, null, null, KEY_ROWID + " DESC" );
    }

    public Cursor fetchValuesWithConstraint(String constraint)
    {
        //String queryString = "SELECT DISTINCT " + KEY_ROWID + ", " + KEY_LASTNAME + "," + KEY_INITIALS + "," + KEY_LOCATION + " FROM " + DATABASE_TABLE;
        String queryString = "SELECT DISTINCT " + KEY_LASTNAME + " AS _id, " + KEY_LASTNAME + " FROM " + DATABASE_TABLE;


        if (constraint != null)
        {
            constraint = constraint.trim() + "%";
            queryString += " WHERE " + KEY_LASTNAME + " LIKE ?";
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

    public Cursor fetchPastEntry(long rowId) throws SQLException
    {
        Cursor mCursor = database.query(true,
                DATABASE_TABLE,
                new String[] { KEY_ROWID, KEY_DATE, KEY_INITIALS, KEY_LASTNAME, KEY_LOCATION },
                KEY_ROWID + "=" + rowId,
                null, null, null, null, null);

        if ( mCursor != null )
        {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public void truncatePastEntries()
    {
        // Turns out the TRUNCATE SQL query doesn't work with SQLite. Annoying, but we can work around it since we only have a small data set.
        // database.rawQuery("TRUNCATE " + DATABASE_TABLE, null);

        Cursor cursor = fetchAllPastEntries();
        if ( cursor.moveToFirst())
        {
            while ( !cursor.isAfterLast() )
            {
                deletePastEntry(cursor.getLong(cursor.getColumnIndex(KEY_ROWID)));
                cursor.moveToNext();
            }
        }
    }

}
