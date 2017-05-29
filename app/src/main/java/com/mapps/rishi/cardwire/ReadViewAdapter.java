package com.mapps.rishi.cardwire;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rishi on 2017-01-21.
 */

public class ReadViewAdapter extends ArrayAdapter<Card>{
    private final Context context;
    private final ArrayList<Card> cardsArrayList;

    public ReadViewAdapter(Context context, ArrayList<Card> cardsArrayList){
        super(context, R.layout.listviewread, cardsArrayList);

        this.context = context;
        this.cardsArrayList = cardsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.listviewread, parent, false);

        // 3. Get the two text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(R.id.label);
        TextView linkView = (TextView) rowView.findViewById(R.id.link);

        // 4. Set the text for textView
        labelView.setText(cardsArrayList.get(position).getType());
        linkView.setText(cardsArrayList.get(position).getLink());

        // 5. retrn rowView
        return rowView;
    }
}
