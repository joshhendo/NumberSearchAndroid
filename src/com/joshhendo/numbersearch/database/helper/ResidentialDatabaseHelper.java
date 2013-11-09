package com.joshhendo.numbersearch.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * com.joshhendo.numbersearch.database Copyright 2011
 * User: Josh
 * Date: 29/09/11
 * Time: 12:38 AM
 */
public class ResidentialDatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "numbersearchdata";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "CREATE TABLE pastresidential (_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, lastname TEXT NOT NULL, initials TEXT NOT NULL, location TEXT NOT NULL);";

    public ResidentialDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        //Log.w(TodoDatabaseHelper.class.getName(),
		//		"Upgrading database from version " + oldVersion + " to "
		//				+ newVersion + ", which will destroy all old data");
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS todo");
		onCreate(sqLiteDatabase);
    }
}
