package com.example.dulich;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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

public class tourDetail extends Fragment{
    ListView listView;
    ArrayList<aTour> noteList = new ArrayList<aTour>();
    MyAdapter myAdapter;
    SharedPreferences preferences;
    TextView tours;
    String token;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tour_detail,container,false);
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

        listView.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


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


    /*
    private String docNoiDung_Tu_URL(String theUrl){
        StringBuilder content = new StringBuilder();
        try    {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null){
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)    {
            e.printStackTrace();
        }
        return content.toString();
    }

     */
}
