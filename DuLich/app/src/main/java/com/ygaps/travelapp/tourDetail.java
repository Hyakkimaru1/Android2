package com.ygaps.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.ygaps.travelapp.Adapter.MyAdapter;

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

public class tourDetail extends AppCompatActivity {
    ArrayList <stopPoint> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);
        tourDetail.this.setTitle("Tour Detail");


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.tour_detail,menu);
        MenuItem item = menu.findItem(R.id.itemDelete);
        MenuItem item2 = menu.findItem(R.id.itemEdit);

        return super.onCreateOptionsMenu(menu);
    }

    void readJson(int tourId){
        String auth = "";

            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                   .getTourInfo(auth, tourId);

            call.enqueue( new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code()==200) {
                        list = new ArrayList<stopPoint>( );
                        String bodyListTour = null;
                        try {
                            bodyListTour = response.body().string();

                            JSONObject tourData = new JSONObject(bodyListTour);
                            // Log.i("JSON",tourData.getString("total"));

                            JSONArray responseArray = tourData.getJSONArray("tours");
                            if (responseArray.length() > 0) {
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject jb = responseArray.getJSONObject( i );


                                }
                                if (!list.isEmpty())
                                {

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
