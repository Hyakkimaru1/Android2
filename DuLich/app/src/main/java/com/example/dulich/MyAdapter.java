package com.example.dulich;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MyAdapter extends ArrayAdapter<aTour> implements Filterable {
    ArrayList<aTour> noteList;
    ArrayList<aTour> noteListFull;
    public MyAdapter(Context context, int resource,	ArrayList<aTour> objects) {
        super(context, resource, objects);
        // TODO Auto-generated constructor stub
        noteList	=	objects;
        noteListFull = new ArrayList<>(noteList);
    }

    public void delete(ArrayList<aTour> objects)
    {
        noteList	=	objects;
        noteListFull.clear();
        noteListFull.addAll(noteList);
        notifyDataSetChanged();
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
        v = inflater.inflate(R.layout.item_layout, null);

        aTour p =getItem( position );
        if (p!=null) {
            TextView place = (TextView) v.findViewById( R.id.place );
            place.setText( p.getName());
            TextView calendar = (TextView) v.findViewById( R.id.calendar );
            Date d = null;
            if (p.getStartDate().equals( "null" ))
            {
                    d = new Date(0);
            }
            else {
                d = new Date(Long.parseLong(  p.getStartDate()));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            calendar.setText( sdf.format( d ));

            TextView endDate = (TextView) v.findViewById( R.id.endDate );
            if (p.getStartDate().equals( "null" ))
            {
                d = new Date(0);
            }
            else {
                d = new Date(Long.parseLong( p.getEndDate()));
            }

            endDate.setText( sdf.format( d ) );

            TextView priceMin = (TextView) v.findViewById( R.id.priceMin );
            priceMin.setText(p.getMinCost());
            TextView priceMax = (TextView) v.findViewById( R.id.priceMax );
            priceMax.setText(  p.getMaxCost());
            TextView group = (TextView) v.findViewById( R.id.group );
            if (p.getAdults().equals( "null" )&& p.getChilds().equals( "null" )){
                group.setText("0");
            }
            else if (p.getAdults().equals( "null" ) && !p.getChilds().equals( "null" )) {
                group.setText(String.valueOf( Integer.parseInt(p.getChilds())));

            }
            else if (!p.getAdults().equals( "nu;;" ) && p.getChilds().equals( "null" )){
                group.setText(String.valueOf( Integer.parseInt( p.getAdults())));
            }
            else {
                group.setText(String.valueOf( Integer.parseInt(p.getChilds())+Integer.parseInt(p.getAdults())));
            }


            ImageView imageView = (ImageView)v.findViewById( R.id.mainPic);
            if (!p.getAvatar().equals(""))
                Picasso.get().load(p.getAvatar()).into(imageView);


        }
        return v;
    }


    @NonNull
    @Override
    public Filter getFilter() {
        return noteFilter;
    }

    private Filter noteFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList <aTour> filterNotes = new ArrayList<>();
            if (charSequence==null || charSequence=="")
            {
                filterNotes.addAll(noteListFull);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (aTour x:noteListFull)
                {
                    if (x.getName().toLowerCase().contains( filterPattern )){
                        filterNotes.add(x);
                    }
                }

            }

            FilterResults results = new FilterResults();
            results.values = filterNotes;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            noteList.clear();
            if (filterResults.values==null)
            {
                noteList.addAll(noteListFull);
            }
            else
                noteList.addAll((ArrayList)filterResults.values);
            notifyDataSetChanged();
        }
    };
}
