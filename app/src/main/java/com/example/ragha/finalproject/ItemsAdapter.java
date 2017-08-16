package com.example.ragha.finalproject;


/**
 * Created by ragha on 8/13/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ragha.finalproject.Data.InventoryContract;


import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN_ITEM_IMAGE;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN_ITEM_NAME;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN_ITEM_QUANTITY;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN__ITEM_PRICE;


public class ItemsAdapter extends CursorAdapter {
    private final MainActivity activity;

    public ItemsAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView quantityTextView;
        ImageView sale;
        ImageView image;
        TextView priceTextView;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        ViewHolder holder = new ViewHolder();


        holder.nameTextView = (TextView) view.findViewById(R.id.product_name);
        holder.quantityTextView = (TextView) view.findViewById(R.id.quantity);
        holder.sale = (ImageView) view.findViewById(R.id.sale);
        holder.image = (ImageView) view.findViewById(R.id.image_view);
        holder.priceTextView = (TextView) view.findViewById(R.id.price);
        view.setTag(holder);

        String name = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_QUANTITY));
        int price = cursor.getInt(cursor.getColumnIndex(COLUMN__ITEM_PRICE));

        holder.image.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_IMAGE))));
        holder.nameTextView.setText(name);
        holder.quantityTextView.setText(String.valueOf(quantity));
        holder.priceTextView.setText(String.valueOf(price));

        final long id = cursor.getLong(cursor.getColumnIndex(InventoryContract.Input._ID));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnViewItem(id);
            }
        });

        holder.sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnSale(id,
                        quantity);
            }
        });
    }
}
