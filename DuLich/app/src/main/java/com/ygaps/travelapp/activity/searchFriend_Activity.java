package com.ygaps.travelapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.Adapter.adapter_searchFriend;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.RetrofitClient;
import com.ygaps.travelapp.friends_user;

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
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_share_friend);

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
                            //    Log.i( "AAAAAAAAAAA",Body );
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

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, int i, long l) {
                SharedPreferences preferences = getSharedPreferences( "isLogin", Context.MODE_PRIVATE );
                final String Authorization = preferences.getString( "token","" );
                final String tourID =  preferences.getString( "tourID","" );
                final AlertDialog.Builder builder = new AlertDialog.Builder( searchFriend_Activity.this );
                builder.setTitle( "Xác nhận" );
                builder.setMessage( "Bạn có muốn mời người này vào tour ?" );
                builder.setPositiveButton( "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Call<ResponseBody> call = RetrofitClient
                                .getInstance()
                                .getApi()
                                .invite_friend( Authorization,tourID, String.valueOf( noteList.get( i ).getId() ) ,false );
                        call.enqueue( new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.code()==200){
                                    Toast.makeText( searchFriend_Activity.this,"Mời bạn thành công",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText( searchFriend_Activity.this,"Mời bạn thất bại",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        } );
                    }
                } );
                builder.setNegativeButton( "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                } );
                builder.show();
            }
        } );
    }
}
