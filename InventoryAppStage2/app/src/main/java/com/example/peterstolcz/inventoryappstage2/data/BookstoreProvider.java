package com.example.peterstolcz.inventoryappstage2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class BookstoreProvider extends ContentProvider {

    private static final int BOOKSTORE = 100;


    private static final int BOOKSTORE_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(BookstoreContract.CONTENT_AUTHORITY, BookstoreContract.PATH_BOOKSTORE, BOOKSTORE);
        sUriMatcher.addURI(BookstoreContract.CONTENT_AUTHORITY, BookstoreContract.PATH_BOOKSTORE + "/#", BOOKSTORE_ID);
    }

    public static final String LOG_TAG = BookstoreProvider.class.getSimpleName();

    private BookstoreDbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new BookstoreDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKSTORE:

                cursor = database.query(BookstoreContract.BookstoreEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOKSTORE_ID:

                selection = BookstoreContract.BookstoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookstoreContract.BookstoreEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Querying of unknown URI is not possible: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKSTORE:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not possible for: " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(BookstoreContract.BookstoreEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed insertion of row for: " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);


        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKSTORE:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKSTORE_ID:

                selection = BookstoreContract.BookstoreEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not possible for " + uri);
        }


    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookstoreContract.BookstoreEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated !=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int matcher = sUriMatcher.match(uri);
        switch (matcher) {
            case BOOKSTORE:
                rowsDeleted = database.delete(BookstoreContract.BookstoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKSTORE_ID:
                selection = BookstoreContract.BookstoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookstoreContract.BookstoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is possible for: " + uri);
        }
        if (rowsDeleted !=0){
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKSTORE:
                return BookstoreContract.BookstoreEntry.CONTENT_LIST_TYPE;
            case BOOKSTORE_ID:
                return BookstoreContract.BookstoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}