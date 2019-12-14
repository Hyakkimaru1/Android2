package com.ygaps.travelapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class notification_Adapter extends ArrayAdapter<Tour_Invitation> {
    ArrayList<Tour_Invitation> noteList;
    SharedPreferences preferences;
    String token;
    public notification_Adapter(@NonNull Context context, int resource, @NonNull ArrayList<Tour_Invitation> objects) {
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
        preferences = this.getContext().getSharedPreferences("isLogin", Context.MODE_PRIVATE);

        token = preferences.getString( "token","" );
        v = inflater.inflate(R.layout.item_notification, null);
        final Tour_Invitation p =getItem( position );
        if (p!=null) {
            TextView textView = v.findViewById( R.id.textNotification );
            textView.setText( Html.fromHtml("<strong>" + p.getHostName() +" </strong>"  + getContext().getString( R.string.notification ) + "<strong> " + p.getNameTour() +"</strong>") );
            TextView textTimeInvite = v.findViewById( R.id.textTimeInvite );
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date d = new Date(p.getCreateOn());
            textTimeInvite.setText(sdf.format( d ));
            ImageView imageView = (ImageView)v.findViewById( R.id.avtTourInvite);
            if (!p.getAvtTour().equals(""))
                Picasso.get().load(p.getAvtTour()).into(imageView);
            Button confirm_invite = v.findViewById( R.id.confirm_invite );
            confirm_invite.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Call<ResponseBody> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .joiningTour(token, String.valueOf( p.getTourID()),1);
                    Log.e("Token",token);
                    Log.e("TourID",String.valueOf( p.getTourID()));
                    call.enqueue( new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                           if (response.code()==200){
                               noteList.remove( p );
                               notifyDataSetChanged();
                               String body = null;
                               try {
                                   body = response.body().string();
                                   Log.e("AAAAAKA",body);
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                          }
                           else {
                               Log.e("AAAAAKA",response.message().toString());
                           }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    } );
                }
            } );

            Button delete_invite = v.findViewById( R.id.delete_invite );
            delete_invite.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Call<ResponseBody> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .joiningTour(token, String.valueOf(p.getTourID()),0);

                    call.enqueue( new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code()==200){
                                noteList.remove( p );
                                notifyDataSetChanged();
                                String body = null;
                                try {
                                    body = response.body().string();
                                    Log.e("AAAAAKA",body);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                Log.e("AAAAAKA",response.message().toString());
                            }

                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    } );
                }
            } );
        }
        return v;
    }

}
