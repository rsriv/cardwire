package com.example.rishi.cardwire;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class DisplayInfoActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback,
        NfcAdapter.CreateNdefMessageCallback{

    private NfcAdapter mNfcAdapter;
    public  ArrayList<Card> myCards = new ArrayList<Card>();
    public  ArrayList<Card> receivedCards = new ArrayList<Card>();
    public ReadViewAdapter readViewAdapter;
    public String cardString = "";
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

    public NdefRecord[] createRecords() {
        String toSend = createStringfromCards(myCards);
        NdefRecord[] records = new NdefRecord[2];
        //Older Api
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {

            byte[] payload = toSend.
                    getBytes(Charset.forName("UTF-8"));
            NdefRecord record = new NdefRecord(
                    NdefRecord.TNF_WELL_KNOWN,
                    NdefRecord.RTD_TEXT,
                    new byte[0],
                    payload);

            records[0] = record;

        }
        //Higher api
        else {

            byte[] payload = toSend.
                    getBytes(Charset.forName("UTF-8"));

            NdefRecord record = NdefRecord.createMime("text/plain",payload);
            records[0] = record;

        }
        records[1] = NdefRecord.createApplicationRecord(getPackageName());
        return records;
    }

    public void displayCards(ArrayList<Card> cards){
        readViewAdapter = new ReadViewAdapter(DisplayInfoActivity.this, cards);
        // 2. Get ListView from activity_main.xml
        ListView listView = (ListView) findViewById(R.id.listviewread);
        // 3. setListAdapter
        listView.setAdapter(readViewAdapter);

    }

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
        setContentView(R.layout.activity_display_info);
        //Load Card Data
        Intent intent = getIntent();
        cardString = intent.getExtras().getString("card");
        Log.d("CardString ",cardString);
        if(cardString==null || cardString.equals("")) {
            Log.d("CARDSTRING NULL","");
            cardString = readFromFile();
        }
        myCards = createCardsfromString(cardString);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            //Check for NFC
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if(mNfcAdapter != null) {
                //sets createNdefMessage
                mNfcAdapter.setNdefPushMessageCallback(this, this);

                //Successfully sent message
                mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
            }
        }
        else {
            Toast.makeText(this, "NFC not available on this device",
                    Toast.LENGTH_SHORT).show();
        }
        displayCards(myCards);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(cardString==null || cardString.equals("")) {
            cardString = readFromFile();

        myCards = createCardsfromString(cardString);
        handleNfcIntent(getIntent());
        if(receivedCards.size() == 0){

            displayCards(myCards);
        }
        else{
            displayCards(receivedCards);
        }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {

        if(cardString==null || cardString.equals("")) {
            cardString = readFromFile();
            myCards = createCardsfromString(cardString);

            displayCards(myCards);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if(cardString==null || cardString.equals("")) {
            cardString = readFromFile();
            String cardString = readFromFile();
            myCards = createCardsfromString(cardString);

            displayCards(myCards);
        }
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        //Successfully sent
        Toast.makeText(this,"CardWired!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefRecord[] recordsToAttach = createRecords();
        return new NdefMessage(recordsToAttach);
    }

    //NFC Receive Methods
    private void handleNfcIntent(Intent NfcIntent) {

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
                    //Log.d(receivedCards.get(2).getType(),receivedCards.get(2).getLink());
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
        displayCards(receivedCards);
    }
    
    
}
