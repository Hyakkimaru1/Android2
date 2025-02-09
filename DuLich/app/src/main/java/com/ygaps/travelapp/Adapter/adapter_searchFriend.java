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
    public adapter_searchFriend(Context context, int resource, ArrayList<friends_user> objects) {
        super(context, resource, objects);
        // TODO Auto-generated constructor stub
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
        v = inflater.inflate( R.layout.layout_searchfriend, null);

        friends_user p = getItem( position );
        if (p!=null) {
            TextView fullNameSearch = (TextView) v.findViewById( R.id.fullNameSearch );
            fullNameSearch.setText( p.getFullName());
            if (p.isHost()){
                TextView isHost = v.findViewById( R.id.isHost );
                isHost.setText( "Host" );
            }



            if (!p.getAvatar().equals("null") && !p.getAvatar().isEmpty()){
                ImageView imgSearch = (ImageView)v.findViewById( R.id.imgSearch);
                Picasso.get().load(p.getAvatar()).into(imgSearch);
            }


        }
        return v;
    }

}
