package com.example.peterstolcz.inventoryappstage2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.peterstolcz.inventoryappstage2.data.BookstoreContract.BookstoreEntry;

public class BookstoreActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int BOOKSTORE_LOADER = 0;

    BookstoreCursorAdapter mBookstoreCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookstore);

        FloatingActionButton additem = findViewById(R.id.add_item);
        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookstoreActivity.this, BookModifierActivity.class);
                startActivity(intent);
            }
        });

        ListView bookstoreListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_layout);
        bookstoreListView.setEmptyView(emptyView);

        mBookstoreCursorAdapter = new BookstoreCursorAdapter(this, null);
        bookstoreListView.setAdapter(mBookstoreCursorAdapter);

        getLoaderManager().initLoader(BOOKSTORE_LOADER, null, this);

    }

    private void templateCreator() {

        ContentValues values = new ContentValues();

        values.put(BookstoreEntry.COLUMN_BOOK_NAME, "Sample Book");
        values.put(BookstoreEntry.COLUMN_BOOK_PRICE, 10);
        values.put(BookstoreEntry.COLUMN_BOOK_QUANTITY, 1);
        values.put(BookstoreEntry.COLUMN_SUPPLIER_NAME, BookstoreEntry.SUPPLIER_GOODBOOKS);
        values.put(BookstoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, 361543210);

        getContentResolver().insert(BookstoreEntry.CONTENT_URI, values);

    }

    private void deleteBookInventory() {
        getContentResolver().delete(BookstoreEntry.CONTENT_URI, null, null);
        Toast.makeText(this, getString(R.string.delete_inventory), Toast.LENGTH_SHORT).show();
    }

    public void bookSales(Long bID, Long bQuantity, String bName) {
        bQuantity = bQuantity - 1;

        if (bQuantity >= 1) {
            ContentValues values = new ContentValues();

            values.put(BookstoreEntry.COLUMN_BOOK_QUANTITY, bQuantity);
            Uri updateUri = ContentUris.withAppendedId(BookstoreEntry.CONTENT_URI, bID);
            getContentResolver().update(updateUri, values, null, null);

        } else if (bQuantity == 0) {

            ContentValues values = new ContentValues();
            values.put(BookstoreEntry.COLUMN_BOOK_QUANTITY, bQuantity);
            Uri updateUri = ContentUris.withAppendedId(BookstoreEntry.CONTENT_URI, bID);
            getContentResolver().update(updateUri, values, null, null);
            Toast.makeText(this, getString(R.string.no_more_books) + " " + bName, Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this, getString(R.string.no_more_books) + " " + bName, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bookstore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.create_template:
                templateCreator();
            Toast.makeText(this, getString(R.string.template_created), Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_delete_all_entries:
                showInventoryDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookstoreEntry._ID,
                BookstoreEntry.COLUMN_BOOK_NAME,
                BookstoreEntry.COLUMN_BOOK_PRICE,
                BookstoreEntry.COLUMN_BOOK_QUANTITY,
                BookstoreEntry.COLUMN_SUPPLIER_NAME,
                BookstoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this,
                BookstoreEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBookstoreCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookstoreCursorAdapter.swapCursor(null);

    }


    private void showInventoryDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.inventory_delete_confirmation);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                deleteBookInventory();
            }

        });

        builder.setNegativeButton(R.string.cancellation, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
