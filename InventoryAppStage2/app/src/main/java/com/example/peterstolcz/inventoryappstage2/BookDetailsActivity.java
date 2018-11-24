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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peterstolcz.inventoryappstage2.data.BookstoreContract;
import com.example.peterstolcz.inventoryappstage2.data.BookstoreContract.BookstoreEntry;

public class BookDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_BOOKSTORE_LOADER = 0;

    private Uri mCurrentBookstoreUri;

    private TextView mBookNameTextView;

    private TextView mBookPriceTextView;

    private TextView mBookQuantityTextView;

    public TextView mBookSupplierNameSpinner;

    public TextView mBookSupplierPhoneTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mBookNameTextView = findViewById(R.id.detail_book_name);
        mBookPriceTextView = findViewById(R.id.detail_book_price);
        mBookQuantityTextView = findViewById(R.id.detail_book_quantity);
        mBookSupplierNameSpinner = findViewById(R.id.detail_spinner_supplier_name);
        mBookSupplierPhoneTextView = findViewById(R.id.detail_supplier_phone_number);

        Intent intent = getIntent();
        mCurrentBookstoreUri = intent.getData();

        if (mCurrentBookstoreUri == null) {
            setTitle(R.string.detail_activity_title_book_details);
        } else {
            setTitle(getString(R.string.detail_activity_title_book_details));
            getLoaderManager().initLoader(EXISTING_BOOKSTORE_LOADER, null, this);
        }

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
                mCurrentBookstoreUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            Button increaseButton = findViewById(R.id.quantity_increase_button);
            Button decreaseButton = findViewById(R.id.quantity_decrease_button);
            Button phoneCall = findViewById(R.id.call_button);
            Button details_modifybutton = findViewById(R.id.modify_button_in_details);
            Button searchButton = findViewById(R.id.look_up_book);

            int nameColumnIndex = cursor.getColumnIndex(BookstoreEntry.COLUMN_BOOK_NAME);
            final int idColumnIndex = cursor.getColumnIndex(BookstoreEntry._ID);
            int priceColumnIndex = cursor.getColumnIndex(BookstoreEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookstoreEntry.COLUMN_BOOK_QUANTITY);
            int suppliernameColumnIndex = cursor.getColumnIndex(BookstoreEntry.COLUMN_SUPPLIER_NAME);
            int supplierphonenumberColumnIndex = cursor.getColumnIndex(BookstoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            final int bookIDColumnIndex = cursor.getColumnIndex(BookstoreContract.BookstoreEntry._ID);

            final String currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            final int currentQuantity = cursor.getInt(quantityColumnIndex);
            int currentSuppliername = cursor.getInt(suppliernameColumnIndex);
            String currentSupplierphonenumber = cursor.getString(supplierphonenumberColumnIndex);
            final String bID = cursor.getString(bookIDColumnIndex);

            mBookNameTextView.setText(currentName);
            mBookPriceTextView.setText(Integer.toString(currentPrice));
            mBookQuantityTextView.setText(Integer.toString(currentQuantity));
            mBookSupplierPhoneTextView.setText(currentSupplierphonenumber);

            if(currentQuantity == 0){

                TextView outofstockTextView = findViewById(R.id.outofstocktextview);
                outofstockTextView.setText(R.string.outofstock);

            }else{
                TextView outofstockTextView = findViewById(R.id.outofstocktextview);
                outofstockTextView.setText(R.string.empty);
            }

            switch (currentSuppliername) {
                case BookstoreEntry.SUPPLIER_GOODBOOKS:
                    mBookSupplierNameSpinner.setText(getText(R.string.supplier_goodbooks));
                    break;
                case BookstoreEntry.SUPPLIER_FICTIONFANS:
                    mBookSupplierNameSpinner.setText(getText(R.string.supplier_fictionfans));
                    break;
                case BookstoreEntry.SUPPLIER_BOOKHOUSE:
                    mBookSupplierNameSpinner.setText(getText(R.string.supplier_bookhouse));
                    break;
                case BookstoreEntry.SUPPLIER_READWELL:
                    mBookSupplierNameSpinner.setText(getText(R.string.supplier_readwell));
                    break;
                default:
                    mBookSupplierNameSpinner.setText(getText(R.string.supplier_unknown));
                    break;

            }

            phoneCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    String suppliercaller = mBookSupplierPhoneTextView.getText().toString();
                    intent.setData(Uri.parse("tel:" + suppliercaller));
                    startActivity(intent);
                }
            });


            details_modifybutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BookDetailsActivity.this, BookModifierActivity.class);
                    Uri currentBookstoreUri = ContentUris.withAppendedId(BookstoreContract.BookstoreEntry.CONTENT_URI, Long.parseLong(bID));
                    intent.setData(currentBookstoreUri);
                    startActivity(intent);
                }
            });

            decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decrease(idColumnIndex, currentQuantity);
                }
            });

            increaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increase(idColumnIndex, currentQuantity);
                }
            });

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showAddAuthorDialog();

                }
            });


        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    public void decrease(int bID, int bQuantity) {
        bQuantity = bQuantity - 1;

        if (bQuantity >= 1) {
            updateBook(bQuantity);

        } else if (bQuantity == 0) {
            updateBook(bQuantity);
            Toast.makeText(this, getString(R.string.no_more_books_in_detail), Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, getString(R.string.no_more_books_in_detail), Toast.LENGTH_SHORT).show();
        }
    }


    public void increase(int bID, int bQuantity) {
        bQuantity = bQuantity + 1;
        if (bQuantity >= 0 && bQuantity < 1000000000) {
            updateBook(bQuantity);
        }
    }

    private void updateBook(int bQuantity) {

        if (mCurrentBookstoreUri == null) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookstoreEntry.COLUMN_BOOK_QUANTITY, bQuantity);

        if (mCurrentBookstoreUri == null) {
            Uri newUri = getContentResolver().insert(BookstoreEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.book_insertion_failed), Toast.LENGTH_SHORT).show();
           }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookstoreUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.book_update_failed), Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void showAddAuthorDialog(){

        AlertDialog.Builder addauthor = new AlertDialog.Builder(this);

        addauthor.setTitle(R.string.search_book);
        addauthor.setMessage(R.string.add_author);
        String bookName = mBookNameTextView.getText().toString();

        final EditText authordetails = new EditText(this);
        addauthor.setView(authordetails);
        authordetails.setText(bookName);

        addauthor.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userinput = authordetails.getText().toString();

                    Intent browserLookup = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=" + userinput));
                    startActivity(browserLookup);

                }
        });

        addauthor.setNegativeButton(R.string.cancellation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        addauthor.show();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentBookstoreUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_from_details);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.delete_from_details:

                showDeleteConfirmationDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirmation);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteBook();
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


    private void deleteBook() {

        if (mCurrentBookstoreUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentBookstoreUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.modify_deletion_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.modify_deletion_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
        NavUtils.navigateUpFromSameTask(BookDetailsActivity.this);
    }

}