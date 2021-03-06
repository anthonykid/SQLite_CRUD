package com.example.latihan12;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    String[] daftar;
    String[] listID;

    ListView listView;
    Cursor cursor;
    DBHelper database;
    public static MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fabBut);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pindah = new Intent(MainActivity.this, com.example.latihan12.CreateActivity.class);
                startActivity(pindah);
            }
        });

        getSupportActionBar().setTitle("Daftar Mahasiswa");

        ma = this;
        database = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshList();
    }

    public void refreshList() {

        SQLiteDatabase db = database.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM mahasiswa", null);

        daftar = new String[cursor.getCount()];
        listID = new String[cursor.getCount()];
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            listID[i] = cursor.getString(0).toString();
            daftar[i] = cursor.getString(2).toString();
        }

        listView = findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, daftar));
        listView.setSelected(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                final String selection = daftar[arg2];
                final String selectionID = listID[arg2];

                final CharSequence[] dialogitem = {"Lihat Mahasiswa", "Edit Mahasiswa", "Hapus Mahasiswa"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Pilihan");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                                intent.putExtra("id", selectionID);
                                startActivity(intent);
                                break;
                            case 1:
                                Intent updateIntent = new Intent(getApplicationContext(), UpdateActivity.class);
                                updateIntent.putExtra("id", selectionID);
                                startActivity(updateIntent);
                                break;
                            case 2:
                                SQLiteDatabase db = database.getWritableDatabase();
                                db.execSQL("DELETE FROM mahasiswa WHERE id = '" + selectionID + "'");
                                refreshList();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
        ((ArrayAdapter) listView.getAdapter()).notifyDataSetInvalidated();
    }
}