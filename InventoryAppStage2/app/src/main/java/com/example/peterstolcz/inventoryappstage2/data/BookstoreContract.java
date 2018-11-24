package com.example.peterstolcz.inventoryappstage2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookstoreContract {


    public static final String CONTENT_AUTHORITY = "com.example.peterstolcz.inventoryappstage2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public final static String PATH_BOOKSTORE = "bookstore";

    public BookstoreContract() {
    }

    public final static class BookstoreEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKSTORE);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKSTORE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKSTORE;


        public final static String TABLE_NAME = "bookstore";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_BOOK_NAME = "Name";

        public final static String COLUMN_BOOK_PRICE = "Price";

        public final static String COLUMN_BOOK_QUANTITY = "Quantity";

        public final static String COLUMN_SUPPLIER_NAME = "Supplier_name";

        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "Supplier_phone_number";

        public static final int SUPPLIER_UNKNOWN = 0;
        public static final int SUPPLIER_GOODBOOKS = 1;
        public static final int SUPPLIER_FICTIONFANS = 2;
        public static final int SUPPLIER_BOOKHOUSE = 3;
        public static final int SUPPLIER_READWELL = 4;

        public static boolean isValidSupplierName(int suppliername) {
            if (suppliername == SUPPLIER_UNKNOWN || suppliername == SUPPLIER_GOODBOOKS ||
                    suppliername == SUPPLIER_FICTIONFANS || suppliername == SUPPLIER_BOOKHOUSE ||
                    suppliername == SUPPLIER_READWELL) {
                return true;
            }
            return false;


        }

    }

}