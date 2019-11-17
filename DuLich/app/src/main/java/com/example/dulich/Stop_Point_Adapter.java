package com.example.dulich;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class Stop_Point_Adapter extends ArrayAdapter<stopPoint> {
    ArrayList<stopPoint> noteList;
    public Stop_Point_Adapter( Context context, int resource, ArrayList<stopPoint>  objects) {
        super( context, resource, objects );
            noteList	=	objects;
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
       // v = inflater.inflate(R.layout.item_layout, null);

        stopPoint p =getItem( position );
        if (p!=null) {


        }
        return v;
    }

}
