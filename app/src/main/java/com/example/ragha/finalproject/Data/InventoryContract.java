package com.example.ragha.finalproject.Data;

/**
 * Created by ragha on 8/13/2017.
 */

import android.provider.BaseColumns;

public class InventoryContract {
    public InventoryContract() {
    }

    public static final class Input implements BaseColumns {

        public static final String TABLE_NAME = "sales";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN__ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_ITEM_IMAGE = "image";

        public static final String CREATE_TABLE_INVENTORY = "CREATE TABLE " +
                TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_ITEM_NAME + " TEXT NOT NULL," +
                COLUMN__ITEM_PRICE + " TEXT NOT NULL," +
                COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                COLUMN_ITEM_SUPPLIER_NAME + " TEXT NOT NULL," +
                COLUMN_ITEM_IMAGE + " TEXT NOT NULL" + ");";
    }
}
