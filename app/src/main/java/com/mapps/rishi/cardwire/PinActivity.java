package com.mapps.rishi.cardwire;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class PinActivity extends AppCompatActivity {
//HERE

    SocketApplication app;
    private Socket mSocket;

//TO HERE

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String pin;
                    try {
                        pin = data.getString("pin");
                        writePin(pin);
                    } catch (JSONException e) {
                        return;
                    }


                    TextView pinView = (TextView) findViewById(R.id.pin_text_view);
                    pinView.setText(readPin());
                    mSocket.emit("join", readPin());

                }
            });
        }
    };


    private void writePin (String newPin){
        SharedPreferences pref = getSharedPreferences("PREF_GENERIC", Context.MODE_PRIVATE);
        pref.edit().putString(getString(R.string.pin_key), newPin).apply();
        //SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
       // SharedPreferences.Editor editor = sharedPref.edit();
       // editor.putString(getString(R.string.pin_key), newPin);
       // editor.apply();
    }

    private String readPin (){
        SharedPreferences pref = getSharedPreferences("PREF_GENERIC", Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.pin_key_default);
        String pin = pref.getString(getString(R.string.pin_key), defaultValue);
      //  SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return pin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        //HERE -> TODO: - request pin from server //DONE - receive pin from server and write to shared prefs - read pin from shared prefs//done
        //Connect to Socket.io
        app = (SocketApplication) getApplication();
        Log.d(app.getSocket().toString(),"");

        try

        {
            mSocket= app.getSocket();

        }

        catch(NullPointerException e){
            Log.d("failed class","123");
            try {
                mSocket = IO.socket(Constants.SERVER_URL);
            }
            catch (URISyntaxException e1){

            }

        }

        mSocket.on("new user",onNewMessage);
        mSocket.connect();

        if(readPin().equals("0")) {
            mSocket.emit("new user", "Return me a pin"); // TO HERE
        }
        String pin = readPin();

        TextView pinView = (TextView) findViewById(R.id.pin_text_view);
        pinView.setText(pin);
        Toast.makeText(this, "Tap to begin using CardWire",
                Toast.LENGTH_LONG).show();

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        finish();
        return super.dispatchTouchEvent(ev);
    }
}
