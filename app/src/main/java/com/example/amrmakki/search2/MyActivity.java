package com.example.amrmakki.search2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Map;

public class MyActivity extends Activity {

    ImageButton save;
    EditText editSearch, editTag;
    ListView list;
    SharedPreferences sharedPreferences;
    ArrayList<String> listOfTags;
    ArrayAdapter<String> adapter , options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        save = (ImageButton) findViewById(R.id.image1);
        save.setBackgroundResource(R.drawable.save);


        list = (ListView) findViewById(R.id.list1);

        listOfTags = new ArrayList<String>();

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.rows, listOfTags);

        populateList();

        editSearch = (EditText) findViewById(R.id.editSearch);
        editTag = (EditText) findViewById(R.id.etTag);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTag.setEnabled(true);
                sharedPreferences.edit().putString(editTag.getText().toString(),editSearch.getText().toString()).commit();
                populateList();
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) parent.findViewById(R.id.rowid);
                final String text = textView.getText().toString();

                LayoutInflater li = LayoutInflater.from(MyActivity.this);
                View view1= li.inflate(R.layout.alert_view, null);

                AlertDialog.Builder alert = new AlertDialog.Builder(MyActivity.this);
                alert.setTitle("Share, Edit or Delete the search tagged as "+text);
                alert.setView(view1);
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alert.show();

                String []opt = {"SHARE","EDIT","DELETE"};
                options = new ArrayAdapter<String>(getApplicationContext(), R.layout.rows1, opt );
                ListView list2 = (ListView) view1.findViewById(R.id.list2);
                list2.setAdapter(options);
                list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch(position){
                            case 1:
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_TEXT, sharedPreferences.getString(text,null));
                                startActivity(Intent.createChooser(i, "Share search to:" ));
                                break;
                            case 2:
                                editTag.setText(text);
                                editTag.setEnabled(false);
                                if(editSearch.requestFocus()) {
                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                }
                                break;
                            case 3:
                                sharedPreferences.edit().remove(text).commit();

                                populateList();
                                break;
                        }
                    }
                });

                return true;
            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) parent.findViewById(R.id.rowid);
                final String text = textView.getText().toString();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                String URL = "http://www.uta.edu/search/?q=";
                String a[] = text.split(" ");
                for(int i=0;i<a.length;i++){
                    URL = URL + a[i];
                    if(i != a.length-1){
                        URL = URL + "+";
                    }

                }
                browserIntent.setData(Uri.parse(URL));

                startActivity(browserIntent);
            }
        });


    }

    private void populateList() {
        Map<String,?> allTags =  sharedPreferences.getAll();
        adapter.clear();

        for(Map.Entry<String, ?> entry : allTags.entrySet()){
            listOfTags.add(entry.getKey());
            adapter.add(entry.getKey());
        }

        list.setAdapter(adapter);
    }
}
