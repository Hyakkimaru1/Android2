package com.example.dulich;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    ImageButton start;
    ImageButton end;
    SharedPreferences preferences;
    String token;
    EditText departure;
    EditText destinate;
    CheckBox check;
    SimpleDateFormat simpleDateFormat;

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
        preferences = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        token = preferences.getString( "token","" );
        creatTour = findViewById(R.id.CreateTour );
        tourName = findViewById(R.id.editTextTourname);
        startDay = findViewById(R.id.editTextStartday);
        endDay = findViewById(R.id.editTextEndday);
        adult = findViewById(R.id.editTextAdult);
        children = findViewById(R.id.editTextChildren);
        minC = findViewById(R.id.editTextMinC);
        maxC = findViewById(R.id.editTextMaxC);
        start = findViewById(R.id.imageButtonStartTime);
        end = findViewById(R.id.imageButtonEndTime);
        check = findViewById(R.id.checkBox);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start();
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                End();
            }
        });


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

                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                    intent.putExtra("token", token);
                    intent.putExtra( "tourName" ,tourName.getText().toString());
                    intent.putExtra( "sStartDay",sStartDay );
                    intent.putExtra( "sEndDay",sEndDay );
                    intent.putExtra( "check",check.isChecked() );
                    intent.putExtra( "adult",Integer.parseInt(adult.getText().toString()) );
                    intent.putExtra( "children",Integer.parseInt( children.getText().toString() ) );
                    intent.putExtra( "minC",Long.parseLong( minC.getText().toString() ) );
                    intent.putExtra( "maxC",Long.parseLong( maxC.getText().toString() ) );
                    startActivity(intent);
                }

            }
        } );


    }
    private void Start(){
        final Calendar calendar = Calendar.getInstance();
        int ngay = calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                startDay.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, nam,thang,ngay);
        datePickerDialog.show();
    }

    private void End(){
        final Calendar calendar = Calendar.getInstance();
        int ngay = calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                endDay.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, nam,thang,ngay);
        datePickerDialog.show();
    }

    private boolean CheckData()
    {
        if (tourName.getText().toString().isEmpty()||startDay.getText().toString().isEmpty()
                ||endDay.getText().toString().isEmpty() || adult.getText().toString().isEmpty()
                ||children.getText().toString().isEmpty()||minC.getText().toString().isEmpty()
                || maxC.getText().toString().isEmpty() )
        {
            Toast.makeText( this, "Vui lòng không để trống thông tin",Toast.LENGTH_SHORT ).show();
            return false;
        }
        if (Integer.parseInt(adult.getText().toString()) < 0 ||
            Integer.parseInt(children.getText().toString()) < 0 || Integer.parseInt(maxC.getText().toString()) < 0
            || Integer.parseInt(minC.getText().toString()) < 0)
        {
            Toast.makeText( this, "Vui lòng không nhập số âm",Toast.LENGTH_SHORT ).show();
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
