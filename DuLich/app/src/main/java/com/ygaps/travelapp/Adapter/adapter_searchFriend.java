package com.ygaps.travelapp.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.friends_user;

import java.util.ArrayList;


public class adapter_searchFriend extends ArrayAdapter<friends_user> implements Filterable {
    ArrayList<friends_user> noteList;
    ArrayList<friends_user> noteListFull;
    public adapter_searchFriend(Context context, int resource, ArrayList<friends_user> objects) {
        super(context, resource, objects);
        // TODO Auto-generated constructor stub
        noteList	=	objects;
        noteListFull = new ArrayList<>(noteList);
    }

    public void delete(ArrayList<friends_user> objects)
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
        v = inflater.inflate( R.layout.layout_searchfriend, null);

        friends_user p = getItem( position );
        if (p!=null) {
            TextView fullNameSearch = (TextView) v.findViewById( R.id.fullNameSearch );
            fullNameSearch.setText( p.getFullName());


            ImageView imgSearch = (ImageView)v.findViewById( R.id.imgSearch);
            if (!p.getAvatar().equals("null"))
                Picasso.get().load(p.getAvatar()).into(imgSearch);


        }
        return v;
    }

}
