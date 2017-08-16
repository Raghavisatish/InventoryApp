package com.example.ragha.finalproject;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.ragha.finalproject.Data.InventoryDbHelper;
import com.example.ragha.finalproject.Data.ItemEntry;


import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN_ITEM_IMAGE;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN_ITEM_NAME;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN_ITEM_QUANTITY;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN_ITEM_SUPPLIER_NAME;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.COLUMN__ITEM_PRICE;
import static com.example.ragha.finalproject.Data.InventoryContract.Input.TABLE_NAME;
import static com.example.ragha.finalproject.Data.InventoryContract.Input._ID;


public class DetailsActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private InventoryDbHelper dbHelper;
    EditText nameEdit;
    EditText priceEdit;
    EditText quantityEdit;
    EditText supplierNameEdit;
    long currentItemId;
    ImageButton decreaseQuantity;
    ImageButton increaseQuantity;
    Button imageBtn;
    ImageView imageView;
    Uri actualUri;
    private static final int PICK_IMAGE_REQUEST = 0;
    Boolean infoItemHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameEdit = (EditText) findViewById(R.id.itemName_editText);
        priceEdit = (EditText) findViewById(R.id.itemPrice_editText);
        quantityEdit = (EditText) findViewById(R.id.quantity_edit);
        supplierNameEdit = (EditText) findViewById(R.id.itemSupplier_editText);
        decreaseQuantity = (ImageButton) findViewById(R.id.decrease_quantity);
        increaseQuantity = (ImageButton) findViewById(R.id.increase_quantity);
        imageBtn = (Button) findViewById(R.id.select_image);
        imageView = (ImageView) findViewById(R.id.image_view);

        dbHelper = new InventoryDbHelper(this);
        currentItemId = getIntent().getLongExtra("itemId", 0);

        if (currentItemId == 0) {
            setTitle(getString(R.string.title_new_item));
        } else {
            setTitle(getString(R.string.title_edit_item));
            addValuesToEditItem(currentItemId);

        }

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractOneToQuantity();
                infoItemHasChanged = true;
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumOneToQuantity();
                infoItemHasChanged = true;
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToOpenImageSelector();
                infoItemHasChanged = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!infoItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void subtractOneToQuantity() {
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            quantityEdit.setText(String.valueOf(previousValue - 1));
        }
    }

    private void sumOneToQuantity() {
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        quantityEdit.setText(String.valueOf(previousValue + 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentItemId == 0) {
            MenuItem deleteOneItemMenuItem = menu.findItem(R.id.action_delete_item);
            MenuItem orderMenuItem = menu.findItem(R.id.action_order);
            deleteOneItemMenuItem.setVisible(false);
            orderMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // save item in DB
                if (!addItemToDb()) {
                    // saying to onOptionsItemSelected that user clicked button
                    return true;
                }
                finish();
                return true;
            case android.R.id.home:
                if (!infoItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.action_order:
                showOrderConfirmationDialog();
                return true;
            case R.id.action_delete_item:
                // delete one item
                showDeleteConfirmationDialog(currentItemId);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean addItemToDb() {
        boolean isInputted = true;
        if ((!checkIfValueSet(nameEdit, "name")) | (!checkIfValueSet(priceEdit, "price")) | (!checkIfValueSet(quantityEdit, "quantity")) | (!checkIfValueSet(supplierNameEdit, "supplier name"))) {
            isInputted = false;
        }
        if (actualUri == null && currentItemId == 0) {
            isInputted = false;
            imageBtn.setError("Missing image");
        }
        if (!isInputted) {
            return false;
        }

        if (currentItemId == 0) {
            ItemEntry item = new ItemEntry(
                    nameEdit.getText().toString().trim(),
                    priceEdit.getText().toString().trim(),
                    Integer.parseInt(quantityEdit.getText().toString().trim()),
                    supplierNameEdit.getText().toString().trim(),
                    actualUri.toString());
            dbHelper.insertItem(item);
        } else {
            int quantity = Integer.parseInt(quantityEdit.getText().toString().trim());
            String newName = nameEdit.getText().toString();
            String newSupplier = supplierNameEdit.getText().toString();
            int price = Integer.parseInt(priceEdit.getText().toString().trim());
            dbHelper.updateItem(currentItemId, quantity, newName, price, newSupplier);
        }
        return true;
    }

    private boolean checkIfValueSet(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError("Missing product " + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    private void addValuesToEditItem(long itemId) {
        Cursor cursor = dbHelper.readItem(itemId);
        cursor.moveToFirst();
        nameEdit.setText(cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME)));
        priceEdit.setText(cursor.getString(cursor.getColumnIndex(COLUMN__ITEM_PRICE)));
        quantityEdit.setText(cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_QUANTITY)));
        supplierNameEdit.setText(cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_SUPPLIER_NAME)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_IMAGE))));
        nameEdit.setEnabled(true);
        priceEdit.setEnabled(true);
        supplierNameEdit.setEnabled(true);
        imageBtn.setEnabled(false);


    }

    private String createEmailMessage(String name, int price, String supplier, int quantity) {

        String emailMessage = "Name : " + name;
        emailMessage += "\n" + "Each of them Costs : " + price;
        emailMessage += "\n" + "of the Quantity : " + quantity;
        emailMessage += "\n" + " supplier :" + supplier;
        return emailMessage;
    }


    private void showOrderConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_message);

        builder.setNegativeButton(R.string.email, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to email

                String name = nameEdit.getText().toString().trim();
                String priceItem = priceEdit.getText().toString().trim();
                int price = Integer.parseInt(priceItem);
                String supplier = supplierNameEdit.getText().toString().trim();
                String quantityItem = quantityEdit.getText().toString().trim();
                int quantity = Integer.parseInt(quantityItem);

                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_TEXT, createEmailMessage(name, price, supplier, quantity));
                String bodyMessage = "The item is - " +
                        name + "\n Number of items needed - " + quantity + "." + "\n The total price is \t" + price * quantity +
                        "/-";
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private int deleteOneItem(long itemId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = _ID + "=?";
        String[] selectionArgs = {String.valueOf(itemId)};
        int rowsDeleted = database.delete(
                TABLE_NAME, selection, selectionArgs);
        return rowsDeleted;
    }

    private void showDeleteConfirmationDialog(final long itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteOneItem(itemId);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                    // permission was granted
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                actualUri = resultData.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }
}
