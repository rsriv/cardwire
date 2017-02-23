package com.example.rishi.cardwire;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class PinActivity extends AppCompatActivity {
//HERE
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://480345cd.ngrok.io"); //  insert ngrok tunnel link here
        } catch (URISyntaxException e) {}
    }
//TO HERE

    private void writePin (String newPin){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pin_key), newPin);
        editor.apply();
    }

    private String readPin (){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.pin_key_default);
        String highScore = sharedPref.getString(getString(R.string.pin_key),defaultValue);
        return highScore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        //HERE -> TODO: - request pin from server - receive pin from server and write to shared prefs //done- read pin from shared prefs//done

        writePin("A8398FF");
        String pin = readPin();
        TextView pinView = (TextView) findViewById(R.id.pin_text_view);
        pinView.setText(pin);
        //Connect to Socket.io
        mSocket.connect();
        mSocket.emit("new message", "This is a test message"); // TO HERE
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        finish();
        return super.dispatchTouchEvent(ev);
    }
}
