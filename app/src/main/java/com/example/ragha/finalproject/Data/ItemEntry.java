package com.example.ragha.finalproject.Data;

/**
 * Created by ragha on 8/13/2017.
 */

public class ItemEntry {

    private final String mProductName;
    private final String mPrice;
    private final int mQuantity;
    private final String mSupplierName;
    private final String mImage;

    public ItemEntry(String productName, String price, int quantity, String supplierName, String image) {
        mProductName = productName;
        mPrice = price;
        mQuantity = quantity;
        mSupplierName = supplierName;
        mImage = image;
    }

    public String getmProductName() {
        return mProductName;
    }

    public String getmPrice() {
        return mPrice;
    }

    public int getmQuantity() {
        return mQuantity;
    }

    public String getmSupplierName() {
        return mSupplierName;
    }


    public String getmImage() {
        return mImage;
    }

    @Override
    public String toString() {
        return "StockItem{" +
                "productName='" + mProductName + '\'' +
                ", price='" + mPrice + '\'' +
                ", quantity=" + mQuantity +
                ", supplierName='" + mSupplierName + '\'' +
                '}';
    }

}
