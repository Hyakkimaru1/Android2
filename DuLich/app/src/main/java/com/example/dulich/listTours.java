package com.example.dulich;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class listTours extends Fragment {
    ListView listView;
    ArrayList<aTour> noteList = new ArrayList<aTour>();
    MyAdapter myAdapter;
    SharedPreferences preferences;
    TextView tours;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_tours,container,false);
        listView =(ListView) view.findViewById( R.id.listTours);

        preferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        tours= view.findViewById( R.id.tours );
        readJson();
        return view;
    }

    void readJson(){
        String token = preferences.getString( "token","" );
        if (!token.equals(""))
        {
            Map<String, String> params = new HashMap<>();
            params.put("rowPerPage","10");
            params.put("pageNum","1");
            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getListTour(token,params);

            call.enqueue( new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code()==200) {
                        String bodyListTour = null;
                        try {
                            bodyListTour = response.body().string();
                         //   Log.i("JSON", bodyListTour.toString());
                            JSONObject tourData = new JSONObject(bodyListTour);
                            tours.setText( tourData.getString("total"));
                            JSONArray responseArray = tourData.getJSONArray("tours");
                            if (responseArray.length() > 0) {
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject jb = responseArray.getJSONObject( i );
                                    noteList.add( new aTour( jb.getInt( "id" ), jb.getInt( "status" ), jb.getString( "name" ), jb.getLong( "minCost" ),
                                            jb.getLong( "maxCost" ), jb.getLong( "startDate" ), jb.getLong( "endDate" ), jb.getInt( "adults" ),
                                            jb.getInt( "childs" ), jb.getBoolean( "isPrivate" ), jb.getString( "avatar" ) ) );
                                }
                                myAdapter = new MyAdapter( getContext(), R.layout.item_layout, noteList );
                                listView.setAdapter( myAdapter );
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
