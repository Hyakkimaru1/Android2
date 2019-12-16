package com.ygaps.travelapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ygaps.travelapp.Adapter.MyAdapter;
import com.ygaps.travelapp.activity.chat_tour;

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

public class listTours extends Fragment {
    ListView listView;
    ArrayList<aTour> noteList;
    MyAdapter myAdapter;
    SharedPreferences preferences;
    TextView tours;
    String token;
    FloatingActionButton buttonAddTour;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_tours,container,false);
        listView =(ListView) view.findViewById( R.id.listTours);
        buttonAddTour = view.findViewById(R.id.button_input);
        preferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);

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
                //Intent intent = new Intent(getActivity(), createTourActivity.class);
                Intent intent = new Intent(getActivity(), chat_tour.class);
                startActivity(intent);
            }
        });




        tours= view.findViewById( R.id.tours );
        readJson();
        return view;




    }

    void readJson(){
        if (!token.equals(""))
        {
            Map<String, String> params = new HashMap<>();
            params.put("rowPerPage","150");
            params.put("pageNum","1");
            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getListTour(token,params);

            call.enqueue( new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code()==200) {
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
