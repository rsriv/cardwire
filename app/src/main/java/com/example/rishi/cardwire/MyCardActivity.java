package com.example.rishi.cardwire;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MyCardActivity extends AppCompatActivity {
    public ArrayList<Card> myCards = new ArrayList<Card>();
    public ArrayList<Card> cache = new ArrayList<Card>();
    public WriteViewAdapter writeViewAdapter;

    public void displayCards(ArrayList<Card> cards){
        writeViewAdapter = new WriteViewAdapter(this, cards);
        ListView listView = (ListView) findViewById(R.id.listviewwrite);
        listView.setAdapter(writeViewAdapter);
    }

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

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Toast.makeText(this,"Internal Error",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this,"Error: Please Restart CardWire",Toast.LENGTH_SHORT).show();
        }

        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_card);

        String cardString = readFromFile(this);
        myCards = createCardsfromString(cardString);
        if (myCards.size() == 0) {
            myCards.add(new Card("Facebook", " www.facebook.com/yourlinkhere"));
            myCards.add(new Card("Twitter", "www.twitter.com/yourlinkhere"));
            myCards.add(new Card("LinkedIn", "https://www.linkedin.com/in/yourlinkhere"));
        }
        cache = myCards;
        displayCards(myCards);

    }

    protected void saveCard (View v){
        //WRITE CARD
        for (int i = 0;i<myCards.size();i++){
            ListView listView = (ListView) findViewById(R.id.listviewwrite);
            View view = listView.getChildAt(i);
            EditText typeField = (EditText) view.findViewById(R.id.typeField);
            EditText linkField = (EditText) view.findViewById(R.id.linkField);
            myCards.get(i).setType(typeField.getText().toString());
            myCards.get(i).setLink(linkField.getText().toString());
        }

        String cardString = createStringfromCards(myCards);

        writeToFile(cardString,this);
        Toast.makeText(this,"Card Saved!",Toast.LENGTH_SHORT).show();
    }

    public void add (View v){
        Log.d("poo","abc");
        ListView listView = (ListView) findViewById(R.id.listviewwrite);
        Card newCard = new Card("","");
        int pos = listView.getPositionForView(v)+1;
        Log.d("num ", Integer.toString(pos));
        for (int i = 0;i<cache.size();i++){
            View view = listView.getChildAt(i);
            EditText typeField = (EditText) view.findViewById(R.id.typeField);
            EditText linkField = (EditText) view.findViewById(R.id.linkField);
            cache.get(i).setType(typeField.getText().toString());
            cache.get(i).setLink(linkField.getText().toString());
            Log.d(Integer.toString(i)+" Type: "+cache.get(i).getType() + " Link: "+cache.get(i).getLink(),"here");
        }
        cache.add(pos,newCard);


        Log.d(Integer.toString(cache.size()-1)+" Type: "+cache.get(cache.size()-1).getType() + " Link: "+cache.get(cache.size()-1).getLink(),"here");
        displayCards(cache);
    }

    public void delete (View v){
        ListView listView = (ListView) findViewById(R.id.listviewwrite);
        int pos = listView.getPositionForView(v);
        for (int i = 0;i<cache.size();i++){
            View view = listView.getChildAt(i);
            EditText typeField = (EditText) view.findViewById(R.id.typeField);
            EditText linkField = (EditText) view.findViewById(R.id.linkField);
            cache.get(i).setType(typeField.getText().toString());
            cache.get(i).setLink(linkField.getText().toString());
        }
        if (cache.size() > 1) {
            cache.remove(pos);
            displayCards(cache);
        }
        else {
            //do nothing
        }
    }

}
