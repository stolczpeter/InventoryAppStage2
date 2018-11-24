package com.example.peterstolcz.inventoryappstage2;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.peterstolcz.inventoryappstage2.data.BookstoreContract;

public class BookstoreCursorAdapter extends CursorAdapter {

    public BookstoreCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final TextView bnameTextView = view.findViewById(R.id.text_view_bname);
        TextView bquantityTextView = view.findViewById(R.id.text_view_bquantity);
        TextView bpriceTextView = view.findViewById(R.id.text_view_bprice);
        Button soldbutton = view.findViewById(R.id.sold_button);

        final int bookIDColumnIndex = cursor.getColumnIndex(BookstoreContract.BookstoreEntry._ID);
        int booknameColumnIndex = cursor.getColumnIndex(BookstoreContract.BookstoreEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookstoreContract.BookstoreEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookstoreContract.BookstoreEntry.COLUMN_BOOK_QUANTITY);

        final String bID = cursor.getString(bookIDColumnIndex);
        final String bName = cursor.getString(booknameColumnIndex);
        final String bQuantity = cursor.getString(quantityColumnIndex);
        String bPrice = cursor.getString(priceColumnIndex);

        bnameTextView.setText(bName);
        bpriceTextView.setText("Price:  " + bPrice + " $");
        bquantityTextView.setText("In stock:  " + bQuantity + " pcs");

        Button modifybutton = view.findViewById(R.id.modify_button);
        modifybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookModifierActivity.class);
                Uri currentBookstoreUri = ContentUris.withAppendedId(BookstoreContract.BookstoreEntry.CONTENT_URI, Long.parseLong(bID));
                intent.setData(currentBookstoreUri);
                context.startActivity(intent);
            }
        });

        Button detailsbutton = view.findViewById(R.id.details_button);
        detailsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookDetailsActivity.class);
                Uri currentBookstoreUri = ContentUris.withAppendedId(BookstoreContract.BookstoreEntry.CONTENT_URI, Long.parseLong(bID));
                intent.setData(currentBookstoreUri);
                context.startActivity(intent);
            }
        });

        soldbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BookstoreActivity salecountinactivity = (BookstoreActivity) context;
                salecountinactivity.bookSales(Long.valueOf(bID), Long.valueOf(bQuantity), bName);

            }
        });

    }
}