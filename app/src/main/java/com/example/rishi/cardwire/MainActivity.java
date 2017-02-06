package com.example.rishi.cardwire;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mPlanetTitles = {"Home",
                                        "Wire",
                                        "My Card"
                                        };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    protected void disp (View view){
        Intent intent = new Intent(this, DisplayInfoActivity.class);
        startActivity(intent);
    }
    protected void myCard (View view){
        Intent intent = new Intent(this, MyCardActivity.class);
        startActivity(intent);
    }


}
