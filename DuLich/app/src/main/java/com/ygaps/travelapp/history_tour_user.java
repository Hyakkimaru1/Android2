package com.ygaps.travelapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.Adapter.MyAdapter;
import com.ygaps.travelapp.activity.tourDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class history_tour_user extends Fragment{
    ListView listView;
    ArrayList<aTour> noteList = new ArrayList<aTour>();
    ArrayList<aTour> noteListFull = new ArrayList<aTour>();
    MyAdapter myAdapter;
    SharedPreferences preferences;
    TextView tours;
    String token;

    SharedPreferences.Editor editor;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history_tour_user,container,false);
        listView =(ListView) view.findViewById( R.id.listHistoryTour);
        preferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        token = preferences.getString( "token","" );

        tours= view.findViewById( R.id.historyTours );
        readJson();

        SearchView searchView = view.findViewById( R.id.searchHistory );
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                myAdapter.getFilter().filter(s);
                return false;
            }
        } );



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), tourDetail.class);
                intent.putExtra("token", token);
                intent.putExtra("tourId", String.valueOf(noteList.get(position).getId()));
                intent.putExtra("status", noteList.get(position).getStatus());
                intent.putExtra("isHost", noteList.get(position).getisHost());

                startActivity(intent);

            }

        });



        listView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText( getContext(),String.valueOf(noteList.get(i).getId()),Toast.LENGTH_SHORT ).show();
                editor = preferences.edit();
                editor.putInt( "id", noteList.get(i).getId());
                editor.commit();
                MyCustomDialog dialog = new MyCustomDialog();
                dialog.show( getFragmentManager(),"MyCustomDiaLog" );
                dialog.setCancelable( false );

                return false;
            }
        } );

        return view;

    }

    void readJson(){
        if (!token.equals(""))
        {
            Map<String, String> params = new HashMap<>();
            params.put("pageIndex","1");
            params.put("pageSize","8000");
            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getHistoryTourUser(token,params);

            call.enqueue( new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code()==200) {
                        String bodyListTour = null;
                        try {
                            bodyListTour = response.body().string();

                            JSONObject tourData = new JSONObject(bodyListTour);
                            // Log.i("JSON",tourData.getString("total"));
                            int total = tourData.getInt("total");
                            JSONArray responseArray = tourData.getJSONArray("tours");
                            if (responseArray.length() > 0) {
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject jb = responseArray.getJSONObject( i );
                                        noteListFull.add( new aTour( jb.getInt( "id" ), jb.getInt( "status" ), jb.getString( "name" ), jb.getString( "minCost" ),
                                                jb.getString( "maxCost" ), jb.getString( "startDate" ), jb.getString( "endDate" ), jb.getString( "adults" ),
                                                jb.getString( "childs" ),  jb.getString( "avatar" ), jb.getBoolean( "isHost" ) ) );
                                }
                                if (!noteListFull.isEmpty())
                                {
                                    for (int i = 0;i<noteListFull.size();i++)	{
                                        if (noteListFull.get( i ).getStatus() != -1){
                                            noteList.add( noteListFull.get( i ));
                                        }
                                        else {
                                            total--;
                                        }
                                    }
                                    if (!noteList.isEmpty()){
                                        tours.setText( String.valueOf( total ));
                                        myAdapter = new MyAdapter( getContext(), R.layout.item_layout, noteList );
                                        listView.setAdapter( myAdapter );
                                    }
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
}
