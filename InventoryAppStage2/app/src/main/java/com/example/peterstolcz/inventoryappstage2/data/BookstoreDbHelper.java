package com.example.peterstolcz.inventoryappstage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.peterstolcz.inventoryappstage2.data.BookstoreContract.BookstoreEntry;

public class BookstoreDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookstoreDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "bookstore.db";

    private static final int DATABASE_VERSION = 1;

    public BookstoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_BOOKSTORE_TABLE =  "CREATE TABLE " + BookstoreEntry.TABLE_NAME + " ("
                + BookstoreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookstoreEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookstoreEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL, "
                + BookstoreEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL, "
                + BookstoreEntry.COLUMN_SUPPLIER_NAME + " INTEGER NOT NULL, "
                + BookstoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT);";

        db.execSQL(SQL_CREATE_BOOKSTORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}