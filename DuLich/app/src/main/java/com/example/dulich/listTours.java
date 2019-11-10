package com.example.dulich;

import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class listTours extends Fragment {
    ListView listView;
    ArrayList<aTour> noteList = new ArrayList<aTour>();
    MyAdapter myAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_tours,container,false);
        listView =(ListView) view.findViewById( R.id.listTours);
        noteList.add(new aTour( 1,0,"HCM - Nha Trang",10000000000000L,0,1552401906062L,1552401906062L,0,0,false,null ));
        myAdapter = new MyAdapter( getContext(),R.layout.item_layout,noteList );
        listView.setAdapter(myAdapter);
        getActivity().runOnUiThread( new Runnable() {
            @Override
            public void run() {
                new readJson().execute( "http://35.197.153.192:3000/user/login/" );
            }
        } );

        return view;
    }

    class readJson extends AsyncTask<String, Integer,String>{

        @Override
        protected String doInBackground(String... strings) {
            return docNoiDung_Tu_URL(strings[0]);

        }

        @Override
        protected void onPostExecute(String s) {

            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                if (jsonArray.length()>0)
                {
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jb = jsonArray.getJSONObject(i);
                        noteList.add( new aTour( jb.getInt( "id" ),jb.getInt("status"),jb.getString("name"),jb.getLong( "minCost" ) ,
                                jb.getLong("maxCost"),jb.getLong("startDate"),jb.getLong("endDate"),jb.getInt( "adults"),
                                jb.getInt("childs"),jb.getBoolean( "isPrivate" ),jb.getString( "avatar")));
                    }
                    myAdapter = new MyAdapter( getContext(),R.layout.item_layout,noteList );
                    listView.setAdapter(myAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
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
}
