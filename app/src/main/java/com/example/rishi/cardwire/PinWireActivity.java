package com.example.rishi.cardwire;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.net.URISyntaxException;

public class PinWireActivity extends AppCompatActivity {
    SocketApplication app;
    private Socket mSocket;

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
                        AlertDialog.Builder box = new AlertDialog.Builder(PinWireActivity.this);
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
                    else { //response is yes

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
                    Log.d("test ","abc");
                    JSONObject data = (JSONObject) args[0];
                    final String pin;
                    try {
                        //pin = data.getString("card");
                        pin = data.getString("from");
                        String from = data.getString("to");
                        Log.d("card: ",pin);
                    } catch (JSONException e) {
                        return;
                    }
                    AlertDialog.Builder box = new AlertDialog.Builder(PinWireActivity.this);
                    box.setMessage("Share card with " + pin+"?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id){
                                    //send add response yes
                                    JSONObject obj = new JSONObject();
                                    String card = readFromFile();
                                    try {
                                        obj.put("response", "y");
                                        obj.put("to", pin);
                                        obj.put("from", readPin());
                                        obj.put("card", card);
                                    }
                                    catch (JSONException x){}
                                    Log.d("Sent ","YES");
                                    mSocket.emit("add response", obj);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id){
                                    //send add response yes
                                    JSONObject obj = new JSONObject();
                                    try {
                                        obj.put("response", "n");
                                        obj.put("to", pin);
                                        obj.put("from", readPin());
                                    }
                                    catch (JSONException x){}
                                    Log.d("Sent ","NO");
                                    mSocket.emit("add response", obj);
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert =  box.create();
                    alert.setTitle("Request from "+pin);
                    alert.show();


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

    private String readPin (){
        SharedPreferences pref = getSharedPreferences("PREF_GENERIC", Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.pin_key_default);
        String pin = pref.getString(getString(R.string.pin_key), defaultValue);
        //  SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return pin;
    }

    public void addUser (View v) {
        JSONObject obj = new JSONObject();
        EditText e = (EditText) findViewById(R.id.add_pin);
        String to = e.getText().toString();
        String card = readFromFile();
        try {
            obj.put("to", to);
            obj.put("from", readPin());
            obj.put("card", card);
        }
        catch (JSONException x){}
        Log.d("Sent ",obj.toString());
        mSocket.emit("add request", obj);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_wire);

        app = (SocketApplication) getApplication();
        Log.d(app.getSocket().toString(),"");

        //Initialize socket
        try {
            mSocket= app.getSocket();


        }
        catch(NullPointerException e){
            Log.d("failed class","");
            try {
                mSocket = IO.socket(Constants.SERVER_URL);
            }
            catch (URISyntaxException e1){

            }

        }
        mSocket.on("add request",addReq);
        mSocket.on("add response",addResp);
        mSocket.connect();
    }
}
