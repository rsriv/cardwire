package com.example.rishi.cardwire;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class DisplayInfoActivity extends AppCompatActivity  {

    private NfcAdapter mNfcAdapter;
    public  ArrayList<Card> myCards = new ArrayList<Card>();
    public  ArrayList<Card> receivedCards = new ArrayList<Card>();
    String StringArray [] = {"Facebook","Twitter","Instagram","LinkedIn"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_info);

        //Load Card Data
        myCards.add(new Card("Facebook"," www.facebook.com/rishi.srivastava.18488"));
        myCards.add(new Card("Twitter","www.twitter.com/suyash218"));
        myCards.add(new Card("LinkedIn","www.linkedin.com/in/rishi-srivastava-bb3585127"));

        //Check if NFC is available on device
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            Toast.makeText(this, "NFC Connected",
                    Toast.LENGTH_SHORT).show();
            //This will refer back to createNdefMessage for what it will send
            //mNfcAdapter.setNdefPushMessageCallback(this, this);

            //This will be called if the message is sent successfully
            //mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
        else {
            Toast.makeText(this, "NFC not available on this device",
                    Toast.LENGTH_SHORT).show();
        }

        Adapter adapter = new Adapter(this, myCards);

        // 2. Get ListView from activity_main.xml
        ListView listView = (ListView) findViewById(R.id.listview1);

        // 3. setListAdapter
        listView.setAdapter(adapter);

    }
    
    
}
