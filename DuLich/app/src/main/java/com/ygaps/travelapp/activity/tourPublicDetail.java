package com.ygaps.travelapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.Adapter.adapter_searchFriend;
import com.ygaps.travelapp.Adapter.rate_adapter;
import com.ygaps.travelapp.MyCustomDialog;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.RetrofitClient;
import com.ygaps.travelapp.aRate;
import com.ygaps.travelapp.friends_user;
import com.ygaps.travelapp.stopPoint;
import com.ygaps.travelapp.updateTour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class tourPublicDetail extends AppCompatActivity {

    ArrayList <stopPoint> list;
    ListView listViewRV;
    ArrayList <aRate> listRV;
    ArrayList <aRate> noteList;
    ArrayList <friends_user> listFR;
    adapter_searchFriend adapterSearchFriend;

    String token;
    String sStartDay;
    String sEndDay;
    String id;

    Button button2;
    Button inviteFriends;
    Button cancel;
    Button buttonChat;
    Button buttonFollow;
    ListView listFriend;

    ListView list_review_SP;
    EditText editText3;
    rate_adapter adapterRV;
    rate_adapter adapter;
    TextView tourname;
    TextView start;
    TextView end;
    TextView adult;
    TextView child;
    TextView min;
    TextView max;
    int Host;
    int id_user;
    boolean isPrivate;

    TextView textViewListStopPoint;

    RatingBar ratingBar;
    String check;
    Button buttonRate;
    Button buttonFriend;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_tour_detail);
        tourPublicDetail.this.setTitle("Tour Detail");

        textViewListStopPoint = findViewById( R.id.textViewListStopPoint );
        textViewListStopPoint.setText( "Review" );
        tourname = findViewById(R.id.textViewTourName);
        start = findViewById(R.id.textViewTimeStart);
        end = findViewById(R.id.textViewTimeEnd);
        adult = findViewById(R.id.textViewAdult);
        child = findViewById(R.id.textViewChild);
        min = findViewById(R.id.textViewMin);
        max = findViewById(R.id.textViewMax);
        listViewRV = findViewById( R.id.ListViewSP );
        buttonFriend = findViewById( R.id.buttonFriend );
        buttonRate = findViewById(R.id.buttonRate);
        buttonChat = findViewById( R.id.buttonChatFriend );
        buttonFollow = findViewById( R.id.buttonFollowTour );


        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        id = intent.getStringExtra( "tourId") ;
        preferences = this.getBaseContext().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        id_user = preferences.getInt( "userID",-1 );
       // Log.i("DDDdđ",String.valueOf( id_user ));
        final Dialog dialogInvite = new Dialog( tourPublicDetail.this );
        dialogInvite.setTitle( "Thành viên" );
        dialogInvite.setCancelable( false );
        dialogInvite.setContentView(R.layout.dialog_list_friends_in_tour );
        inviteFriends = dialogInvite.findViewById( R.id.inviteFriends );
        cancel = dialogInvite.findViewById( R.id.cancel );
        listFriend = dialogInvite.findViewById( R.id.listFriend );

        readJson();

        if (Host != id_user)
        {
            buttonChat.setVisibility( View.INVISIBLE );
            buttonFollow.setVisibility( View.INVISIBLE );
        }
    buttonFollow.setOnClickListener( new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getBaseContext(), maps_follow_thetour.class);
        intent.putExtra("tourId", id);

        startActivity(intent);
    }
} );


        buttonRate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog1= new Dialog(tourPublicDetail.this);
                dialog1.setContentView(R.layout.send_rv_stoppoint);

                list_review_SP = dialog1.findViewById( R.id.list_review_SP );
                editText3 = dialog1.findViewById( R.id.editText3 );
                ratingBar = dialog1.findViewById( R.id.ratingBar );
                button2 = dialog1.findViewById( R.id.button2 );

                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .getReviewTour( token, Integer.parseInt(id),1,1000);
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code()==200) {
                            String bodyListTour = null;
                            try {
                                bodyListTour = response.body().string();
                                noteList = new ArrayList<>( );
                                JSONObject stopPointData = new JSONObject(bodyListTour);
                                // Log.i("JSON",tourData.getString("total"));


                                JSONArray responseArray = stopPointData.getJSONArray("reviewList");
                                //Log.e("feedbackList",String.valueOf(responseArray.length()));
                                if (responseArray.length() > 0) {

                                    for (int i = 0; i < responseArray.length(); i++) {
                                        JSONObject jb = responseArray.getJSONObject( i );
                                        noteList.add( new aRate( jb.getInt( "id" ),jb.getString( "name" ) , jb.getInt( "point" ),jb.getString( "review" ),
                                                jb.getString( "createdOn" )) );


                                    }

                                    adapter = new rate_adapter( tourPublicDetail.this, R.layout.item_rate, noteList );
                                    list_review_SP.setAdapter(adapter) ;


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

                button2.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendReview( Integer.parseInt(id) ,editText3.getText().toString(), Math.round(ratingBar.getRating()));
                        readJson1();
                        dialog1.cancel();


                    }
                } );
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                dialog1.getWindow().setLayout((9*width)/10,(9*height)/10);
                dialog1.show();
            }
        } );

        buttonFriend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                if (Host == id_user)
                {
                    inviteFriends.setVisibility( View.VISIBLE );
                }

                inviteFriends.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), searchFriend_Activity.class);
                        intent.putExtra("tourId", id);
                        intent.putExtra( "isPrivate",isPrivate );
                        startActivity(intent);
                    }
                } );
                dialogInvite.getWindow().setLayout((9*width)/10,(9*height)/10);
                dialogInvite.show();
                cancel.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogInvite.hide();
                    }
                } );
            }
        } );

    }


    public void sendReview(int serviceId, String feedback1, int point){

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .sendReviewTour(token,serviceId,feedback1,point);
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    String bodyTourCreate = null;
                    try {
                        bodyTourCreate = response.body().string();

                        JSONObject tourData = new JSONObject(bodyTourCreate);

                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText( tourPublicDetail.this,"Send error!!!",Toast.LENGTH_SHORT ).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );
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
                    list = new ArrayList<>( );
                    String bodyListTour = null;
                    try {
                        bodyListTour = response.body().string();

                        JSONObject stopPointData = new JSONObject(bodyListTour);
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
                        Host = stopPointData.getInt("hostId");
                       // Log.e("Host",String.valueOf( Host ));
                        isPrivate = stopPointData.getBoolean( "isPrivate" );
                        listFR = new ArrayList<>(  );
                        JSONArray array = stopPointData.getJSONArray( "members" );
                        for (int i =0;i<array.length();i++){
                            JSONObject object = array.getJSONObject( i );
                            listFR.add( new friends_user( object.getInt( "id" ),object.getString( "name" ),object.getString( "phone" ),
                                    object.getString( "avatar" ),object.getBoolean( "isHost" )) );
                        }
                        adapterSearchFriend = new adapter_searchFriend( tourPublicDetail.this,R.layout.layout_searchfriend,listFR );
                        listFriend.setAdapter( adapterSearchFriend );

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

        call = RetrofitClient
                .getInstance()
                .getApi()
                .getReviewTour(token, Integer.parseInt(id),1,200);

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    listRV = new ArrayList<>( );
                    String bodyListTour = null;
                    try {
                        bodyListTour = response.body().string();

                        JSONObject jsonObject = new JSONObject(bodyListTour);
                        JSONArray jsonArray = jsonObject.getJSONArray( "reviewList" );
                        if (jsonArray.length()>0){
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject( i );

                                listRV.add( new aRate( object.getInt( "id" ),object.getString( "name" ),object.getInt( "point" ),
                                        object.getString( "review" ),object.getString( "createdOn" )) );
                            }
                            adapterRV = new rate_adapter( tourPublicDetail.this,R.layout.item_rate, listRV );
                            listViewRV.setAdapter(adapterRV) ;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.e("ERROR:","GET REVIEW TOUR");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );
    }

    void readJson1(){

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getReviewTour(token, Integer.parseInt(id),1,200);

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    listRV.clear();
                    listRV = new ArrayList<>( );
                    String bodyListTour = null;
                    try {
                        bodyListTour = response.body().string();

                        JSONObject jsonObject = new JSONObject(bodyListTour);
                        JSONArray jsonArray = jsonObject.getJSONArray( "reviewList" );
                        if (jsonArray.length()>0){
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject( i );

                                listRV.add( new aRate( object.getInt( "id" ),object.getString( "name" ),object.getInt( "point" ),
                                        object.getString( "review" ),object.getString( "createdOn" )) );
                            }
                            if (adapter!= null){
                                adapterRV.clear();
                            }

                            adapterRV = new rate_adapter( tourPublicDetail.this,R.layout.item_rate, listRV );
                            listViewRV.setAdapter(adapterRV) ;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.e("ERROR:","GET REVIEW TOUR");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemEdit:
                //Do some thing to save


                if (id_user == Host) {
                    Intent intent = new Intent( getBaseContext(), updateTour.class );
                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    //intent.putExtra("return check", check);
                    intent.putExtra( "token", token );
                    intent.putExtra( "id", id );
                    intent.putExtra( "tourName", tourname.getText().toString() );
                    intent.putExtra( "sStartDay", sStartDay );
                    intent.putExtra( "sEndDay", sEndDay );
                    intent.putExtra( "check", check );
                    intent.putExtra( "adult", Integer.parseInt( adult.getText().toString() ) );
                    intent.putExtra( "children", Integer.parseInt( child.getText().toString() ) );
                    intent.putExtra( "minC", Long.parseLong( min.getText().toString() ) );
                    intent.putExtra( "maxC", Long.parseLong( max.getText().toString() ) );

                    startActivity( intent );
                }
                else {
                    Toast.makeText( tourPublicDetail.this,"Bạn không có quyền",Toast.LENGTH_SHORT ).show();
                }
                return true;


            case R.id.itemDelete:


                Toast.makeText( getBaseContext(),"Không được sử dụng",Toast.LENGTH_SHORT ).show();
                if (id_user==Host){
                    editor = preferences.edit();
                    editor.putInt( "id", Integer.parseInt(id));
                    editor.commit();

                    MyCustomDialog dialog = new MyCustomDialog();
                    dialog.show( getSupportFragmentManager(),"MyCustomDiaLog" );
                    dialog.setCancelable( false );
                }
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
