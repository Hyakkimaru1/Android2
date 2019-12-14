package com.ygaps.travelapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ygaps.travelapp.R;
import com.ygaps.travelapp.stopPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class adapter_stopPoint extends ArrayAdapter<stopPoint> {
        ArrayList<stopPoint> list;

    public adapter_stopPoint(@NonNull Context context, int resource, int textViewResourceId, ArrayList<stopPoint> list) {
        super(context, resource, textViewResourceId);
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate( R.layout.layout_stop_point, null);

        stopPoint p = getItem( position );
        if (p!=null) {
            TextView fullNameSearch = (TextView) v.findViewById(R.id.textViewName);
            fullNameSearch.setText(p.getName());

            TextView address = (TextView) v.findViewById(R.id.textViewAddress);
            fullNameSearch.setText(p.getAddress());

            TextView min = (TextView) v.findViewById(R.id.textViewMin);
            fullNameSearch.setText(String.valueOf(p.getMinCost()));

            TextView max = (TextView) v.findViewById(R.id.textViewMax);
            fullNameSearch.setText(String.valueOf(p.getMaxCost()));

            TextView start = (TextView) v.findViewById(R.id.textViewTimeStart);
            Date d = null;

                d = new Date(p.getLeaveAt());

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            start.setText(sdf.format(d));

            TextView endDate = (TextView) v.findViewById(R.id.textViewTimeEnd);

                d = new Date(p.getLeaveAt());


            endDate.setText(sdf.format(d));
        }
        return v;
    }
}
