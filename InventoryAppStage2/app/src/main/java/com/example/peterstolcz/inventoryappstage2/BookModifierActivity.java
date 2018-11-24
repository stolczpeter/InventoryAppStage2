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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peterstolcz.inventoryappstage2.data.BookstoreContract;
import com.example.peterstolcz.inventoryappstage2.data.BookstoreContract.BookstoreEntry;

public class BookModifierActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_BOOKSTORE_LOADER = 0;

    private Uri mCurrentBookstoreUri;

    private EditText mBookNameEditText;

    private EditText mBookPriceEditText;

    private EditText mBookQuantityEditText;

    public Spinner mBookSupplierNameSpinner;

    public TextView mBookSupplierPhoneTextView;

    private int mBookSupplierName = BookstoreEntry.SUPPLIER_UNKNOWN;

    private boolean mBookChange = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookChange = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        Intent intent = getIntent();
        mCurrentBookstoreUri = intent.getData();
        Button modify_detailsbutton = findViewById(R.id.details_button_in_modify);

        if (mCurrentBookstoreUri == null) {
            setTitle(R.string.modify_activity_title_new_book);
            modify_detailsbutton.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.modify_activity_title));
            getLoaderManager().initLoader(EXISTING_BOOKSTORE_LOADER, null, this);
            modify_detailsbutton.setVisibility(View.VISIBLE);

        }

        mBookNameEditText = findViewById(R.id.modify_book_name);
        mBookPriceEditText = findViewById(R.id.modify_book_price);
        mBookQuantityEditText = findViewById(R.id.modify_book_quantity);
        mBookSupplierNameSpinner = findViewById(R.id.spinner_supplier_name);
        mBookSupplierPhoneTextView = findViewById(R.id.supplier_phone_number);

        mBookNameEditText.setOnTouchListener(mTouchListener);
        mBookPriceEditText.setOnTouchListener(mTouchListener);
        mBookQuantityEditText.setOnTouchListener(mTouchListener);
        mBookSupplierNameSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    private void setupSpinner() {

        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mBookSupplierNameSpinner.setAdapter(supplierSpinnerAdapter);

        mBookSupplierNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_readwell))) {
                        mBookSupplierName = BookstoreEntry.SUPPLIER_READWELL;
                        mBookSupplierPhoneTextView.setText(R.string.phonenumber_readwell);
                    } else if (selection.equals(getString(R.string.supplier_goodbooks))) {
                        mBookSupplierName = BookstoreEntry.SUPPLIER_GOODBOOKS;
                        mBookSupplierPhoneTextView.setText(R.string.phonenumber_goodbooks);
                    } else if (selection.equals(getString(R.string.supplier_fictionfans))) {
                        mBookSupplierName = BookstoreEntry.SUPPLIER_FICTIONFANS;
                        mBookSupplierPhoneTextView.setText(R.string.phonenumber_fictionfans);
                    } else if (selection.equals(getString(R.string.supplier_bookhouse))) {
                        mBookSupplierName = BookstoreEntry.SUPPLIER_BOOKHOUSE;
                        mBookSupplierPhoneTextView.setText(R.string.phonenumber_bookhouse);
                    } else {
                        mBookSupplierName = BookstoreEntry.SUPPLIER_UNKNOWN;
                        mBookSupplierPhoneTextView.setText(R.string.phonenumber_none);
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBookSupplierName = BookstoreEntry.SUPPLIER_UNKNOWN;
            }
        });
    }

    private void saveBook() {
        String nameString = mBookNameEditText.getText().toString().trim();
        String priceString = mBookPriceEditText.getText().toString().trim();
        String quantityString = mBookQuantityEditText.getText().toString().trim();
        String supplierphoneString = mBookSupplierPhoneTextView.getText().toString().trim();

        if (mCurrentBookstoreUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && mBookSupplierName == BookstoreEntry.SUPPLIER_UNKNOWN) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookstoreEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookstoreEntry.COLUMN_BOOK_PRICE, priceString);
        values.put(BookstoreEntry.COLUMN_BOOK_QUANTITY, quantityString);
        values.put(BookstoreEntry.COLUMN_SUPPLIER_NAME, mBookSupplierName);
        values.put(BookstoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierphoneString);


        if (mCurrentBookstoreUri == null) {

            Uri newUri = getContentResolver().insert(BookstoreEntry.CONTENT_URI, values);

            if (newUri == null) {

                Toast.makeText(this, getString(R.string.modify_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.modify_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookstoreUri, values, null, null);

            if (rowsAffected == 0) {

                Toast.makeText(this, getString(R.string.modify_updation_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.modify_updation_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.save:
                String nameCheck = mBookNameEditText.getText().toString().trim();
                String priceCheck = mBookPriceEditText.getText().toString().trim();
                String quantityCheck = mBookQuantityEditText.getText().toString().trim();
                String supplierNameCheck = mBookSupplierNameSpinner.getSelectedItem().toString().trim();

                if (quantityCheck.isEmpty() && priceCheck.isEmpty() || quantityCheck.isEmpty() && nameCheck.isEmpty() || priceCheck.isEmpty() && nameCheck.isEmpty()
                        || supplierNameCheck.equals("Unknown(required)") && nameCheck.isEmpty() || supplierNameCheck.equals("Unknown(required)") && priceCheck.isEmpty() ||
                        supplierNameCheck.equals("Unknown(required)") && quantityCheck.isEmpty()) {
                    Toast.makeText(this, "Necessary information missing.", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (nameCheck.isEmpty()) {
                    Toast.makeText(this, "No name has been entered.", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (priceCheck.isEmpty()) {
                    Toast.makeText(this, "No price has been entered.", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (quantityCheck.isEmpty()) {
                    Toast.makeText(this, "No quantity has been entered.", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (supplierNameCheck.equals("Unknown(required)")) {
                    Toast.makeText(this, "No valid supplier name and phone number has not been entered.", Toast.LENGTH_SHORT).show();
                    return true;

                } else {
                    saveBook();
                    NavUtils.navigateUpFromSameTask(this);
                    finish();
                    return true;
                }
            case R.id.delete:

                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:

                if (!mBookChange) {
                    NavUtils.navigateUpFromSameTask(BookModifierActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(BookModifierActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);

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

            int nameColumnIndex = cursor.getColumnIndex(BookstoreEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookstoreEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookstoreEntry.COLUMN_BOOK_QUANTITY);
            int suppliernameColumnIndex = cursor.getColumnIndex(BookstoreEntry.COLUMN_SUPPLIER_NAME);
            int supplierphonenumberColumnIndex = cursor.getColumnIndex(BookstoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            Button modify_detailsbutton = findViewById(R.id.details_button_in_modify);

            final int bookIDColumnIndex = cursor.getColumnIndex(BookstoreContract.BookstoreEntry._ID);

            String currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            int currentSuppliername = cursor.getInt(suppliernameColumnIndex);
            String currentSupplierphonenumber = cursor.getString(supplierphonenumberColumnIndex);
            final String bID = cursor.getString(bookIDColumnIndex);

            mBookNameEditText.setText(currentName);
            mBookPriceEditText.setText(Integer.toString(currentPrice));
            mBookQuantityEditText.setText(Integer.toString(currentQuantity));
            mBookSupplierPhoneTextView.setText(currentSupplierphonenumber);

            switch (currentSuppliername) {
                case BookstoreEntry.SUPPLIER_GOODBOOKS:
                    mBookSupplierNameSpinner.setSelection(1);
                    break;
                case BookstoreEntry.SUPPLIER_FICTIONFANS:
                    mBookSupplierNameSpinner.setSelection(2);
                    break;
                case BookstoreEntry.SUPPLIER_BOOKHOUSE:
                    mBookSupplierNameSpinner.setSelection(3);
                    break;
                case BookstoreEntry.SUPPLIER_READWELL:
                    mBookSupplierNameSpinner.setSelection(4);
                    break;
                default:
                    mBookSupplierNameSpinner.setSelection(0);
                    break;

            }

            modify_detailsbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!mBookChange) {
                        Intent intent = new Intent(BookModifierActivity.this, BookDetailsActivity.class);
                        Uri currentBookstoreUri = ContentUris.withAppendedId(BookstoreContract.BookstoreEntry.CONTENT_URI, Long.parseLong(bID));
                        intent.setData(currentBookstoreUri);
                        startActivity(intent);
                    } else {
                        DialogInterface.OnClickListener discardButtonClickListener =
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(BookModifierActivity.this, BookDetailsActivity.class);
                                        Uri currentBookstoreUri = ContentUris.withAppendedId(BookstoreContract.BookstoreEntry.CONTENT_URI, Long.parseLong(bID));
                                        intent.setData(currentBookstoreUri);
                                        startActivity(intent);
                                    }
                                };

                        switchViewDialog(discardButtonClickListener);
                    }
                }
            });


        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookNameEditText.setText("");
        mBookQuantityEditText.setText("");
        mBookPriceEditText.setText("");
        mBookSupplierNameSpinner.setSelection(0);
        mBookSupplierPhoneTextView.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes);
        builder.setPositiveButton(R.string.discard_changes, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void switchViewDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.continue_with_unsaved_changes);
        builder.setPositiveButton(R.string.continue_without_saving, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_modify, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentBookstoreUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (!mBookChange) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
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
        NavUtils.navigateUpFromSameTask(BookModifierActivity.this);
    }

}