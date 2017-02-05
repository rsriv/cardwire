package com.example.rishi.cardwire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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
