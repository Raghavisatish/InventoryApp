package com.example.ragha.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.ragha.finalproject.Data.InventoryDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.ragha.finalproject.Data.InventoryContract.Input.TABLE_NAME;


public class MainActivity extends AppCompatActivity {

    InventoryDbHelper dbHelper;
    ItemsAdapter adapter;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.empty_view)
    View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dbHelper = new InventoryDbHelper(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        listView.setEmptyView(emptyView);
        Cursor cursor = dbHelper.readInput();
        adapter = new ItemsAdapter(this, cursor);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(dbHelper.readInput());
    }

    public void clickOnViewItem(long id) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    public void clickOnSale(long id, int quantity) {
        dbHelper.sellItem(id, quantity);
        adapter.swapCursor(dbHelper.readInput());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_data:
                deleteAllRowsFromTable();
                adapter.swapCursor(dbHelper.readInput());
        }
        return super.onOptionsItemSelected(item);
    }

    private int deleteAllRowsFromTable() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(TABLE_NAME, null, null);
    }
}
