package com.ygaps.travelapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.Adapter.Stop_Point_Adapter;
import com.ygaps.travelapp.Adapter.rate_adapter;
import com.ygaps.travelapp.MyCustomDialog;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.RetrofitClient;
import com.ygaps.travelapp.aRate;
import com.ygaps.travelapp.stopPoint;
import com.ygaps.travelapp.updateSP;
import com.ygaps.travelapp.updateTour;
import com.ygaps.travelapp.updateTourNext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class tourDetail extends AppCompatActivity {
    ListView listView;
    ArrayList <stopPoint> list;
    ListView listViewRV;
    ArrayList <aRate> listRV;
    int[] ratetotal= {0,0,0,0,0};
    String token;
    String sStartDay;
    String sEndDay;
    String id;
    String review;
    int rating;


    Stop_Point_Adapter adapter;
    rate_adapter adapterRV;
    TextView tourname;
    TextView start;
    TextView end;
    TextView adult;
    TextView child;
    TextView min;
    TextView max;
    TextView totalRV;

    TextView ratingBar1;
    TextView ratingBar2;
    TextView ratingBar3;
    TextView ratingBar4;
    TextView ratingBar5;
    TextView textViewRate;
    TextView textViewtotalRV;


    RatingBar ratingBarRate;
    RatingBar ratingBarMain;

    EditText RV;

    String check;
    Button rate;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_tour_detail);
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
//        Log.i("DDDdđ",id);
        preferences = this.getBaseContext().getSharedPreferences("isLogin", Context.MODE_PRIVATE);


        readJson();

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("1111111111",String.valueOf(id));
                Intent intent = new Intent(getBaseContext(), updateSP.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.putExtra("return check", check);
                intent.putExtra("token", token);
                intent.putExtra("id", id);
                intent.putExtra( "position" ,position);

                startActivity(intent);

                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id2) {
                final int svID = Integer.parseInt(list.get(position).getServiceId());
                final Button sendRV;
                Context context;
                Dialog dialog= new Dialog(tourDetail.this);
                dialog.setContentView(R.layout.activity_rate_tour);
                totalRV = dialog.findViewById(R.id.textViewTotalRV);
                listViewRV = dialog.findViewById(R.id.listViewRV);

                ratingBar1 = dialog.findViewById(R.id.textView11);
                ratingBar2 = dialog.findViewById(R.id.textView22);
                ratingBar3 = dialog.findViewById(R.id.textView33);
                ratingBar4 = dialog.findViewById(R.id.textView44);
                ratingBar5 = dialog.findViewById(R.id.textView55);
                textViewtotalRV = dialog.findViewById(R.id.textViewTotalRV);

                textViewRate = dialog.findViewById(R.id.textViewRate);
                ratingBarMain = dialog.findViewById(R.id.ratingBarMain);
                RV = dialog.findViewById(R.id.editTextRV);



                sendRV = dialog.findViewById(R.id.buttonSendRV);
                sendRV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rating = Math.round(ratingBarMain.getRating());
                        review= RV.getText().toString();
                        sendReview(svID,review,rating);
                    }
                });

                ratingBarRate = dialog.findViewById(R.id.ratingBarRate);

                getReview(svID);

                dialog.show();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                dialog.getWindow().setLayout((9*width)/10,(15*height)/16);
               // DialogRate();
            }

        });



    }


    private void DialogRate(){
        Context context;
        Dialog dialog= new Dialog(this);
        dialog.setContentView(R.layout.activity_rate_tour);
        listViewRV = dialog.findViewById(R.id.listViewRV);
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


                        sStartDay = stopPointData.getString("startDate");
                        sEndDay = stopPointData.getString("endDate");
                        check = stopPointData.getString("isPrivate");
                        Date d = null;
                        if (sStartDay.equals( "null" ))
                        {
                            d = new Date(0);
                        }
                        else {
                            d = new Date(Long.parseLong(sStartDay));
                        }

                       // Log.e("EEEEEEE",String.valueOf(d));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                        start.setText( sdf.format( d ));
                        if (!sEndDay.equals( "null" )){
                            d = new Date(Long.parseLong(sEndDay));
                        }
                        else {
                            d = new Date( 0 );
                        }


                        end.setText( sdf.format( d ) );

                        adult.setText( stopPointData.getString("adults"));
                        child.setText( stopPointData.getString("childs"));
                        min.setText( stopPointData.getString("minCost"));
                        max.setText( stopPointData.getString("maxCost"));
                        JSONArray responseArray = stopPointData.getJSONArray("stopPoints");
                       // Log.e("BBBBB",String.valueOf(responseArray.length()));
                        if (responseArray.length() > 0) {

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject jb = responseArray.getJSONObject( i );
                                final Address address = getAddress(jb.getDouble("lat"),jb.getDouble("long"));
                                list.add( new stopPoint( jb.getString( "name" ),address.getAddressLine(0) , 0, jb.getDouble("lat"),
                                        jb.getDouble("long"), jb.getLong( "leaveAt" ), jb.getLong( "arrivalAt" ),
                                        jb.getLong( "minCost" ),  jb.getLong( "maxCost" ), jb.getInt( "serviceTypeId" ),String.valueOf(jb.getInt( "serviceId" ))) );


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

    private  void getReview(int serviceId)
    {
        Log.e("1111111111",String.valueOf(id));
        Log.e("1111111111",String.valueOf(serviceId));
        Log.e("22222222",String.valueOf(token));
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getlistReview(token, serviceId,1,100);

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    listRV = new ArrayList<aRate>( );
                    String bodyListTour = null;
                    try {
                        bodyListTour = response.body().string();

                        JSONObject stopPointData = new JSONObject(bodyListTour);
                        // Log.i("JSON",tourData.getString("total"));


                        JSONArray responseArray = stopPointData.getJSONArray("feedbackList");
                        Log.e("BBBBB",String.valueOf(responseArray.length()));
                        if (responseArray.length() > 0) {

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject jb = responseArray.getJSONObject( i );
                                listRV.add( new aRate( jb.getInt( "id" ),jb.getString( "name" ) , jb.getInt( "point" ),jb.getString( "feedback" ),
                                        jb.getString( "createdOn" )) );


                            }
                            Log.e("SSSSSSSSSSSS",String.valueOf(listRV.size()));
                            totalRV.setText("(Total: " + listRV.size()+") ");
                            if (!listRV.isEmpty()) {
                                int size = listRV.size();
                                for (int i = 0; i < size; i++) {
                                    switch (listRV.get(i).getPoint()) {
                                        case 5:
                                            ratetotal[0]++;
                                            break;
                                        case 1:
                                            ratetotal[1]++;
                                            break;
                                        case 2:
                                            ratetotal[2]++;
                                            break;
                                        case 3:
                                            ratetotal[3]++;
                                            break;
                                        case 4:
                                            ratetotal[4]++;
                                            break;
                                    }
                                }
                            }
                            ratingBar1.setText(String.valueOf(ratetotal[1]));
                            ratingBar2.setText(String.valueOf(ratetotal[2]));
                            ratingBar3.setText(String.valueOf(ratetotal[3]));
                            ratingBar4.setText(String.valueOf(ratetotal[4]));
                            ratingBar5.setText(String.valueOf(ratetotal[0]));
                            double totalR = ratetotal[0]+ratetotal[1]+ratetotal[2]+ratetotal[3]+ratetotal[4];
                           // textViewtotalRV.setText(new DecimalFormat("##").format(totalR));
                            double rateStar= (ratetotal[0]*5+ratetotal[4]*4+ratetotal[3]*3+ratetotal[2]*2+ratetotal[1])/totalR;
                            ratingBarRate.setRating(Float.parseFloat(new DecimalFormat("##.##").format(rateStar)));
                            textViewRate.setText(new DecimalFormat("##.#").format(rateStar));
                                adapterRV = new rate_adapter( getBaseContext(), R.layout.item_rate, listRV );
                                listViewRV.setAdapter(adapterRV) ;


                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else Log.e("RRRRRRRRrr",String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );


    }

    public void sendReview(int serviceId, String feedback1, int point){
        Log.e("A",String.valueOf(serviceId));
        Log.e("BC",feedback1);
        Log.e("C",String.valueOf(point));
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .sendFeedback(token,serviceId,feedback1,point);

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    String bodyTourCreate = null;
                    try {
                        bodyTourCreate = response.body().string();

                        JSONObject tourData = new JSONObject(bodyTourCreate);


                        // Toast.makeText( updateTourNext.this, "Cập nhật tour thành công",Toast.LENGTH_SHORT ).show();
                        Intent intent = new Intent(getBaseContext(), tourDetail.class);
                        intent.putExtra("token", token);
                        intent.putExtra("tourId", id);

                        startActivity(intent);
                        //Need to kill first activity

                                           /* Intent intent = new Intent(getBaseContext(), MapsActivity.class);

                                            intent.putExtra("idTour", message);
                                            startActivity(intent);*/
                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else Log.e("ABC",String.valueOf(response.code()));

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
