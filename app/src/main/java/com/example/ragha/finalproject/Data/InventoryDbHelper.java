package com.example.ragha.finalproject.Data;

/**
 * Created by ragha on 8/13/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN_ITEM_IMAGE;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN_ITEM_NAME;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN_ITEM_QUANTITY;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN_ITEM_SUPPLIER_NAME;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN__ITEM_PRICE;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.CREATE_TABLE_INVENTORY;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.TABLE_NAME;
import static com.example.ragha.finalproject.Data.InventoryContract.Input._ID;


public class InventoryDbHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "inventory.db";
    public final static int DB_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_INVENTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertItem(ItemEntry item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, item.getmProductName());
        values.put(COLUMN__ITEM_PRICE, item.getmPrice());
        values.put(COLUMN_ITEM_QUANTITY, item.getmQuantity());
        values.put(COLUMN_ITEM_SUPPLIER_NAME, item.getmSupplierName());
        values.put(COLUMN_ITEM_IMAGE, item.getmImage());
        long id = db.insert(TABLE_NAME, null, values);
    }

    public Cursor readInput() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                _ID,
                COLUMN_ITEM_NAME,
                COLUMN__ITEM_PRICE,
                COLUMN_ITEM_QUANTITY,
                COLUMN_ITEM_SUPPLIER_NAME,
                COLUMN_ITEM_IMAGE
        };
        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readItem(long itemId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                _ID,
                COLUMN_ITEM_NAME,
                COLUMN__ITEM_PRICE,
                COLUMN_ITEM_QUANTITY,
                COLUMN_ITEM_SUPPLIER_NAME,
                COLUMN_ITEM_IMAGE
        };
        String selection = _ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(itemId)};

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public void updateItem(long currentItemId, int quantity, String newName, int price, String supplier) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_QUANTITY, quantity);
        values.put(COLUMN_ITEM_NAME, newName);
        values.put(COLUMN__ITEM_PRICE, price);
        values.put(COLUMN_ITEM_SUPPLIER_NAME, supplier);
        String selection = _ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(currentItemId)};
        db.update(TABLE_NAME,
                values, selection, selectionArgs);
    }

    public void sellItem(long itemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        int newQuantity = 0;
        if (quantity > 0) {
            newQuantity = quantity - 1;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_QUANTITY, newQuantity);
        String selection = _ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(itemId)};
        db.update(TABLE_NAME,
                values, selection, selectionArgs);
    }
}
