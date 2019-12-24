package com.ygaps.travelapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.Adapter.Stop_Point_Adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fragment_explore extends Fragment {
    SearchView searchView;
    ListView listView;
    SharedPreferences preferences;
    String token;
    boolean flag_loading =false;
    ArrayList<stopPoint> noteList = new ArrayList<stopPoint>();
    Stop_Point_Adapter stop_point_adapter;
    int pageIndex = 1;
    String searchKey = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_explore,container,false);

        listView = view.findViewById( R.id.listExplorePoint );
        searchView = view.findViewById( R.id.searchStopPoint );
        preferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);

        token = preferences.getString( "token","" );
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchKey = s;
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .getDestination( token,s, pageIndex,20);
                pageIndex++;
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code()==200){
                            try {
                                String body = response.body().string();
                                JSONObject jsonObject = new JSONObject( body );

                                JSONArray jsonArray = jsonObject.getJSONArray( "stopPoints" );

                                if (jsonArray.length()>0){
                                    for (int i = 0; i<jsonArray.length();i++){
                                        JSONObject object = jsonArray.getJSONObject( i );

                                        noteList.add( new stopPoint( object.getString( "name" ),object.getString( "address" ),object.getInt( "provinceId" ),
                                                object.getDouble( "lat") ,object.getDouble( "long" ),object.getLong( "minCost" ),
                                                object.getLong( "maxCost" ),object.getString( "avatar" ),object.getInt( "serviceTypeId" )) );
                                    }
                                    if (!noteList.isEmpty())
                                    {
                                        stop_point_adapter = new Stop_Point_Adapter( getContext(),R.layout.item_stoppoint_layout,noteList );
                                        listView.setAdapter( stop_point_adapter );
                                    }

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText( getContext(),"Có lỗi, vui lòng thử lại sau.",Toast.LENGTH_LONG ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText( getContext(),"Có lỗi, vui lòng thử lại sau.",Toast.LENGTH_LONG ).show();
                    }
                } );
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        } );
        listView.setOnScrollListener( new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(flag_loading == false)
                    {
                        flag_loading = true;
                        additems();
                    }
                }
            }
        } );

        return view;
    }

    private void additems() {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getDestination( token, searchKey, pageIndex, 20 );

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    pageIndex++;
                    flag_loading = false;
                    try {
                        String body = response.body().string();
                        JSONObject jsonObject = new JSONObject( body );

                        JSONArray jsonArray = jsonObject.getJSONArray( "stopPoints" );

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject( i );

                                noteList.add( new stopPoint( object.getString( "name" ), object.getString( "address" ), object.getInt( "provinceId" ),
                                        object.getDouble( "lat" ), object.getDouble( "long" ), object.getLong( "minCost" ),
                                        object.getLong( "maxCost" ), object.getString( "avatar" ), object.getInt( "serviceTypeId" ) ) );
                            }
                            stop_point_adapter.notifyDataSetChanged();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText( getContext(), "Có lỗi, vui lòng thử lại sau.", Toast.LENGTH_LONG ).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );
    }
}
