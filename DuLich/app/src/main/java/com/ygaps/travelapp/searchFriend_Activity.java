package com.ygaps.travelapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.Adapter.adapter_searchFriend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class searchFriend_Activity extends AppCompatActivity {
    SearchView searchView;
    ListView listView;
    ArrayList<friends_user> noteList;
    adapter_searchFriend myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tour_detail );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        listView = findViewById( R.id.list_SearchFriend );
        searchView = findViewById( R.id.search_Friend );
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .searchFriend( s,1,50);
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code()==200)
                        {
                            String Body = null;
                            noteList = new ArrayList<friends_user>( );
                            int total;
                            try {
                                Body = response.body().string();
                                Log.i( "AAAAAAAAAAA",Body );
                                JSONObject jsonObject = new JSONObject( Body );
                                total = jsonObject.getInt( "total" );

                                JSONArray jsonArray = jsonObject.getJSONArray( "users" );
                                if (jsonArray.length()>0){
                                    for (int i =0 ;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject( i );
                                        noteList.add( new friends_user( jsonObject1.getInt( "id" ),jsonObject1.getString( "fullName" ),
                                                jsonObject1.getString( "email" ),jsonObject1.getString( "phone" ),jsonObject1.getString( "avatar" )));

                                    }
                                    if (!noteList.isEmpty())
                                    {
                                        myAdapter = new adapter_searchFriend( searchFriend_Activity.this,R.layout.layout_searchfriend, noteList );
                                        listView.setAdapter( myAdapter );
                                    }

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                } );

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        } );
    }
}
