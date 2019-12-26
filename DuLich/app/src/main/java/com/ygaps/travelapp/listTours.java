package com.ygaps.travelapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ygaps.travelapp.Adapter.MyAdapter;
import com.ygaps.travelapp.activity.createTourActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class listTours extends Fragment {
    ListView listView;
    ArrayList<aTour> noteList;
    MyAdapter myAdapter;
    SharedPreferences preferences;
    TextView tours;
    String token;
    FloatingActionButton buttonAddTour;
    SearchView search_tour_in_main;
    int pageNum = 20;
    boolean flag_loading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_tours,container,false);
        listView =(ListView) view.findViewById( R.id.listTours);
        buttonAddTour = view.findViewById(R.id.button_input);
        preferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        search_tour_in_main = view.findViewById( R.id.search_tour_in_main );
        token = preferences.getString( "token","" );
        buttonAddTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (token.equals( "" ))
                {
                    Toast.makeText( getContext(),"Vui lòng đăng nhập",Toast.LENGTH_SHORT ).show();
                    return;
                }
                //
                Intent intent = new Intent(getActivity(), createTourActivity.class);
                //Intent intent = new Intent(getActivity(), chat_tour.class);
                startActivity(intent);
            }
        });

        tours = view.findViewById( R.id.tours );
        readJson();

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

        search_tour_in_main.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals( "" )||s.isEmpty()){
                    flag_loading = false;
                }
                else {
                    myAdapter.getFilter().filter( s );
                    flag_loading = true;
                }

                return false;
            }
        } );
        return view;

    }



    void readJson(){
        if (!token.equals(""))
        {
            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getListTour(token,20,pageNum);

            call.enqueue( new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code()==200) {
                        pageNum++;
                        noteList = new ArrayList<aTour>( );
                        String bodyListTour = null;
                        try {
                            bodyListTour = response.body().string();

                            JSONObject tourData = new JSONObject(bodyListTour);
                           // Log.i("JSON",tourData.getString("total"));
                            tours.setText( tourData.getString("total"));
                            JSONArray responseArray = tourData.getJSONArray("tours");
                            if (responseArray.length() > 0) {
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject jb = responseArray.getJSONObject( i );
                                    noteList.add( new aTour( jb.getInt( "id" ), jb.getInt( "status" ), jb.getString( "name" ), jb.getString( "minCost" ),
                                            jb.getString( "maxCost" ), jb.getString( "startDate" ), jb.getString( "endDate" ), jb.getString( "adults" ),
                                            jb.getString( "childs" ),  jb.getString( "avatar" ) ) );

                                }
                                if (!noteList.isEmpty())
                                {
                                    myAdapter = new MyAdapter( getContext(), R.layout.item_layout, noteList );
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



        }

    }
    private void additems() {

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getListTour(token,20,pageNum);

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    pageNum++;
                    flag_loading = false;
                    String bodyListTour = null;
                    try {
                        bodyListTour = response.body().string();

                        JSONObject tourData = new JSONObject(bodyListTour);
                        // Log.e("JSON",bodyListTour);
                        tours.setText( tourData.getString("total"));
                        JSONArray responseArray = tourData.getJSONArray("tours");
                        if (responseArray.length() > 0) {
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject jb = responseArray.getJSONObject( i );
                                noteList.add( new aTour( jb.getInt( "id" ), jb.getInt( "status" ), jb.getString( "name" ), jb.getString( "minCost" ),
                                        jb.getString( "maxCost" ), jb.getString( "startDate" ), jb.getString( "endDate" ), jb.getString( "adults" ),
                                        jb.getString( "childs" ),  jb.getString( "avatar" ) ) );

                            }
                            myAdapter.notifyDataSetChanged();
                            myAdapter.updateNoteListFull();
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


    }
}
