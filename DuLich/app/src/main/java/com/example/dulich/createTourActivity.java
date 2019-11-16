package com.example.dulich;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
<<<<<<< HEAD
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.widget.ImageView;

public class createTourActivity extends AppCompatActivity {
    Button creatTour;

    EditText tourName;
    EditText startDay;
    EditText endDay;
    EditText adult;
    EditText children;
    EditText minC;
    EditText maxC;
    // ImageView img;
    EditText departure;
    EditText destinate;
    SharedPreferences preferences;
    String token;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_create_tour);
        token = preferences.getString( "token","" );
        creatTour = findViewById(R.id.CreateTour );
        tourName = findViewById(R.id.editTextTourname);
        startDay = findViewById(R.id.editTextStartday);
        endDay = findViewById(R.id.editTextEndday);
        adult = findViewById(R.id.editTextAdult);
        children = findViewById(R.id.editTextChildren);
        minC = findViewById(R.id.editTextMinC);
        maxC = findViewById(R.id.editTextMaxC);
        //img = findViewById(R.id.imageView);

        creatTour.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckData()){
                    long sStartDay=0;
                    long sEndDay=0;
                    SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
                    try {
                        Date dateS = sdf.parse( startDay.getText().toString());
                        sStartDay = (Long)dateS.getTime();
                    } catch (ParseException e){
                        e.printStackTrace();
                    }

                    try {
                        Date dateS = sdf.parse( endDay.getText().toString());
                        sEndDay = (Long)dateS.getTime();
                    } catch (ParseException e){
                        e.printStackTrace();
                    }

                    Call<ResponseBody> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .createTour(token,tourName.getText().toString(),sStartDay,sEndDay, Integer.parseInt(adult.getText().toString()),
                                    Integer.parseInt( children.getText().toString() ), Long.parseLong( minC.getText().toString() ),
                                    Long.parseLong( maxC.getText().toString() ) );

                    call.enqueue( new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code()==200) {
                                String bodyTourCreate = null;
                                try {
                                    bodyTourCreate = response.body().string();

                                    JSONObject tourData = new JSONObject(bodyTourCreate);
                                    // Log.i("JSON",tourData.getString("total"));
                                    Toast.makeText( createTourActivity.this, "Tạo tour thành công",Toast.LENGTH_SHORT ).show();
                                } catch (IOException e) {
                                    e.printStackTrace();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if (response.code()==400)
                            {
                                Toast.makeText( createTourActivity.this, "Tạo tour thất bại",Toast.LENGTH_SHORT ).show();
                            }
                            else {
                                Toast.makeText( createTourActivity.this, "Server error on creating tour",Toast.LENGTH_SHORT ).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    } );

                }

            }
        } );


    }

    private boolean CheckData()
    {
        if (tourName.getText().toString().isEmpty()||startDay.getText().toString().isEmpty()
                ||endDay.getText().toString().isEmpty() || adult.getText().toString().isEmpty()
                ||children.getText().toString().isEmpty()||minC.getText().toString().isEmpty()
                || maxC.getText().toString().isEmpty())
        {
            Toast.makeText( this, "Vui lòng không để trống thông tin",Toast.LENGTH_SHORT ).show();
            return false;
        }
        if (token.equals( "" ))
        {
            Toast.makeText( this, "Tài khoản bị lỗi",Toast.LENGTH_SHORT ).show();
        }
        //Check private or public
        return true;
    }


}
