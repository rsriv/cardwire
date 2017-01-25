package com.example.rishi.cardwire;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
import java.nio.charset.Charset;
import java.util.ArrayList;


public class DisplayInfoActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback,
        NfcAdapter.CreateNdefMessageCallback{


    private NfcAdapter mNfcAdapter;
    public  ArrayList<Card> myCards = new ArrayList<Card>();
    public  ArrayList<Card> receivedCards = new ArrayList<Card>();

    public String createStringfromCards (ArrayList<Card> cards){
        String ret = "";
        for (int i = 0; i<cards.size();i++){
            Card c = cards.get(i);
            if (i == 0) ret = c.getType() + ";" + c.getLink();
            else ret = ret + ";" + c.getType() + ";" + c.getLink();
        }
        ret = ret + ";";
        return ret;
    }

    public ArrayList<Card> createCardsfromString (String s){
        String[] separated = s.split(";");
        String temp = "";
        ArrayList<Card> ret = new ArrayList<>();
        int count = 0;
        for (String data: separated){
            if (count == 0){
                temp = data;
                count++;
            }
            else {
                ret.add(new Card(temp,data));
                count = 0;
            }
        }
        return ret;
    }

    @Override
    public void onResume() {
        super.onResume();
        handleNfcIntent(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_info);

        //Load Card Data
        myCards.add(new Card("Facebook"," www.facebook.com/abc"));
        myCards.add(new Card("Twitter","www.twitter.com/abc"));
        myCards.add(new Card("LinkedIn","www.linkedin.com/in/abc"));

        //Check if NFC is available on device
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            //Handle some NFC initialization here
            //Check if NFC is available on device
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if(mNfcAdapter != null) {
                //This will refer back to createNdefMessage for what it will send
                mNfcAdapter.setNdefPushMessageCallback(this, this);

                //This will be called if the message is sent successfully
                mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
            }
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public NdefRecord[] createRecords() {
        String toSend = createStringfromCards(myCards);
        NdefRecord[] records = new NdefRecord[2];
        //To Create Messages Manually if API is less than
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {

                byte[] payload = toSend.
                        getBytes(Charset.forName("UTF-8"));
                NdefRecord record = new NdefRecord(
                        NdefRecord.TNF_WELL_KNOWN,      //Our 3-bit Type name format
                        NdefRecord.RTD_TEXT,            //Description of our payload
                        new byte[0],                    //The optional id for our Record
                        payload);                       //Our payload for the Record

                records[0] = record;

        }
        //Api is high enough that we can use createMime, which is preferred.
        else {

                byte[] payload = toSend.
                        getBytes(Charset.forName("UTF-8"));

                NdefRecord record = NdefRecord.createMime("text/plain",payload);
                records[0] = record;

        }
        records[1] = NdefRecord.createApplicationRecord(getPackageName());
        return records;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        //This is called when the system detects that our NdefMessage was
        //Successfully sent.
        Toast.makeText(this,"CardWired!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //This will be called when another NFC capable device is detected.
        //We'll write the createRecords() method in just a moment
        NdefRecord[] recordsToAttach = createRecords();
        //When creating an NdefMessage we need to provide an NdefRecord[]
        return new NdefMessage(recordsToAttach);
    }

    //NFC Receive Methods
    private void handleNfcIntent(Intent NfcIntent) {
        Log.d("abc","123");
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                receivedCards.clear();
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for (NdefRecord record:attachedRecords) {
                    String string = new String(record.getPayload());
                    //Make sure we don't pass along our AAR (Android Application Record)
                    if (string.equals(getPackageName())) { continue; }
                    receivedCards=createCardsfromString(string);
                    Log.d(receivedCards.get(0).getType(),receivedCards.get(0).getLink());
                }
                Toast.makeText(this, "Received", Toast.LENGTH_LONG).show();

            }
            else {
                Toast.makeText(this, "Received Blank Parcel", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        handleNfcIntent(intent);
    }
    
    
}
