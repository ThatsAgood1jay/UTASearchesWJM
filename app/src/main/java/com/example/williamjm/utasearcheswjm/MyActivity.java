package com.example.williamjm.utasearcheswjm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MyActivity extends Activity {


    ArrayList<String> tagsArray;
    ListView queryList;
    EditText queryEdit;
    EditText tagsEdit;
    final Context context = this;
    ArrayAdapter<String> adapter;

    String tag;
    SharedPreferences shared;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        shared = getPreferences(MODE_PRIVATE);
        tagsArray = new ArrayList<String>(shared.getAll().keySet());

        queryList = (ListView) findViewById(R.id.queryList);

        queryEdit = (EditText) findViewById(R.id.queryField);

        tagsEdit = (EditText) findViewById(R.id.tagsEdit);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tagsArray);

        queryList.setAdapter(adapter);

        queryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {

                dialogClick(queryList);

                return true;
            }
        });

        queryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                String selection = queryList.getItemAtPosition(position).toString();
                Uri.encode(selection);
                Intent i;
                i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.uta.edu/search/?q=" + selection));
                startActivity(i);

            }
        });

        loadPreferences();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void saveQuery(View v) {

        String queryText = MyActivity.this.queryEdit.getText().toString();
        String tagEdit = MyActivity.this.tagsEdit.getText().toString();
        tagsArray.add(tagEdit);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(tagEdit, queryText);
        queryEdit.setText("Enter Search");
        tagsEdit.setText("Enter Tags");

        editor.apply();

        loadPreferences();

        ((InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                tagsEdit.getWindowToken(), 0);
    }


    private void loadPreferences()
    {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String queryData = sharedPreferences.getString("Query","");
        queryData += " " + sharedPreferences.getString("Tags", String.valueOf(2));
        String tagData = sharedPreferences.getString("Tags" , "");
        tagsArray.add(tagData);
    }

   // @Override
    public void dialogClick(View arg0) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("What would you like to do?");

        alertDialogBuilder
                .setMessage("")
                .setCancelable(false)
                .setPositiveButton("Share",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                onShareClick();
                            }
                        })
                .setNeutralButton("Edit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                dialog.cancel();
                                queryEdit.requestFocus();

                            }
                        })
                .setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                onDeleteClick();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

        public void onShareClick()
        {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Share"));
        }

        public void onDeleteClick()
        {


            queryList.setAdapter(adapter);
            queryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                    SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();


                    tag = queryList.getItemAtPosition(position).toString();
                    tagsArray.remove(tag);

                    editor.remove(tag);
                    editor.apply();
                    adapter.notifyDataSetChanged();
                }
            });

        }
    }