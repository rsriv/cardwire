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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DisplayInfoActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback,
        NfcAdapter.CreateNdefMessageCallback{

    private NfcAdapter mNfcAdapter;
    public  ArrayList<Card> myCards = new ArrayList<Card>();
    public  ArrayList<Card> receivedCards = new ArrayList<Card>();
    String StringArray [] = {"Facebook","Twitter","Instagram","LinkedIn"};

    public static byte[] serialize(ArrayList<Card> obj)  {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream o;
        try {
            o= new ObjectOutputStream(baos);
            for (Object i : obj) {
                o.writeObject(i);
            }
        }
        catch(IOException e){}

        return baos.toByteArray();
    }

    public static Card deserialize(byte[] bytes) throws IOException, ClassNotFoundException {

        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return (Card)o.readObject();

            }
        }
    }

    public NdefRecord[] createRecords() {

        NdefRecord[] records = new NdefRecord[myCards.size()+1];
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            for (int i = 0; i < myCards.size(); i++) {

                byte[] payload = serialize(myCards);

                NdefRecord record = new NdefRecord(
                        NdefRecord.TNF_WELL_KNOWN,  //Our 3-bit Type name format
                        NdefRecord.RTD_TEXT,        //Description of our payload
                        new byte[0],                //The optional id for our Record
                        payload);                   //Our payload for the Record

                records[i] = record;
            }
        }
        else {
            for (int i = 0; i < myCards.size(); i++){
                byte[] payload = serialize(myCards);
                NdefRecord record = NdefRecord.createMime("text/plain",payload);
                records[i] = record;
            }
        }
        records[myCards.size()] = NdefRecord.createApplicationRecord(getPackageName());
        return records;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_info);

        //Load Card Data
        myCards.add(new Card("Facebook"," www.facebook.com/abcd"));
        myCards.add(new Card("Twitter","www.twitter.com/abcd"));
        myCards.add(new Card("LinkedIn","www.linkedin.com/in/abcd"));

        //Check if NFC is available on device
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            Toast.makeText(this, "NFC Connected",
                    Toast.LENGTH_SHORT).show();
            //This will refer back to createNdefMessage for what it will send
            mNfcAdapter.setNdefPushMessageCallback(this, this);

            //This will be called if the message is sent successfully
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
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

    //NFC Receiver Methods
    private void handleNfcIntent(Intent NfcIntent) throws IOException, ClassNotFoundException {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                receivedCards.clear();
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for (NdefRecord record:attachedRecords) {
                    Card thisCard = new Card(deserialize(record.getPayload()));

                    //Make sure we don't pass along our AAR (Android Application Record)
                    if (thisCard.equals(getPackageName())) { continue; }
                    receivedCards.add(thisCard);
                }
                Toast.makeText(this, "Received " + receivedCards.size() +
                        " Cards", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Received Blank Parcel", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent){
        try {
            handleNfcIntent(intent);
        } catch (IOException e){
            //do nothing
        } catch (ClassNotFoundException c){
            //do nothing
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            handleNfcIntent(getIntent());
        } catch (IOException e){
            //do nothing
        } catch (ClassNotFoundException c){
            //do nothing
        }
    }

    //NFC Sender Methods
    public void onNdefPushComplete(NfcEvent event) {
        //This is called when the system detects that our NdefMessage was
        //Successfully sent.
        Toast.makeText(this,"CardWired!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //This will be called when another NFC capable device is detected.
        if (myCards.size() == 0) {
            return null;
        }
        //We'll write the createRecords() method in just a moment
        NdefRecord[] recordsToAttach = createRecords();
        //When creating an NdefMessage we need to provide an NdefRecord[]
        return new NdefMessage(recordsToAttach);
    }
    
    
}
