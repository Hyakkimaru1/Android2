package com.example.dulich;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Stop_Point_Adapter extends ArrayAdapter<stopPoint> {
    ArrayList<stopPoint> noteList;

    public Stop_Point_Adapter(@NonNull Context context, int resource, @NonNull ArrayList<stopPoint> objects) {
        super( context, resource, objects );
        noteList = objects;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return noteList.size();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.item_stoppoint_layout, null);
        stopPoint p =getItem( position );
        if (p!=null) {

            TextView place = (TextView) v.findViewById( R.id.nameSP );
            place.setText( p.getName());
            TextView calendarSP = (TextView) v.findViewById( R.id.calendarSP );

            Date d = null;
            d = new Date(p.getArrivalAt());

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            calendarSP.setText( sdf.format( d ));

            TextView endDate = (TextView) v.findViewById( R.id.endDateSP );
            d = new Date(p.getLeaveAt());

            endDate.setText( sdf.format( d ) );

            TextView priceMin = (TextView) v.findViewById( R.id.priceMinSP );
            priceMin.setText( String.valueOf( p.getMinCost()));
            TextView priceMax = (TextView) v.findViewById( R.id.priceMaxSP);
            priceMax.setText( String.valueOf( p.getMaxCost()));
            TextView group = (TextView) v.findViewById( R.id.locationSP );
            group.setText(p.getAddress());
        }
        return v;
    }

}
