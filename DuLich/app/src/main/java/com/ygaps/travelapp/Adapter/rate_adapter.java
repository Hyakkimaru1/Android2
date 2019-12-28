package com.ygaps.travelapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ygaps.travelapp.R;
import com.ygaps.travelapp.aRate;
import com.ygaps.travelapp.stopPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class rate_adapter extends ArrayAdapter<aRate> {
    ArrayList<aRate> noteList;

    public rate_adapter(Context context, int resource, ArrayList<aRate> objects) {
        super(context,resource,objects);
        noteList = objects;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stubrate_adapter
        return noteList.size();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate( R.layout.item_rate, null);
        aRate p =getItem( position );
        if (p!=null) {

            TextView name = (TextView) v.findViewById( R.id.textViewName );
            name.setText( p.getName());
            RatingBar ratingBar = v.findViewById(R.id.ratingBar8);
            ratingBar.setRating(p.getPoint());
            TextView feedback = (TextView) v.findViewById( R.id.textViewfeedback );
            feedback.setText(p.getFeedback());
            TextView createdOn = (TextView) v.findViewById( R.id.textViewcreatedOn );

            Date d = null;
            if (p.getCreatedOn().equals( "null" ))
            {
                d = new Date(0);
            }
            else {
                d = new Date(Long.parseLong(  p.getCreatedOn()));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            createdOn.setText( sdf.format( d ));



        }
        return v;
    }

}
