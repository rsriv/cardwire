package com.example.rishi.cardwire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DisplayInfoActivity extends AppCompatActivity {



    String StringArray [] = {"Facebook","Twitter","Instagram","LinkedIn"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_info);
        Intent intent = getIntent();
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.listview,StringArray);
        ListView listView = (ListView) findViewById(R.id.listview1);
        listView.setAdapter(adapter);

    }
}
