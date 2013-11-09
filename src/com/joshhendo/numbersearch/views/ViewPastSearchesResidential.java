package com.joshhendo.numbersearch.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import com.joshhendo.numbersearch.R;
import com.joshhendo.numbersearch.adapter.ResidentialPastSearchEntryAdapter;
import com.joshhendo.numbersearch.database.adapter.ResidentialDatabaseAdapter;
import com.joshhendo.numbersearch.structures.ResidentialPastSearchEntry;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * com.joshhendo.numbersearch Copyright 2011
 * User: Josh
 * Date: 29/09/11
 * Time: 1:09 AM
 */
public class ViewPastSearchesResidential extends ListActivity
{
    private ResidentialDatabaseAdapter residentialDatabaseAdapter;
	private Cursor cursor;

    private ArrayList<ResidentialPastSearchEntry> m_entryPasts = null;
    private ResidentialPastSearchEntryAdapter adapter;
    
    private Context gContext = null;

    public enum ContextTask { DELETEENTRY }

    private Integer SelectedPosition;

    @Override
    public void onResume()
    {
        super.onResume();
        if ( residentialDatabaseAdapter == null)
        {
            residentialDatabaseAdapter = new ResidentialDatabaseAdapter(this);
        }
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        gContext = this;
		setContentView(R.layout.pastsearches);
		this.getListView().setDividerHeight(2);
        residentialDatabaseAdapter = new ResidentialDatabaseAdapter(this);

        fillData();
		registerForContextMenu(getListView());

        // Set long press adapter
        // See http://www.vogella.de/articles/AndroidListView/article.html#listadvanced_longclick
        ListView list = this.getListView();

        list.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedPosition = position;

                ArrayList<CharSequence> items = new ArrayList<CharSequence>();

                items.add("Delete ResidentialSearchEntry");

                CharSequence[] itemsArray = new CharSequence[items.size()];
                items.toArray(itemsArray);

                popupList(itemsArray, "Select Task...");

				// Return true to consume the click event. In this case the
				// onListItemClick listener is not called anymore.
				return true;
			}
		});
	}

    private void popupList(final CharSequence[] items, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(gContext);
        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogInterface, int i)
            {
                respondToContextMenu(i);
            }
        });
        builder.create().show();
    }

    private void respondToContextMenu(int i)
    {
        ContextTask task = null;

        switch(i)
        {
            case 0:
                task = ContextTask.DELETEENTRY;
                break;
        }

        switch(task)
        {
            case DELETEENTRY:
                deleteEntry(SelectedPosition);
                break;
        }
    }

    private boolean deleteEntry(Integer position)
    {
        // Get the object trying to delete.
        ResidentialPastSearchEntry o = (ResidentialPastSearchEntry) this.getListAdapter().getItem(position);

        try
        {
            residentialDatabaseAdapter.open();
        }
        catch (SQLException e)
        {
            return false;
        }

        // Delete from database
        residentialDatabaseAdapter.deletePastEntry(o.getId());

        residentialDatabaseAdapter.close();

        // Delete from adapter
        adapter.remove(o);

        // Notify adapter of change.
        adapter.notifyDataSetChanged();

        return true;
    }


    private void fillData()
    {
        try
        {
            residentialDatabaseAdapter.open();
        }
        catch (SQLException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        cursor = residentialDatabaseAdapter.fetchAllPastEntries();
		startManagingCursor(cursor);

        ArrayList<ResidentialPastSearchEntry> entryPasts = new ArrayList<ResidentialPastSearchEntry>();

        if ( cursor.moveToFirst() )
        {
            while (!cursor.isAfterLast())
            {
                Integer _id = cursor.getInt(cursor.getColumnIndex(ResidentialDatabaseAdapter.KEY_ROWID));
                String surname = cursor.getString(cursor.getColumnIndex(ResidentialDatabaseAdapter.KEY_LASTNAME));
                String initial = cursor.getString(cursor.getColumnIndex(ResidentialDatabaseAdapter.KEY_INITIALS));
                String location = cursor.getString(cursor.getColumnIndex(ResidentialDatabaseAdapter.KEY_LOCATION));
                entryPasts.add(new ResidentialPastSearchEntry(_id, surname, initial, location));
                cursor.moveToNext();
            }
        }

        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, entryPasts);
        if (this.adapter == null)
            this.adapter = new ResidentialPastSearchEntryAdapter(this, R.layout.residentailsearchrow, entryPasts);
        this.setListAdapter(this.adapter);

        residentialDatabaseAdapter.close();
	}

    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Get the item that was clicked
		ResidentialPastSearchEntry o = (ResidentialPastSearchEntry) this.getListAdapter().getItem(position);

        Intent data = new Intent();
        data.putExtra("surname", o.getSurname());
        data.putExtra("initial", o.getInitial());
        data.putExtra("location", o.getLocation());

		((Activity) gContext).setResult(Activity.RESULT_OK, data);
        ((Activity) gContext).finish();
	}

    // Create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pastsearchesresidentialoptions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.pastResidentialClearHistory:
                try
                {
                    residentialDatabaseAdapter.open();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                residentialDatabaseAdapter.truncatePastEntries();
                residentialDatabaseAdapter.close();
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
