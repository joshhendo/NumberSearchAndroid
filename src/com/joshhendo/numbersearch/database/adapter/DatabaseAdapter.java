package com.joshhendo.numbersearch.database.adapter;

import android.database.Cursor;

import java.sql.SQLException;

/**
 * com.joshhendo.numbersearch.database Copyright 2011
 * User: Josh
 * Date: 29/09/11
 * Time: 2:09 PM
 */
public interface DatabaseAdapter
{
    public void open() throws SQLException;
    public void close();
    public Cursor fetchValuesWithConstraint(String constraint);
}
