package com.joshhendo.numbersearch.database.helper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * com.joshhendo.numbersearch.database.location Copyright 2011
 * User: Josh
 * Date: 29/09/11
 * Time: 1:09 PM
 * 
 *
 *     see http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
 *
 */
public class LocationDatabaseHelper extends SQLiteOpenHelper
{
    private static String DB_PATH = "/data/data/com.joshhendo.numbersearch/databases/";
    private static String DB_NAME = "locations.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public LocationDatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public void createDatabase() throws IOException
    {
        boolean databaseExists = checkDatabase();

        if ( databaseExists )
        {
            // do nothing
            return ;
        }

        copyDatabase();
    }

    private boolean checkDatabase()
    {
        SQLiteDatabase checkDB = null;

        try
        {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e)
        {
            // doesn't exist yet
        }

        if ( checkDB != null )
        {
            checkDB.close();
        }
        
        return checkDB != null;
    }

    private void copyDatabase() throws IOException
    {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFilename = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFilename);

        byte[] buffer = new byte[1024];
        int length;

        while ( (length = myInput.read(buffer)) > 0 )
        {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDatabase() throws SQLException
    {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public SQLiteDatabase getReadableDatabase()
    {
        //String myPath = DB_PATH + DB_NAME;
        //return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        return myDataBase;
    }

    @Override
    public synchronized void close()
    {

        if (myDataBase != null)
        {
            myDataBase.close();
        }

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Do nothing
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Do nothing
    }
}
