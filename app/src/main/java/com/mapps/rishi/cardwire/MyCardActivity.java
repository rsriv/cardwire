package com.mapps.rishi.cardwire;

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
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MyCardActivity extends AppCompatActivity {
    public ArrayList<Card> myCards = new ArrayList<Card>();
    public ArrayList<Card> cache = new ArrayList<Card>();
    public WriteViewAdapter writeViewAdapter;
    public ListView listView;

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

    //load cardString from file
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
        cache.clear();
        cache.addAll(myCards);
        writeViewAdapter = new WriteViewAdapter(this, cache);
        listView = (ListView) findViewById(R.id.listviewwrite);
        listView.setAdapter(writeViewAdapter);
        writeViewAdapter.notifyDataSetChanged();
        //displayCards(myCards);

    }

    //write updated card to file
    public void saveCard (View v){
        for (int i = 0;i<cache.size();i++){
            ListView listView = (ListView) findViewById(R.id.listviewwrite);
            View view = writeViewAdapter.getViewByPosition(i,listView);
            EditText typeField = (EditText) view.findViewById(R.id.typeField);
            EditText linkField = (EditText) view.findViewById(R.id.linkField);
            cache.get(i).setType(typeField.getText().toString());
            cache.get(i).setLink(linkField.getText().toString());
        }
        myCards.clear();
        myCards.addAll(cache);
        String cardString = createStringfromCards(cache);

        writeToFile(cardString,this);
        Toast.makeText(this,"Card Saved!",Toast.LENGTH_SHORT).show();
    }

    //add new list item
    public void add (View v){
        Card newCard = new Card("","");
        int pos = listView.getPositionForView(v)+1;
        Log.d("num ", Integer.toString(pos) + " listView.getCount() "+ Integer.toString(listView.getCount()) + " VS. cache.size()" +  Integer.toString(cache.size()));

        //iterate through listview and cache content
        for (int i = 0;i<listView.getCount()-1;i++){
            View view = writeViewAdapter.getViewByPosition(i,listView);
            EditText typeField = (EditText) view.findViewById(R.id.typeField);
            EditText linkField = (EditText) view.findViewById(R.id.linkField);
            cache.get(i).setType(typeField.getText().toString());
            cache.get(i).setLink(linkField.getText().toString());
            Log.d(Integer.toString(i)+" Type: "+cache.get(i).getType() + " Link: "+cache.get(i).getLink(),"here");
        }

        //add new card
        if(pos>=cache.size()) {
            cache.add(newCard);
        }
        else {
            cache.add(pos, newCard);
        }

        //update writeviewadapter
        writeViewAdapter.notifyDataSetChanged();
        listView.requestLayout();
        //Log.d(Integer.toString(cache.size()-1)+" Type: "+cache.get(cache.size()-1).getType() + " Link: "+cache.get(cache.size()-1).getLink(),"here");
       // displayCards(cache);
    }

    //delete listview item
    public void delete (View v){
        ListView listView = (ListView) findViewById(R.id.listviewwrite);
        int pos = listView.getPositionForView(v);

        //iterate through listview and cache cards
        for (int i = 0;i<listView.getCount();i++){
            View view = writeViewAdapter.getViewByPosition(i,listView);
            EditText typeField = (EditText) view.findViewById(R.id.typeField);
            EditText linkField = (EditText) view.findViewById(R.id.linkField);
            cache.get(i).setType(typeField.getText().toString());
            writeViewAdapter.notifyDataSetChanged();
            cache.get(i).setLink(linkField.getText().toString());
            writeViewAdapter.notifyDataSetChanged();
        }

        //update writeViewAdapter
        if (cache.size() > 1) {
            cache.remove(pos);
            writeViewAdapter.notifyDataSetChanged();
            listView.requestLayout();
        }
        else {
            //do nothing
        }
    }

}
