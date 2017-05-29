package com.mapps.rishi.cardwire;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity {

    SocketApplication app;
    private Socket mSocket;
    final Context context = this;
    private boolean isRunning = false;
    private Emitter.Listener addResp = new Emitter.Listener() { //called when add response is received

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Log.d("add ", "response");
                    JSONObject data = (JSONObject) args[0];
                    final String resp;
                    final String from;
                    try {
                        //pin = data.getString("card");
                        resp = data.getString("response");
                        from = data.getString("to");
                        Log.d("response: ", resp);
                    } catch (JSONException e) {
                        return;
                    }
                    if (resp.equals("n")) {
                        if(isRunning) {
                            AlertDialog.Builder box = new AlertDialog.Builder(MainActivity.this);
                            box.setMessage(from + " rejected your request")
                                    .setCancelable(false)
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {

                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = box.create();
                            alert.setTitle("Response from " + from);
                            alert.show();
                        }
                    }
                    else { //response is yes
                        if(isRunning) {
                            Log.d("Response: ", "YES");
                            Intent intent = new Intent(context, DisplayInfoActivity.class);
                            String message = "";
                            try {
                                message = data.getString("card");
                            } catch (JSONException e) {
                            }
                            Log.d("Card: ", message);
                            intent.putExtra("card", message);
                            startActivity(intent);
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener addReq = new Emitter.Listener() { //called when add request is received

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    final JSONObject data = (JSONObject) args[0];
                    final String pin;
                    try {
                        //pin = data.getString("card");
                        pin = data.getString("from");
                        String from = data.getString("to");
                        Log.d("card: ",pin);
                    } catch (JSONException e) {
                        return;
                    }
                    if(isRunning) {
                        AlertDialog.Builder box = new AlertDialog.Builder(MainActivity.this);
                        box.setMessage("Share card with " + pin + "?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //send add response yes
                                        JSONObject obj = new JSONObject();
                                        String card = readFromFile();
                                        Log.d("Card Sent from usr: ", card);
                                        try {
                                            obj.put("response", "y");
                                            obj.put("to", pin);
                                            obj.put("from", readPin());
                                            obj.put("card", card);
                                        } catch (JSONException x) {
                                        }
                                        Log.d("Sent ", "YES");
                                        mSocket.emit("add response", obj);


                                        Intent intent = new Intent(context, DisplayInfoActivity.class);
                                        String message = "";
                                        try {
                                            message = data.getString("card");
                                        } catch (JSONException e) {
                                        }
                                        Log.d("Card: ", message);
                                        intent.putExtra("card", message);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //send add response yes
                                        JSONObject obj = new JSONObject();
                                        try {
                                            obj.put("response", "n");
                                            obj.put("to", pin);
                                            obj.put("from", readPin());
                                        } catch (JSONException x) {
                                        }
                                        Log.d("Sent ", "NO");
                                        mSocket.emit("add response", obj);
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = box.create();
                        alert.setTitle("Request from " + pin);
                        alert.show();

                    }
                }
            });
        }
    };
    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = this.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Toast.makeText(this,"Error: Please Restart CardWire",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this,"Error: Please Restart CardWire",Toast.LENGTH_SHORT).show();
        }

        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (SocketApplication) getApplication();

        //Log.d(app.getSocket().toString(),"");

        //Initialize socket
        try {
            mSocket= app.getSocket();
            mSocket.on("add request",addReq);
            mSocket.on("add response",addResp);
            mSocket.connect();

        }
        catch(NullPointerException e){
            Log.d("failed class","");
            try {
                mSocket = IO.socket(Constants.SERVER_URL);
            }
            catch (URISyntaxException e1){

            }

        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        if(!previouslyStarted) {
            //initialize card
            writeToFile("Facebook; www.facebook.com/yourlinkhere;Twitter;www.twitter.com/yourlinkhere;LinkedIn;www.linkedin.com/in/yourlinkhere;");

            //show pin
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.apply();
            Intent i = new Intent(this,PinActivity.class);
            startActivity(i);
        }

        //Set pin text
        TextView pinView = (TextView) findViewById(R.id.my_pin_view);
        pinView.setText("My Pin: "+readPin());

        mSocket.emit("join", readPin());

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

    @Override
    protected void onResume (){
        super.onResume();
        //Set pin text
        TextView pinView = (TextView) findViewById(R.id.my_pin_view);
        pinView.setText("My Pin: "+readPin());
        mSocket.emit("join", readPin());
        isRunning = true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        isRunning = false;
    }

    private String readPin (){
        SharedPreferences pref = getSharedPreferences("PREF_GENERIC", Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.pin_key_default);
        String pin = pref.getString(getString(R.string.pin_key), defaultValue);
        //  SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return pin;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    //Button OnClick Methods
    public void disp (View view){
        Intent intent = new Intent(this, DisplayInfoActivity.class);
        intent.putExtra("card", "");
        startActivity(intent);
    }

    public void help (View view){
        Intent intent = new Intent(this, PinActivity.class);
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
