package com.ygaps.travelapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class notifications extends Fragment {

    ListView listView;
    ArrayList<Tour_Invitation> noteList;
    notification_Adapter notificationAdapter;
    SharedPreferences preferences;
    String token;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notifications,container,false);

        listView =(ListView) view.findViewById( R.id.list_notification);
        preferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);

        token = preferences.getString( "token","" );
        if (!token.equals( "" )){
            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getTourInvitation(token,1,50);
            call.enqueue( new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code()==200){
                        noteList = new ArrayList<>(  );
                        String body = null;
                        try {

                            body = response.body().string();

                            JSONObject tourData = new JSONObject(body);

                            int total = tourData.getInt("total");

                            JSONArray jsonArray = tourData.getJSONArray( "tours" );
                            if (total>0){
                                for (int i = 0; i<total;i++){
                                    JSONObject object = jsonArray.getJSONObject( i );
                                    noteList.add(new Tour_Invitation( object.getInt( "id" ),object.getInt( "hostId" ),object.getString( "hostName" ) ,
                                            object.getString( "hostPhone" ),object.getString( "hostEmail" ),object.getString( "hostAvatar" ),
                                            object.getInt( "status" ),object.getString("name"),object.getString( "minCost" ),object.getString( "maxCost" ),
                                            object.getString( "startDate" ),object.getString( "endDate" ),object.getInt( "adults" ),object.getInt( "childs" ),
                                            object.getString( "avatar" ),object.getLong( "createdOn" ) ) );

                                }
                                notificationAdapter = new notification_Adapter( getContext(), R.layout.item_notification, noteList );
                                listView.setAdapter( notificationAdapter );

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else if (response.code()==500)
                    {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            } );
        }


        return view;
    }
}
