package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ygaps.travelapp.Adapter.MyAdapter;
import com.ygaps.travelapp.Adapter.Stop_Point_Adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class tourDetail extends AppCompatActivity {
    ListView listView;
    ArrayList <stopPoint> list;
    String token;
    String sStartDay;
    String sEndDay;
    String id;
    Stop_Point_Adapter adapter;
    TextView tourname;
    TextView start;
    TextView end;
    TextView adult;
    TextView child;
    TextView min;
    TextView max;
    String check;
    Button rate;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);
        tourDetail.this.setTitle("Tour Detail");

        listView = findViewById(R.id.ListViewSP);
        tourname = findViewById(R.id.textViewTourName);
        start = findViewById(R.id.textViewTimeStart);
        end = findViewById(R.id.textViewTimeEnd);
        adult = findViewById(R.id.textViewAdult);
        child = findViewById(R.id.textViewChild);
        min = findViewById(R.id.textViewMin);
        max = findViewById(R.id.textViewMax);

        rate = findViewById(R.id.buttonRate);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        id = intent.getStringExtra( "tourId") ;

        preferences = this.getBaseContext().getSharedPreferences("isLogin", Context.MODE_PRIVATE);

        Log.e("dddddd",String.valueOf(id));

        Log.e("TTTTTTTTTTT",token);
        readJson();

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogRate();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), updateTour.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.putExtra("return check", check);
                intent.putExtra("token", token);
                intent.putExtra("id", id);
                intent.putExtra( "tourName" ,tourname.getText().toString());
                intent.putExtra( "sStartDay",sStartDay );
                intent.putExtra( "sEndDay",sEndDay );
                intent.putExtra( "check",check);
                intent.putExtra( "adult",Integer.parseInt(adult.getText().toString()) );
                intent.putExtra( "children",Integer.parseInt( child.getText().toString() ) );
                intent.putExtra( "minC",Long.parseLong( min.getText().toString() ) );
                intent.putExtra( "maxC",Long.parseLong( max.getText().toString() ) );

                startActivity(intent);
            }
        });



    }


    private void DialogRate(){
        Context context;
        Dialog dialog= new Dialog(this);
        dialog.setContentView(R.layout.activity_rate_tour);
        dialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        dialog.getWindow().setLayout((9*width)/10,(9*height)/10);
    }




    void readJson(){


        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getTourInfo(token, Integer.parseInt(id));

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    list = new ArrayList<stopPoint>( );
                    String bodyListTour = null;
                    try {
                        bodyListTour = response.body().string();

                        JSONObject stopPointData = new JSONObject(bodyListTour);
                        // Log.i("JSON",tourData.getString("total"));
                        tourname.setText( stopPointData.getString("name"));

                        Date d = null;
                        sStartDay = stopPointData.getString("startDate");
                        sEndDay = stopPointData.getString("endDate");
                        check = stopPointData.getString("isPrivate");
                        if (sStartDay.equals( "null" ))
                        {
                            d = new Date(0);
                        }
                        else {
                            d = new Date(Long.parseLong(sStartDay));
                        }

                        Log.e("EEEEEEE",String.valueOf(d));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                        start.setText( sdf.format( d ));
                        d = new Date(Long.parseLong(sEndDay));

                        end.setText( sdf.format( d ) );

                        adult.setText( stopPointData.getString("adults"));
                        child.setText( stopPointData.getString("childs"));
                        min.setText( stopPointData.getString("minCost"));
                        max.setText( stopPointData.getString("maxCost"));
                        JSONArray responseArray = stopPointData.getJSONArray("stopPoints");
                        Log.e("BBBBB",String.valueOf(responseArray.length()));
                        if (responseArray.length() > 0) {

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject jb = responseArray.getJSONObject( i );
                                final Address address = getAddress(jb.getDouble("lat"),jb.getDouble("long"));
                                list.add( new stopPoint( jb.getString( "name" ),address.getAddressLine(0) , 0, jb.getDouble("lat"),
                                        jb.getDouble("long"), jb.getLong( "leaveAt" ), jb.getLong( "arrivalAt" ), jb.getInt( "serviceTypeId" ),
                                        jb.getLong( "minCost" ),  jb.getLong( "maxCost" ) ) );


                            }
                            if (!list.isEmpty())
                            {

                                adapter = new Stop_Point_Adapter( getBaseContext(), R.layout.item_stoppoint_layout, list );
                                listView.setAdapter(adapter) ;
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
    private Address getAddress(double lat, double lng) {


        Address address = null;
        Geocoder geocoder = new Geocoder( tourDetail.this );
        List<Address> list = new ArrayList<>(  );
        try
        {
            list = geocoder.getFromLocation( lat,lng,2 );

        } catch (IOException e){
            Log.e( "TAG","geoLocate: IOException "+e.getMessage() );
        }

        if (list.size()>0){
            address = list.get( 0 );
        }
        return address;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemEdit:
                //Do some thing to save



                Intent intent = new Intent(getBaseContext(), updateTour.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.putExtra("return check", check);
                intent.putExtra("token", token);
                intent.putExtra("id", id);
                intent.putExtra( "tourName" ,tourname.getText().toString());
                intent.putExtra( "sStartDay",sStartDay );
                intent.putExtra( "sEndDay",sEndDay );
                intent.putExtra( "check",check);
                intent.putExtra( "adult",Integer.parseInt(adult.getText().toString()) );
                intent.putExtra( "children",Integer.parseInt( child.getText().toString() ) );
                intent.putExtra( "minC",Long.parseLong( min.getText().toString() ) );
                intent.putExtra( "maxC",Long.parseLong( max.getText().toString() ) );

                startActivity(intent);

                return true;


            case R.id.itemDelete:



                Toast.makeText( getBaseContext(),id,Toast.LENGTH_SHORT ).show();
                editor = preferences.edit();
                editor.putInt( "id", Integer.parseInt(id));
                editor.commit();

                MyCustomDialog dialog = new MyCustomDialog();
                dialog.show( getSupportFragmentManager(),"MyCustomDiaLog" );
                dialog.setCancelable( false );

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.tour_detail,menu);
        MenuItem item = menu.findItem(R.id.itemDelete);
        MenuItem item2 = menu.findItem(R.id.itemEdit);

        return super.onCreateOptionsMenu(menu);
    }

}
