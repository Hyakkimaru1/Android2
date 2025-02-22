package com.ygaps.travelapp;
import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

    public class updateTour extends AppCompatActivity {
        Button creatTour;

        int adults;




        EditText tourName;
        EditText startDay;
        EditText endDay;
        EditText adult;
        EditText children;
        EditText minC;
        EditText maxC;
        ImageView img;
        ImageButton start;
        ImageButton end;
        Button chooseImg;
        SharedPreferences preferences;
        String token;
        String id;
        EditText departure;
        EditText destinate;
        CheckBox check;

        // SimpleDateFormat simpleDateFormat;
        public static Activity fa;
        int Request_Code_Img = 5;
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


            Intent intent = getIntent();
            token = intent.getStringExtra("token");
            id = intent.getStringExtra("id");
            adults = intent.getIntExtra( "adult",0);

            tourName.setText(intent.getStringExtra( "tourName"));
            Date d = null;
            if (intent.getStringExtra( "sStartDay").equals( "null" ))
            {
                d = new Date(0);
            }
            else {
                d = new Date(Long.parseLong(intent.getStringExtra( "sStartDay")));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            startDay.setText( sdf.format( d ));
            d = new Date(Long.parseLong(intent.getStringExtra( "sStartDay")));
            endDay.setText( sdf.format( d ) );
            adult.setText(String.valueOf(intent.getIntExtra( "adult",0)));
            children.setText(String.valueOf(intent.getIntExtra( "children",0))); ;
            minC.setText(String.valueOf(intent.getLongExtra( "minC",0)));
            maxC.setText(String.valueOf(intent.getLongExtra( "maxC",0)));



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

            img = findViewById(R.id.imageView);
            chooseImg = findViewById( R.id.textViewChooseImage );

            chooseImg.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Xin phep may anh va thu vien anh
                    if (checkThuVienAnhPermisstion())
                    {
                        Intent intent = new Intent( Intent.ACTION_PICK );
                        intent.setType( "image/*" );
                        startActivityForResult( intent,Request_Code_Img );
                    }

                }
            } );


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

                        Intent intent = new Intent(getBaseContext(), updateTourNext.class);
                        intent.putExtra("token", token);
                        intent.putExtra("id", id);
                       Log.i("IDD",id);
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


        private boolean checkThuVienAnhPermisstion(){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions( this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},Request_Code_Img);
                }
                else {
                    ActivityCompat.requestPermissions( this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},Request_Code_Img);
                }
                return false;
            } else {
                return true;
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if(requestCode == Request_Code_Img && resultCode == RESULT_OK && data != null){
                Uri uri = data.getData();

                try {
                    InputStream inputStream = getContentResolver().openInputStream( uri );
                    Bitmap bitmap = BitmapFactory.decodeStream( inputStream );
                    img.setImageBitmap( bitmap );
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            super.onActivityResult( requestCode, resultCode, data );
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
