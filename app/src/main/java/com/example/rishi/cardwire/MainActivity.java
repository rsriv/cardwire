package com.example.rishi.cardwire;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.commit();
            Intent i = new Intent(this,PinActivity.class);
            startActivity(i);
        }

        //Button Menu Animations
        Button b1 = (Button) findViewById(R.id.myCard);
        Button b2 = (Button) findViewById(R.id.wire);
        Button b3 = (Button) findViewById(R.id.wire_pin);
        Animation slideUp;
        b1.setVisibility(View.INVISIBLE);
        b2.setVisibility(View.INVISIBLE);
        b3.setVisibility(View.INVISIBLE);
        slideUp = AnimationUtils.loadAnimation(this,R.anim.slideup);
        b1.startAnimation(slideUp);
        b2.startAnimation(slideUp);
        b3.startAnimation(slideUp);
        b1.setVisibility(View.VISIBLE);
        b2.setVisibility(View.VISIBLE);
        b3.setVisibility(View.VISIBLE);

    }

    //Button OnClick Methods
    public void disp (View view){
        Intent intent = new Intent(this, DisplayInfoActivity.class);
        startActivity(intent);
    }

    public void wirePin (View view){
        Intent intent = new Intent(this, PinWireActivity.class);
        startActivity(intent);
    }

    public void myCard (View view){
        Intent intent = new Intent(this, MyCardActivity.class);
        startActivity(intent);
    }


}
