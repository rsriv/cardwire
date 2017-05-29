package com.mapps.rishi.cardwire;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by rishi on 2017-01-21.
 */

public class WriteViewAdapter extends ArrayAdapter<Card>{
    private final Context context;
    private ArrayList<Card> cardsArrayList;

    public WriteViewAdapter(Context context, ArrayList<Card> cardsArrayList){
        super(context, R.layout.listviewwrite, cardsArrayList);
        this.context = context;
        this.cardsArrayList = cardsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.listviewwrite, parent, false);

        // 3. Get the two text view from the rowView
        EditText labelView = (EditText) rowView.findViewById(R.id.typeField);
        EditText linkView = (EditText) rowView.findViewById(R.id.linkField);

        // 4. Set the text for textView
        labelView.setText(cardsArrayList.get(position).getType());
        linkView.setText(cardsArrayList.get(position).getLink());

        // 5. retrn rowView
        return rowView;
    }

    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
