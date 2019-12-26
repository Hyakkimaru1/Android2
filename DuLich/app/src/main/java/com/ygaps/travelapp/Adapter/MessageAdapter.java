package com.ygaps.travelapp.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ygaps.travelapp.Message;
import com.ygaps.travelapp.R;

import java.util.ArrayList;


public class MessageAdapter extends ArrayAdapter<Message> {
    ArrayList<Message> messages;

    public MessageAdapter(Context context, int resource, ArrayList<Message> objects) {
        super(context, resource, objects);
        // TODO Auto-generated constructor stub
        messages	=	objects;
    }
    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return messages.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Message message = getItem( position );
        if (message.isBelongsToCurrentUser()) { //Nếu người là người đang sử dụng
            v = inflater.inflate( R.layout.my_message, null);
            TextView messageBody = (TextView) v.findViewById(R.id.message_body);
            messageBody.setText(message.getComment());
        }
        else {
            v = inflater.inflate( R.layout.friends_chat, null);
            ImageView avatar = (ImageView) v.findViewById(R.id.avatar);
            TextView name = (TextView) v.findViewById(R.id.name_friend );
            TextView messageBody = (TextView) v.findViewById(R.id.message_body);

            if (!message.getAvatar().equals(""))
                Picasso.get().load(message.getAvatar()).into(avatar);
            name.setText(message.getName());
            messageBody.setText(message.getComment());
        }
        return v;
    }

}
